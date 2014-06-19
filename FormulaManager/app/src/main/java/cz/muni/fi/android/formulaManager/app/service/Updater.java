package cz.muni.fi.android.formulaManager.app.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.muni.fi.android.formulaManager.app.database.FormulaSQLHelper;
import cz.muni.fi.android.formulaManager.app.entity.Formula;
import cz.muni.fi.android.formulaManager.app.entity.Parameter;

/**
 * Created by Slavomír on 22.5.2014.
 */
public class Updater extends IntentService {
    private static final String TAG = "cz.muni.fi.android.formulaManager.app.service.Updater";
    public Updater() {
        super("Updater");
    }
    static final String JSON_FORMULA_NAME ="name";
    static final String JSON_FORMULA_RAW ="rawFormula";
    static final String JSON_FORMULA_SVG ="svgFormula";
    static final String JSON_FORMULA_CATEGORY ="category";
    static final String JSON_FORMULA_PARAMETERS ="parameters";
    static final String JSON_FORMULA_PARAMETER_NAME ="name";
    static final String JSON_FORMULA_PARAMETER_TYPE ="type";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.d(TAG, "Starting updater");
        try
        {
            final URL url = new URL("http://www.fi.muni.cz/~xkrupa2/PV239/formula.json");
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == 200)
            {
                Log.d(TAG, "File opened");
                final BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                try
                {
                    final StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = r.readLine()) != null)
                    {
                        sb.append(line);
                    }

                    try
                    {
                        final JSONArray formulas = new JSONArray(sb.toString());

                        List<Formula> entityList = new ArrayList<Formula>(formulas.length());
                        Set<ContentValues> categories = new HashSet<ContentValues>(formulas.length());
                        for (int i = 0; i < formulas.length(); i++)
                        {
                            JSONObject formulaJson = formulas.getJSONObject(i);
                            Formula formula = parseFormula(formulaJson);
                            Cursor formulasCursor = getContentResolver()
                                    .query(FormulaSQLHelper.Formulas.contentUri(), null, FormulaSQLHelper.Formulas.NAME + "=?", new String[]{formula.getName()}, null);
                            if(formulasCursor.getCount()!= 0){
                                Log.d(TAG, "Conflict name "+ formula.getName() );
                               continue;
                            }

                            Cursor categoriesCursor = getContentResolver()
                                    .query(FormulaSQLHelper.Categories.contentUri(), null, FormulaSQLHelper.Categories.NAME + "=?", new String[]{formula.getCategory()}, null);
                            if(categoriesCursor.getCount() == 0) {
                                Log.d(TAG, "Adding category " + formula.getCategory());
                                ContentValues category = new ContentValues();
                                category.put(FormulaSQLHelper.Categories.NAME, formula.getCategory());
                                categories.add(category);
                            }
                            entityList.add(formula);
                            Log.d(TAG, "Formula " + formula.getName() + " will be added");
                        }

                        int categoryCount = getContentResolver().bulkInsert(FormulaSQLHelper.Categories.contentUri(), categories.toArray(new ContentValues[categories.size()]));
                        Log.d(TAG, "Inserted " +  categoryCount + " categories.");
                        int formulaCount = insertFormulas(entityList);
                        Log.d(TAG, "Inserted " +  formulaCount + " formulas.");
                        Intent updateIntent = new Intent("update-UI");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(updateIntent);
                    } catch (JSONException   e)
                    {
                        Log.d(TAG, "json error " + e.getMessage());
                    } finally
                    {

                    }
                } finally
                {
                    r.close();
                }
            } else {
                Log.d(TAG, "Wrong response from server");
            }
        } catch (IOException e)
        {
            Log.d(TAG, "IO error " + e.getMessage());
        }
    }


    private static Formula parseFormula(JSONObject formulaJson) throws JSONException {
        Formula formula = new Formula();
        formula.setId(null);
        formula.setName(formulaJson.getString(JSON_FORMULA_NAME));
        formula.setRawFormula(formulaJson.getString(JSON_FORMULA_RAW));
        formula.setSvgFormula(formulaJson.getString(JSON_FORMULA_SVG));
        formula.setCategory(formulaJson.getString(JSON_FORMULA_CATEGORY));
        formula.setFavorite(false);
        List<Parameter> parameterList;
        if(formulaJson.has(JSON_FORMULA_PARAMETERS)) {
            JSONArray parameters = formulaJson.getJSONArray(JSON_FORMULA_PARAMETERS);
            parameterList = parseParameters(parameters);
        }
        else {
            parameterList = new ArrayList<Parameter>();
        }
        formula.setParams(parameterList);
        return formula;
    }

    private static List<Parameter> parseParameters(JSONArray parameters) throws JSONException {
        List<Parameter> parameterList = new ArrayList<Parameter>(parameters.length());
        for (int i = 0; i < parameters.length(); i++){
            JSONObject jsonParameter = parameters.getJSONObject(i);
            Parameter parameter = new Parameter();
            parameter.setName(jsonParameter.getString(JSON_FORMULA_PARAMETER_NAME));
            Parameter.ParameterType parameterType = Parameter.ParameterType.fromIntValue(jsonParameter.getInt(JSON_FORMULA_PARAMETER_TYPE));
            parameter.setType(parameterType);
            parameterList.add(parameter);
        }
    return parameterList;
    }

    private void addParametersToDatabase(Formula f) {

        for (Parameter param : f.getParams()) {
            ContentValues values = new ContentValues();
            values.put(FormulaSQLHelper.Parameters.NAME, param.getName());
            values.put(FormulaSQLHelper.Parameters.TYPE, param.getType().getIntValue());
            values.put(FormulaSQLHelper.Parameters.FORMULA_ID, f.getId());
            getContentResolver().insert(FormulaSQLHelper.Parameters.contentUri(), values);

        }
    }
    private int insertFormulas(List<Formula> formulas) {
        int count =0;
        for (Formula formula : formulas) {
            Uri uri = getContentResolver().insert(FormulaSQLHelper.Formulas.contentUri(), Formula.getValues(formula));
            long newId = getIdOfInsertedObject(uri);
            if(newId != 0){
                count++;
            }
            formula.setId(newId);
            addParametersToDatabase(formula);
        }

        return count;
    }
    private long getIdOfInsertedObject(Uri uri) {
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        c.moveToFirst();
        return c.getLong(c.getColumnIndex(FormulaSQLHelper.Formulas._ID));
    }
}
