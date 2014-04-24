package cz.muni.fi.android.formulaManager.app.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import cz.muni.fi.android.formulaManager.app.Formula;
import cz.muni.fi.android.formulaManager.app.R;

/**
 * Created by Majo on 10. 4. 2014.
 */
public class CalculationFragment extends Fragment {

    private static final String TAG = "cz.fi.android.formulamanager.CalculationFragment";

    private Formula formula;

    public CalculationFragment() {
        formula = new Formula();
    }

    public CalculationFragment(Formula formula) {
        this.formula = formula;
    }

    //TODO button to share is visible if only list is on screen - vertical list fragment - we should remove it somehow in calculation fragment lifecycle

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //this fragment has menu items
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.calculation, menu);
        //Log.i(TAG, "create menu calc fragment");
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

    //TODO redo this with id of formula not with index
    public long getShownId() {
        return formula.getId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }

        ScrollView scroller = new ScrollView(getActivity());
        //TODO display proper formula stuff here
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                4, getActivity().getResources().getDisplayMetrics());
        TextView text = new TextView(getActivity());

        text.setPadding(padding, padding, padding, padding);
        text.setText(formula.getName());
        text.append("\n" + formula.getParamsAsString());
        text.append("\n" + formula.getRawFormula());

        scroller.addView(text);
        return scroller;
    }
}
