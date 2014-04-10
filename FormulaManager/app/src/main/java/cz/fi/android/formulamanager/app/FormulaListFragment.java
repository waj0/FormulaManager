package cz.fi.android.formulamanager.app;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by Majo on 9. 4. 2014.
 */
public class FormulaListFragment extends ListFragment {
    public static final String F_INDEX = "cz.fi.android.formulamanager.position";
    public static final String F_LABEL = "cz.fi.android.formulamanager.label";
    public static final String F_EDIT = "cz.fi.android.formulamanager.edit";
    public static final String F_CHOICE = "cz.fi.android.formulamanager.choice";
    private static final String TAG = "cz.fi.android.formulamanager.FormulaListFragment";

    private boolean mDualPane;
    private int mCurCheckPosition = 0;

    //TODO delete dummy values
    public static final String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
            "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
            "Linux", "OS/2", "BlackberryA", "WebOSA", "UbuntuA", "Windows7A", "Max OS XA", "BlackberryB", "WebOSB", "UbuntuB", "Windows7B", "Max OS XB" };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //we got some new action bar icons here
        setHasOptionsMenu(true);

        //TODO reset list here
        FormulaAdapter adapter = new FormulaAdapter(getActivity(), Arrays.asList(values));
        setListAdapter(adapter);

        //listener for long click on list item - starts editation of formula
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, position + " long click");
                Intent intent = new Intent(getActivity(), CreationActivity.class);
                TextView textView = (TextView) view.findViewById(R.id.label);

                String message = textView.getText().toString();
                intent.putExtra(F_LABEL, message);
                intent.putExtra(F_INDEX, position);
                //put true so creation activity edit existing formula
                intent.putExtra(F_EDIT, true);
                //TODO put stuff in intent for creation activity for editing formula
                startActivity(intent);

                return true;
            }
        });

        //listener for short click on list item - starts calculation
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, i + " short click");
                showDetails(i);
            }
        });

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt(F_CHOICE, 0);
        }

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showDetails(mCurCheckPosition);
        }
    }

    // inflating new buttons to action bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.formula_list_menu, menu);

        //set search button properly
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)search.getActionView();

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(getActivity().getApplicationContext(), MainActivity.class)));

        // backwards compatible listeners to reset list after search widget was closed
        MenuItemCompat.setOnActionExpandListener(search, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //TODO reset list here
                setListAdapter(new FormulaAdapter(getActivity(),Arrays.asList(values)));
                return true;
            }
        });

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
        if (!mDualPane) {
            menu.removeItem(R.id.action_share);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_new:
                Intent intent = new Intent(getActivity(), CreationActivity.class);
                //put false so creation activity create new formula
                intent.putExtra(FormulaListFragment.F_EDIT, false);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save current choice
        outState.putInt(F_CHOICE, mCurCheckPosition);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index) {
        mCurCheckPosition = index;

        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(index, true);

            // Check what fragment is currently shown, replace if needed.
            CalculationFragment details = (CalculationFragment) getFragmentManager().findFragmentById(R.id.details);
            if (details == null || details.getShownIndex() != index) {
                // Make new fragment to show this selection.
                details = CalculationFragment.newInstance(index);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (index == 0) {
                    ft.replace(R.id.details, details);
                } else {
                    //ft.replace(R.id.a_item, details);
                    ft.replace(R.id.details, details);
                }
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            //TODO put stuff in intent for calculation activity
            Intent intent = new Intent();
            intent.setClass(getActivity(), CalculationActivity.class);
            intent.putExtra(F_INDEX, index);
            startActivity(intent);
        }
    }
}
