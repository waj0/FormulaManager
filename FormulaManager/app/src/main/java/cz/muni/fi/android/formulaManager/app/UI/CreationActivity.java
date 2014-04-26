package cz.muni.fi.android.formulaManager.app.UI;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.muni.fi.android.formulaManager.app.Formula;
import cz.muni.fi.android.formulaManager.app.Parameter;
import cz.muni.fi.android.formulaManager.app.R;


/**
 * Created by Majo on 9. 4. 2014.
 */
public class CreationActivity extends ActionBarActivity implements CreateParamDialog.CreateParamDialogListener {

    private static final String TAG = "cz.fi.android.formulamanager.CreationActivity";

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
                newFormula.setName(((EditText) findViewById(R.id.formula_name)).getText().toString());
                newFormula.setRawFormula(((EditText) findViewById(R.id.formulaText)).getText().toString());

                if (getIntent().getBooleanExtra(FormulaListFragment.F_EDIT, false)) {
                    //TODO update formula and its params in DB. and somehow reset list in main activity
                }
                //TODO add formula and its params to DB. and somehow reset list in main activity

                //TODO display toast
                CreationActivity.this.finish();
            }
        });
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
        p.setId(((long) newFormula.getParams().size()));
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
        paramGrid.addView(b, index);
    }

    private void prepareListData() {

        // Adding child data
        functionGroups.add("Konstanty");
        functionGroups.add("Relacne operatory");
        functionGroups.add("Goniometricke operatory");

        // Adding child data
        List<String> constants = new ArrayList<String>();
        constants.add("E");
        constants.add("PI");
        constants.add("Degree");
        constants.add("False");
        constants.add("True");
        constants.add("GoldenRation");
        constants.add("Infinity");

        List<String> goniometricalOperators = new ArrayList<String>();
        goniometricalOperators.add("Sine");
        goniometricalOperators.add("Cosine");
        goniometricalOperators.add("Tangent");
        goniometricalOperators.add("Cotangent");
        goniometricalOperators.add("Arcus sine");
        goniometricalOperators.add("Arcus cosine");
        goniometricalOperators.add("Arcus tangent");
        goniometricalOperators.add("Arcus contangent");

        List<String> relationalOperators = new ArrayList<String>();
        relationalOperators.add("Equal to");
        relationalOperators.add("Not equal to");
        relationalOperators.add("Greater than");
        relationalOperators.add("Less than");
        relationalOperators.add("Greater than or equal to");
        relationalOperators.add("Less than or equal to");

        functionItems.put(functionGroups.get(0), constants);
        functionItems.put(functionGroups.get(1), goniometricalOperators);
        functionItems.put(functionGroups.get(2), relationalOperators);
    }
}
