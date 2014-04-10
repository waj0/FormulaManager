package cz.fi.android.formulamanager.app;


import android.support.v4.app.ListFragment;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Arrays;

public class MainActivity extends ActionBarActivity {
    private static final String TAG = "cz.fi.android.formulamanager.MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // method for searching formulas
    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        Log.i(TAG, "search triggered");
        handleIntent(intent);
    }

    // method for searching formulas
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i(TAG, "search triggered "+ query);
            doSearch(query);
        }
    }

    private void doSearch(String queryStr) {
        //TODO search in DB, may use content provider
        FormulaAdapter adapter = new FormulaAdapter(this, Arrays.asList(FormulaListFragment.values).subList(1, 3));
        ((ListFragment) getSupportFragmentManager().findFragmentById(R.id.formulas)).setListAdapter(adapter);
        // get a Cursor, prepare the ListAdapter
        // and set it
    }
}
