package cz.muni.fi.android.formulaManager.app.UI;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import cz.muni.fi.android.formulaManager.app.Formula;


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
}
