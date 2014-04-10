package cz.fi.android.formulamanager.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by Majo on 9. 4. 2014.
 */
public class CreationActivity extends ActionBarActivity {
//TODO this activity probably need to be redone, using fragments
    private static final String TAG = "cz.fi.android.formulamanager.CreationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creation_layout);
        Intent intent = getIntent();

        boolean edit = intent.getBooleanExtra(FormulaListFragment.F_EDIT, false);
        TextView tv = (TextView) findViewById(R.id.message);

        if(edit){
            String name = intent.getStringExtra(FormulaListFragment.F_LABEL);
            int pos = intent.getIntExtra(FormulaListFragment.F_INDEX,-1);
            tv.setText(pos + " " + name + " will be edited.");
        } else {
            tv.setText("New formula will be created");
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
}
