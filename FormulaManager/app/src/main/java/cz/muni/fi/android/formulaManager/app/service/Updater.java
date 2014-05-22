package cz.muni.fi.android.formulaManager.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
        Log.e(TAG,"started");
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

        try
        {
            final URL url = new URL("http://www.fi.muni.cz/~xkrupa2/PV239/formula.json");
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == 200)
            {
                Log.e(TAG, "File opened");
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
                        Log.e(TAG, "formula:" + formulas.length());
                        for (int i = 0; i < formulas.length(); i++)
                        {
                            JSONObject formulaJson = formulas.getJSONObject(i);
                            Formula formula = parseFormula(formulaJson);
                            Cursor c = getContentResolver()
                                    .query(FormulaSQLHelper.Formulas.contentUri(), null, FormulaSQLHelper.Formulas.NAME + "=?", new String[]{formula.getName()}, null);
                            if(c.getCount()!= 0){
                                Log.e(TAG, "Name already exists "+ formula.getName() );
                                break;
                            }
                            Uri uri = getContentResolver().insert(FormulaSQLHelper.Formulas.contentUri(), Formula.getValues(formula));
                            getContentResolver().notifyChange(uri,null);
                            Log.e(TAG, "insertion done" +uri);
                            //TODO insert all parameters
                        }



                    } catch (JSONException   e)
                    {
                        Log.e(TAG, "json error " + e.getMessage());
                    } finally
                    {
//                        db.endTransaction();
                    }
                } finally
                {
                    r.close();
                }
            }
        } catch (IOException e)
        {
            Log.e(TAG, "IO error " + e.getMessage());
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
