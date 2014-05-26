package cz.muni.fi.android.formulaManager.app.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.muni.fi.android.formulaManager.app.UI.FormulaListFragment;
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
    static final String JsonFormulaName="name";
    static final String JsonFormulaRaw="rawFormula";
    static final String JsonFormulaSvg="svgFormula";
    static final String JsonFormulaCategory="category";
    static final String JsonFormulaParameters="parameters";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {//TODO synchronization with listfragment
        Log.d(TAG, "Starting service ");
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
                        Log.d(TAG, "Inserting " + formulas.length() + " formulas.");
                        List<ContentValues> entityList = new ArrayList<ContentValues>(formulas.length());
                        for (int i = 0; i < formulas.length(); i++)
                        {
                            JSONObject formulaJson = formulas.getJSONObject(i);
                            Formula formula = parseFormula(formulaJson);
                            Cursor c = getContentResolver()
                                    .query(FormulaSQLHelper.Formulas.contentUri(), null, FormulaSQLHelper.Formulas.NAME + "=?", new String[]{formula.getName()}, null);
                            if(c.getCount()!= 0){
                                Log.d(TAG, "Conflict name "+ formula.getName() );
                                continue;
                            }
                            entityList.add(Formula.getValues(formula));
                            Log.d(TAG, "Formula " + formula + " will be added" );
                            //TODO insert all parameters
                        }

                        int inserted = getContentResolver().bulkInsert(FormulaSQLHelper.Formulas.contentUri(), entityList.toArray(new ContentValues[entityList.size()]));
                        //TODO maybe some check if all added
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
            //catch new broadcast receiver - updating
            Log.d(TAG, "IO error " + e.getMessage());
        }
    }

    private static Formula parseFormula(JSONObject formulaJson) throws JSONException {
        Formula formula = new Formula();
        formula.setId(null);
        formula.setName(formulaJson.getString(JsonFormulaName));
        formula.setRawFormula(formulaJson.getString(JsonFormulaRaw));
        formula.setSvgFormula(formulaJson.getString(JsonFormulaSvg));
        formula.setCategory(formulaJson.getString(JsonFormulaCategory));
        formula.setFavorite(false);
        List<Parameter> parameterList;
        if(formulaJson.has(JsonFormulaParameters)) {
            JSONObject parameters = formulaJson.getJSONObject(JsonFormulaParameters);
            parameterList = parseParameters(parameters);
        }
        else {
            parameterList = new ArrayList<Parameter>();
        }
        formula.setParams(parameterList);
        return formula;
    }

    private static List<Parameter> parseParameters(JSONObject parameters) {
        //TODO implementation
        return null;
    }
}
