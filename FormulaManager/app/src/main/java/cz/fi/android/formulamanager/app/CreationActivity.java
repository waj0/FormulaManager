package cz.fi.android.formulamanager.app;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.GridLayout;
import android.widget.EditText;
import android.widget.ImageButton;


/**
 * Created by Majo on 9. 4. 2014.
 */
public class CreationActivity extends ActionBarActivity  implements CreateParamDialog.CreateParamDialogListener {

    private static final String TAG = "cz.fi.android.formulamanager.CreationActivity";

    private GridLayout paramGrid;

    private Formula newFormula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creation_layout);

        newFormula = new Formula();
        Button save = (Button) findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newFormula.setName(((EditText) findViewById(R.id.formula_name)).getText().toString());
                newFormula.setRawFormula(((EditText) findViewById(R.id.formulaText)).getText().toString());


                if(getIntent().getBooleanExtra(FormulaListFragment.F_EDIT, false)) {
                    //TODO update formula and its params in DB.
                    for (Formula f : MainActivity.valuesFromDB) {
                        if(f.getId() == newFormula.getId()){
                            MainActivity.valuesFromDB.remove(f);
                            break;
                        }
                    }
                }
                //TODO add formula and its params to DB.
                MainActivity.valuesFromDB.add(newFormula);

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
        //paramGrid.setColumnCount();

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
        if(edit){

            Formula fromDB = intent.getParcelableExtra(FormulaListFragment.FORMULA);

/*            long id = intent.getLongExtra(FormulaListFragment.F_ID, -1);
            if(id == -1){
                return;
            }
            // TODO find formula in DB
            for(Formula f : MainActivity.valuesFromDB) {
                if(f.getId() == id) {
                    fromDB = f;
                    break;
                }
            }*/

            ((EditText)findViewById(R.id.formula_name)).setText(fromDB.getName());
            ((EditText)findViewById(R.id.formulaText)).setText(fromDB.getRawFormula());
            for(Parameter p : fromDB.getParams()) {
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
        if(p == null) {
            return;
        }
        //TODO set onclick onlongclick listeners to this button , maybe put upper bound on width
        Button b = new Button(this);
        b.setText(p.getName());
        int index = paramGrid.getChildCount()-1;
        paramGrid.addView(b,index);
    }
}
