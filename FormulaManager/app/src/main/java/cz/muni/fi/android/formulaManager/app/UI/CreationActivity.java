package cz.muni.fi.android.formulaManager.app.UI;


import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cz.muni.fi.android.formulaManager.app.Formula;
import cz.muni.fi.android.formulaManager.app.Parameter;
import cz.muni.fi.android.formulaManager.app.R;
import cz.muni.fi.android.formulaManager.app.database.FormulaSQLHelper;


/**
 * Created by Majo on 9. 4. 2014.
 */
public class CreationActivity extends ActionBarActivity implements CreateParamDialog.CreateParamDialogListener {

    private static final String TAG = "cz.fi.android.formulamanager.CreationActivity";

    private FormulaSQLHelper sqlHelper = FormulaSQLHelper.getInstance(this);

    private GridLayout paramGrid;

    private DrawerLayout functionListDrawer;
    private ExpandableListView drawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    List<String> functionGroups = new ArrayList<String>();
    HashMap<String, List<String>> functionItems = new HashMap<String, List<String>>();

    private Formula newFormula;

    private String[] functions = {"Relacne operatory","Zaokruhlenie","Goniometria"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creation_layout);

        prepareListData();
        /* init navigation drawer */
        functionListDrawer = (DrawerLayout) findViewById(R.id.creation_drawer);
        drawerList = (ExpandableListView) findViewById(R.id.function_list);
        drawerList.setAdapter(new ExpandableListAdapter(this,functionGroups,functionItems));

        drawerList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return false;
            }
        });

        drawerList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        functionGroups.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        drawerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        functionGroups.get(groupPosition)
                                + " : "
                                + functionItems.get(
                                functionGroups.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

        newFormula = new Formula();
        Button save = (Button) findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((EditText) findViewById(R.id.formula_name)).getText().toString();
                String rawFormula = ((EditText) findViewById(R.id.formulaText)).getText().toString();

                if(name.equals("") || name.equals("")) {
                    Toast.makeText(getApplicationContext(), "You need to fill name and formula!", Toast.LENGTH_LONG).show();
                    return;
                }

                newFormula.setName(name);
                newFormula.setRawFormula(rawFormula);

                if (getIntent().getBooleanExtra(FormulaListFragment.F_EDIT, false)) {
                    //TODO update formula and its params in DB. and somehow reset list in main activity
                }
                //TODO add formula and its params to DB. and somehow reset list in main activity, set category to null if it is user own formula
                //TODO put also params to DB and set them (to objects) proper id - see onDialogPositiveClick()

                addFormulaToDatabase(newFormula);

                //TODO display toast
                sqlHelper.close();
                CreationActivity.this.finish();
            }
        });
    }

    private void addParametersToDatabase(Formula formula) {

        long lastInsertedFormulaID = getLastID();

        for(Parameter param : formula.getParams()) {
            ContentValues values = new ContentValues();
            Log.i(CreationActivity.TAG,param.getName() + " " + param.getType());
            values.put(FormulaSQLHelper.Parameters.NAME,param.getName());
            values.put(FormulaSQLHelper.Parameters.TYPE, param.getType().toString());

            values.put(FormulaSQLHelper.Parameters.FORMULA_ID,formula.getId());

            if(existParameterInDB(param)) {
                Log.i(TAG,"Update param: " + formula.getId());
                //values.put(FormulaSQLHelper.Parameters.FORMULA_ID,formula.getId());
                //sqlHelper.getWritableDatabase().update(FormulaSQLHelper.TABLE_PARAMETERS,values,
                //        FormulaSQLHelper.Parameters._ID + "=" + param.getId(),null);
                int rows = getContentResolver().update(FormulaSQLHelper.Parameters.contentItemUri(param.getId()),values,null,null);

            } else if (updateFormula()) {
                Log.i(TAG,"Insert param exist formula: " + lastInsertedFormulaID);
//                values.put(FormulaSQLHelper.Parameters.FORMULA_ID,formula.getId());
//                sqlHelper.getWritableDatabase().insert(FormulaSQLHelper.TABLE_PARAMETERS, null, values);
                Uri uri = getContentResolver().insert(FormulaSQLHelper.Parameters.contentUri(),values);
            } else {
                Log.i(TAG,"Insert param dont exists formula: " + newFormula.getId());
//                values.put(FormulaSQLHelper.Parameters.FORMULA_ID,lastInsertedFormulaID);
//                sqlHelper.getWritableDatabase().insert(FormulaSQLHelper.TABLE_PARAMETERS, null, values);
                Uri uri = getContentResolver().insert(FormulaSQLHelper.Parameters.contentUri(),values);
            }
        }
    }

    private boolean existParameterInDB(Parameter param) {
        //if parameter is not in DB it has negative ID
        return param.getId() <= 0 ? false : true;
    }

    private void addFormulaToDatabase(Formula newFormula) {

        ContentValues values = new ContentValues();

        values.put(FormulaSQLHelper.Formulas.NAME, newFormula.getName());
        values.put(FormulaSQLHelper.Formulas.RAW_FORMULA, newFormula.getRawFormula());

        if(updateFormula()) {
            Log.i(TAG,"Formula ID: " + newFormula.getId());
            //sqlHelper.getWritableDatabase().update(FormulaSQLHelper.TABLE_FORMULAS,values,
            //        FormulaSQLHelper.Formulas._ID + "=" + newFormula.getId(),null);
            int rows = getContentResolver().update(FormulaSQLHelper.Formulas.contentItemUri(newFormula.getId()), Formula.getValues(newFormula), null, null);
        } else {
            //sqlHelper.getWritableDatabase().insert(FormulaSQLHelper.TABLE_FORMULAS, null, values);

            //returned uri contains new id
            Uri uri = getContentResolver().insert(FormulaSQLHelper.Formulas.contentUri(), Formula.getValues(newFormula));
            Cursor c = getContentResolver().query(uri,null,null,null,null);
            c.moveToFirst();
            long newId = c.getLong(c.getColumnIndex(FormulaSQLHelper.Formulas._ID));
            newFormula.setId(newId);

        }

        addParametersToDatabase(newFormula);
    }

    private boolean updateFormula() {
        return getIntent().getBooleanExtra(FormulaListFragment.F_EDIT, false);
    }

    private long getLastID() {
        return sqlHelper.getLastID(sqlHelper.getWritableDatabase());
    }

    @Override
    protected void onStart() {
        super.onStart();
        paramGrid = (GridLayout) findViewById(R.id.params_grid);
        paramGrid.setOrientation(GridLayout.HORIZONTAL);

        //TODO long text in buttons is problem, set columns from dispay width and/or max button width
        paramGrid.setColumnCount(3);

        ImageButton addParam = (ImageButton) findViewById(R.id.add_button);
        addParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoticeDialog();
            }
        });

        Intent intent = getIntent();
        boolean edit = intent.getBooleanExtra(FormulaListFragment.F_EDIT, false);
        //TODO decide whether editing only name or whole formula
        if (edit) {

            newFormula = intent.getParcelableExtra(FormulaListFragment.FORMULA);

            ((EditText) findViewById(R.id.formula_name)).setText(newFormula.getName());
            ((EditText) findViewById(R.id.formulaText)).setText(newFormula.getRawFormula());
            for (Parameter p : newFormula.getParams()) {
                addParamButton(p);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new CreateParamDialog();
        dialog.show(getSupportFragmentManager(), "CreateParamDialog");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        CreateParamDialog cpDialog = (CreateParamDialog) dialog;

        Parameter p = new Parameter();
        //set temporary id to parameter TODO replace it with id from DB later
        p.setId(((long) newFormula.getParams().size() * (-1)));
        Log.i(CreationActivity.TAG, cpDialog.getSelectedType().toString());
        p.setType(cpDialog.getSelectedType());
        p.setName(cpDialog.getParamName());

        newFormula.addParam(p);
        addParamButton(p);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    private void addParamButton(Parameter p) {
        if (p == null) {
            return;
        }
        //TODO set onclick onlongclick listeners to this button , maybe put upper bound on width
        Button b = new Button(this);
        b.setText(p.getName());
        int index = paramGrid.getChildCount() - 1;
        b.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        paramGrid.addView(b, index);
    }

    private void prepareListData() {

        String[] groups = getResources().getStringArray(R.array.function_groups);

        for(String group:groups) {
            functionGroups.add(group);
        }

        // Adding child data
        List<String> constants = new ArrayList<String>(
                Arrays.asList(getResources().getStringArray(R.array.constants)));
        List<String> goniometricalOperators = new ArrayList<String>(
                Arrays.asList(getResources().getStringArray(R.array.trigonometric_operators)));
        List<String> relationalOperators = new ArrayList<String>(
                Arrays.asList(getResources().getStringArray(R.array.relational_operators)));

        functionItems.put(functionGroups.get(0), constants);
        functionItems.put(functionGroups.get(1), goniometricalOperators);
        functionItems.put(functionGroups.get(2), relationalOperators);
    }
}
