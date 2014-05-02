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

import cz.muni.fi.android.formulaManager.app.entity.Formula;
import cz.muni.fi.android.formulaManager.app.entity.Parameter;
import cz.muni.fi.android.formulaManager.app.R;
import cz.muni.fi.android.formulaManager.app.database.FormulaSQLHelper;
import cz.muni.fi.android.formulaManager.app.manager.FormulaManager;
import cz.muni.fi.android.formulaManager.app.manager.FormulaManagerImpl;


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

    private Formula formula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creation_layout);

        prepareListData();
        initNavigationDrawer();

        formula = new Formula();
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

                if (existFormulaWithName(name) && !editFormula()) {
                    Log.i(TAG, "Formula exist");
                    Toast.makeText(getApplicationContext(), "Formula with given name already exist. Please change the name of formula.", Toast.LENGTH_LONG).show();
                    return;
                }

                formula.setName(name);
                formula.setRawFormula(rawFormula);

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

        for (Parameter param : formula.getParams()) {
            ContentValues values = new ContentValues();

            values.put(FormulaSQLHelper.Parameters.NAME, param.getName());
            values.put(FormulaSQLHelper.Parameters.TYPE, param.getType().toString());
            values.put(FormulaSQLHelper.Parameters.FORMULA_ID, formula.getId());

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

        if (param.getId() == null) {
            return false;
        }

        Cursor c = getContentResolver().
                query(FormulaSQLHelper.Parameters.contentItemUri(param.getId()), null, null, null, null);

        return c.getCount() == 0 ? false : true;
    }

    private void addFormulaToDatabase() {

        ContentValues values = new ContentValues();

        values.put(FormulaSQLHelper.Formulas.NAME, formula.getName());
        values.put(FormulaSQLHelper.Formulas.RAW_FORMULA, formula.getRawFormula());

        if (editFormula()) {
            getContentResolver().update(FormulaSQLHelper.Formulas.contentItemUri(formula.getId()), Formula.getValues(formula), null, null);
        } else {
            insertFormulaToDB();
        }

        addParametersToDatabase();
    }

    private void insertFormulaToDB() {
        Uri uri = getContentResolver().insert(FormulaSQLHelper.Formulas.contentUri(), Formula.getValues(formula));
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        c.moveToFirst();
        long newId = c.getLong(c.getColumnIndex(FormulaSQLHelper.Formulas._ID));
        formula.setId(newId);
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
                showNoticeDialog(null, -1);
            }
        });

        if (editFormula()) {

            formula = getIntent().getParcelableExtra(FormulaListFragment.FORMULA);

            ((EditText) findViewById(R.id.formula_name)).setText(formula.getName());
            ((EditText) findViewById(R.id.formulaText)).setText(formula.getRawFormula());
            for (Parameter p : formula.getParams()) {
                addParamButton(p);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }


    private void showNoticeDialog(Parameter parameter, int index) {
        DialogFragment dialog = new CreateParamDialog();

        Bundle arguments = new Bundle();
        arguments.putParcelable("parameter", parameter);
        arguments.putInt("buttonIndex", index);

        dialog.setArguments(arguments);
        dialog.show(getSupportFragmentManager(), "CreateParamDialog");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        CreateParamDialog cpDialog = (CreateParamDialog) dialog;

        FormulaManager manager = new FormulaManagerImpl();

        Parameter p = new Parameter();
        p.setType(cpDialog.getSelectedType());
        p.setName(cpDialog.getParamName());

        Parameter editedParameter = dialog.getArguments().getParcelable("parameter");

        if (editedParameter != null) {
            p.setId(editedParameter.getId());
            manager.editParameter(formula, p);

            Log.i(TAG, "Position of button: " + dialog.getArguments().getInt("buttonIndex"));
            Button button = (Button) paramGrid.getChildAt(dialog.getArguments().getInt("buttonIndex"));
            button.setText(p.getName());

        } else {
            manager.addParameter(formula, p);
            addParamButton(p);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    private void addParamButton(final Parameter p) {
        if (p == null) {
            return;
        }
        //TODO set onclick onlongclick listeners to this button , maybe put upper bound on width
        Button b = new Button(this);
        b.setText(p.getName());

        final int index = paramGrid.getChildCount() - 1;

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoticeDialog(p, index);
            }
        });


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
