package cz.muni.fi.android.formulaManager.app.UI;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import cz.muni.fi.android.formulaManager.app.entity.Formula;
import cz.muni.fi.android.formulaManager.app.R;


/**
 * Created by Majo on 9. 4. 2014.
 */
public class CalculationActivity extends ActionBarActivity {

    private static final String TAG = "cz.fi.android.formulamanager.CalculationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }
        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            CalculationFragment details = new CalculationFragment((Formula) getIntent().getParcelableExtra(FormulaListFragment.FORMULA));
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, details).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calculation, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_share:
                //TODO do stuff to share
                Log.i(TAG, "share this now");
        }
        return super.onOptionsItemSelected(item);
    }
}
