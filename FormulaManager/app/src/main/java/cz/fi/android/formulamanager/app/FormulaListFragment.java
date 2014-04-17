package cz.fi.android.formulamanager.app;


import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;

import de.timroes.android.listview.EnhancedListView;


/**
 * Created by Majo on 9. 4. 2014.
 */
public class FormulaListFragment extends Fragment {
    public static final String FORMULA = "cz.fi.android.formulamanager.formula";
    public static final String F_EDIT = "cz.fi.android.formulamanager.edit";
    public static final String F_CHOICE = "cz.fi.android.formulamanager.choice";
    private static final String TAG = "cz.fi.android.formulamanager.FormulaListFragment";

    private boolean mDualPane;
    private int mCurCheckPosition = 0;

    private FormulaAdapter mAdapter; //TODO change to better adapter
    private EnhancedListView mListView;
    private DrawerLayout mDrawerLayout;

    private boolean[] checkedCategories;

    public FormulaListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mDrawerLayout = (DrawerLayout) inflater.inflate(R.layout.formula_list_layout, null);
        //TODO reset list here, get values from DB
        mAdapter = new FormulaAdapter(getActivity());
        checkedCategories = new boolean[3];
        return mDrawerLayout;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //we got some new action bar icons here
        setHasOptionsMenu(true);

        mDrawerLayout.setDrawerListener(new ActionBarDrawerToggle(getActivity(), mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().getActionBar().setTitle(R.string.label_categories);
                getActivity().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().getActionBar().setTitle(R.string.app_name);
                getActivity().invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

            }
        });



        CheckBox category1 = (CheckBox) getActivity().findViewById(R.id.check_cat1);
        category1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkedCategories[0] = b;
            }
        });
        CheckBox category2 = (CheckBox) getActivity().findViewById(R.id.check_cat2);
        category2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkedCategories[1] = b;
            }
        });
        CheckBox category3 = (CheckBox) getActivity().findViewById(R.id.check_cat3);
        category3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkedCategories[2] = b;
            }
        });

        mListView = (EnhancedListView) getActivity().findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        //listener for long click on list item - starts editation of formula
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, position + " long click");
                Formula f = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), CreationActivity.class);
                //put formula to edit into intent
                intent.putExtra(FORMULA, f);
                //put true so creation activity edit existing formula
                intent.putExtra(F_EDIT, true);
                startActivity(intent);
                return true;
            }
        });

        //listener for short click on list item - starts calculation
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i(TAG, position + " short click");
                mListView.setItemChecked(position, true);
                showDetails(position);
            }
        });

        mListView.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(EnhancedListView enhancedListView, final int position) {
                final Formula item = mAdapter.getItem(position);


                mAdapter.remove(position);
                return new EnhancedListView.Undoable() {
                    @Override
                    public void undo() {
                        mAdapter.insert(position, item);
                    }
                };
            }
        });

        mListView.setUndoStyle(EnhancedListView.UndoStyle.MULTILEVEL_POPUP);
        mListView.enableSwipeToDismiss();
        mListView.setSwipeDirection(EnhancedListView.SwipeDirection.START);
        mListView.setSwipingLayout(R.id.row);

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
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showDetails(mCurCheckPosition);
        }
    }


    @Override
    public void onStop() {
        if(mListView != null) {
            mListView.discardUndo();
        }
        super.onStop();
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
                //TODO reset list here get values from DB
                mAdapter.resetItems();
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
                return true;
            case R.id.action_done:
                mDrawerLayout.closeDrawers();
                applyCategories();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean drawer = mDrawerLayout.isDrawerVisible(Gravity.LEFT);
        menu.findItem(R.id.action_settings).setVisible(!drawer);
        menu.findItem(R.id.action_search).setVisible(!drawer);
        menu.findItem(R.id.action_new).setVisible(!drawer);
        menu.findItem(R.id.action_done).setVisible(drawer);
        MenuItem m = menu.findItem(R.id.action_share);
        if(m != null) {
            m.setVisible(!drawer);
            Log.i(TAG, "prepare menu list fragment disable share " + m.isVisible());
        }
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
            mListView.setItemChecked(index, true);

            // Check what fragment is currently shown, replace if needed.
            CalculationFragment details = (CalculationFragment) getFragmentManager().findFragmentById(R.id.details);
            if (details == null || details.getShownId() != mAdapter.getItem(index).getId()) {
                // Make new fragment to show this selection.
                details = new CalculationFragment(mAdapter.getItem(index));

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
            //TODO put stuff in intent for calculation activity, whole formula to intent
            Intent intent = new Intent();
            intent.setClass(getActivity(), CalculationActivity.class);
            intent.putExtra(FORMULA, mAdapter.getItem(index));
            startActivity(intent);
        }
    }

    private void applyCategories() {
        mAdapter.resetItems();
        //mListView.setAdapter(mAdapter);
        //TODO put only formulas from checked categories to list
    }
}
