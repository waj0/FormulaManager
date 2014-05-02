package cz.muni.fi.android.formulaManager.app.UI;


import android.app.ActionBar;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

    private GridLayout paramGrid;
    private DrawerLayout functionListDrawer;
    private ExpandableListView drawerList;

    List<String> functionGroups = new ArrayList<String>();
    HashMap<String, List<String>> functionItems = new HashMap<String, List<String>>();

    private Formula newFormula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creation_layout);

        prepareListData();
        initNavigationDrawer();

        newFormula = new Formula();
        Button save = (Button) findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ((EditText) findViewById(R.id.formula_name)).getText().toString();
                String rawFormula = ((EditText) findViewById(R.id.formulaText)).getText().toString();

                if (name.equals("") || rawFormula.equals("")) {
                    Toast.makeText(getApplicationContext(), "You need to fill name and formula!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (existFormulaWithName(name)) {
                    Log.i(TAG,"Formula exist");
                    Toast.makeText(getApplicationContext(), "Formula with given name already exist. Please change the name of formula.", Toast.LENGTH_LONG).show();
                    return;
                }

                newFormula.setName(name);
                newFormula.setRawFormula(rawFormula);

                addFormulaToDatabase();

                CreationActivity.this.finish();
            }
        });
    }

    private void initNavigationDrawer() {
        functionListDrawer = (DrawerLayout) findViewById(cz.muni.fi.android.formulaManager.app.R.id.creation_drawer);
        drawerList = (ExpandableListView) findViewById(R.id.function_list);
        drawerList.setAdapter(new ExpandableListAdapter(this, functionGroups, functionItems));

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
                                childPosition), Toast.LENGTH_SHORT
                )
                        .show();
                return false;
            }
        });
    }

    private void addParametersToDatabase() {

        for (Parameter param : newFormula.getParams()) {
            ContentValues values = new ContentValues();

            values.put(FormulaSQLHelper.Parameters.NAME, param.getName());
            values.put(FormulaSQLHelper.Parameters.TYPE, param.getType().toString());
            values.put(FormulaSQLHelper.Parameters.FORMULA_ID, newFormula.getId());

            if (existParameterInDB(param)) {
                getContentResolver().update(FormulaSQLHelper.Parameters.contentItemUri(param.getId()), values, null, null);
            } else {
                getContentResolver().insert(FormulaSQLHelper.Parameters.contentUri(), values);
            }
        }
    }

    private boolean existFormulaWithName(String name) {
        Cursor c = getContentResolver().
                query(FormulaSQLHelper.Formulas.contentUri(), null, "name=?", new String[]{name}, null);

        return c.getCount() == 0 ? false : true;
    }

    private boolean existParameterInDB(Parameter param) {
        return param.getId() <= 0 ? false : true;
    }

    private void addFormulaToDatabase() {

        ContentValues values = new ContentValues();

        values.put(FormulaSQLHelper.Formulas.NAME, newFormula.getName());
        values.put(FormulaSQLHelper.Formulas.RAW_FORMULA, newFormula.getRawFormula());

        if (editFormula()) {
            getContentResolver().update(FormulaSQLHelper.Formulas.contentItemUri(newFormula.getId()), Formula.getValues(newFormula), null, null);
        } else {
            insertFormulaToDB();
        }

        addParametersToDatabase();
    }

    private void insertFormulaToDB() {
        Uri uri = getContentResolver().insert(FormulaSQLHelper.Formulas.contentUri(), Formula.getValues(newFormula));
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        c.moveToFirst();
        long newId = c.getLong(c.getColumnIndex(FormulaSQLHelper.Formulas._ID));
        newFormula.setId(newId);
    }

    private boolean editFormula() {
        return getIntent().getBooleanExtra(FormulaListFragment.F_EDIT, false);
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

        if (editFormula()) {

            newFormula = getIntent().getParcelableExtra(FormulaListFragment.FORMULA);

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

        for (String group : groups) {
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
