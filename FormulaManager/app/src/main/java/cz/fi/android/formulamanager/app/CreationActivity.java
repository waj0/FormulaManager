package cz.fi.android.formulamanager.app;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
                newFormula.setParsable(((EditText) findViewById(R.id.formulaText)).getText().toString());

                //TODO add/update formula to DB.
                if(getIntent().getBooleanExtra(FormulaListFragment.F_EDIT, false)) {
                    for (Formula f : MainActivity.valuesFromDB) {
                        if(f.getId() == newFormula.getId()){
                            MainActivity.valuesFromDB.remove(f);
                            break;
                        }
                    }
                }
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

        //int index = paramGrid.getChildCount()-1;
        //TODO long text in buttons is problem :/
        //paramGrid.setColumnCount();//set columns from dispay width and max button width


        ImageButton addParam = (ImageButton) findViewById(R.id.add_button);
        addParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoticeDialog();
            }
        });

        Intent intent = getIntent();
        boolean edit = intent.getBooleanExtra(FormulaListFragment.F_EDIT, false);
        //TODO decide if editing only name of whole formula
        if(edit){
            Formula fromDB = null;
            long id = intent.getLongExtra(FormulaListFragment.F_ID, -1);
            if(id == -1){
                return;
            }
            // TODO find formula in DB
            for(Formula f : MainActivity.valuesFromDB) {
                if(f.getId() == id) {
                    fromDB = f;
                    break;
                }
            }
            ((EditText)findViewById(R.id.formula_name)).setText(fromDB.getName());
            ((EditText)findViewById(R.id.formulaText)).setText(fromDB.getParsable());
            int index = paramGrid.getChildCount()-1;
            for(Parameter p : fromDB.getParams()) {
                //TODO set listeners to this button  - onclick onlongclick, put upper bound on width use createButton()
                Button b = new Button(this);
                b.setText(p.getName());
                paramGrid.addView(b,index);
                index++;
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
        //TODO set listeners to this button - onclick onlongclick put upper bound on width use createButton()
        Button result = new Button(this);

        CreateParamDialog cpDialog = (CreateParamDialog) dialog;

        Parameter p = new Parameter();
        p.setType(cpDialog.getSelectedType());
        p.setName(cpDialog.getParamName());
        //TODO check if same parameter does not exist already
        newFormula.addParam(p);

        result.setText(cpDialog.getParamName());
        int index = paramGrid.getChildCount()-1;
        paramGrid.addView(result, index);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    // TODO create button
    private Button createButton(Parameter p) {
        if(p == null) {
            return null;
        }
        return null;
    }
}
