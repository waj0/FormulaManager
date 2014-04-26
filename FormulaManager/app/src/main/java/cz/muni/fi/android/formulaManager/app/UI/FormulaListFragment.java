package cz.muni.fi.android.formulaManager.app.UI;


import android.app.ActivityOptions;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import cz.muni.fi.android.formulaManager.app.Formula;
import cz.muni.fi.android.formulaManager.app.FormulaAdapter;
import cz.muni.fi.android.formulaManager.app.Parameter;
import cz.muni.fi.android.formulaManager.app.R;
import cz.muni.fi.android.formulaManager.app.database.FormulaSQLHelper;
import de.timroes.android.listview.EnhancedListView;


/**
 * Created by Majo on 9. 4. 2014.
 */
public class FormulaListFragment extends Fragment implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor>{
    public static final String FORMULA = "cz.muni.fi.android.formulaManager.formula";
    public static final String F_EDIT = "cz.muni.fi.android.formulaManager.edit";

    private static final String TAG = "cz.muni.fi.android.formulaManager.FormulaListFragment";

    private static final String CHOICE = "cz.muni.fi.android.formulaManager.choice";
    private static final String NAME_FILTER = "cz.muni.fi.android.formulaManager.namefilter";
    private static final String CATS_FILTER = "cz.muni.fi.android.formulaManager.catsfilter";

    private boolean mDualPane;
    private int mCurCheckPosition = 0;

    //TODO categories: 0 favourites; get names from DB somehow
    public static String[] categoryNames = {"basic", "geom", "stuff", "nuclear science"};
    private static final int NUMBER_OF_CATEGORIES = 4;
    private boolean[] mCurCategoryFilter;
    String mCurNameFilter = null;




    private FormulaAdapter mAdapter;
    private EnhancedListView mListView;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    public FormulaListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //TODO reset list here, get values from DB
        Cursor c = getActivity().getContentResolver().query(FormulaSQLHelper.Formulas.contentUri(), null, null, null, null);
        mAdapter = new FormulaAdapter(getActivity(),c, true);

        mCurCategoryFilter = new boolean[NUMBER_OF_CATEGORIES];

        mDrawerLayout = (DrawerLayout) inflater.inflate(R.layout.formula_list_layout, container);
        return mDrawerLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //we got some new action bar icons here
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt(CHOICE, 0);
            mCurNameFilter = (String) savedInstanceState.getSerializable(NAME_FILTER);
            mCurCategoryFilter = savedInstanceState.getBooleanArray(CATS_FILTER);
        }

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        //                                   //
        //### drawer and categories setup ###//
        //                                   //

        //add checkBox for every category to drawer
        LinearLayout drawerList = (LinearLayout) getActivity().findViewById(R.id.drawer_list);
        ViewGroup.MarginLayoutParams mlp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mlp.setMargins(0,0,0,8);
        for(int i = 0; i < NUMBER_OF_CATEGORIES; i++) {
            final int pos = i;
            mCurCategoryFilter[i] = true;

            CheckBox category = new CheckBox(getActivity());
            category.setText(categoryNames[i]);
            category.setChecked(true);
            category.setLayoutParams(mlp);
            category.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    mCurCategoryFilter[pos] = b;
                    getActivity().getSupportLoaderManager().restartLoader(0, null, FormulaListFragment.this);
                }
            });
            drawerList.addView(category);
        }

        //set drawer listeners
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close ) {
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
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();

        //enable drawer icon on activity
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getActivity().getSupportLoaderManager().initLoader(0, null, this);

        //                       //
        //### listView setup  ###//
        //                       //

        mListView = (EnhancedListView) getActivity().findViewById(R.id.list);
        mListView.setAdapter(mAdapter);

        //listener for long click on list item - starts editation of formula
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, position + " long click");

                Formula f = mAdapter.getItem(position);
                fetchParams(f);

                Intent intent = new Intent(getActivity(), CreationActivity.class);
                intent.putExtra(FORMULA, f);
                //put true so creation activity edit existing formula
                intent.putExtra(F_EDIT, true);

                //set animation
                //Bundle scaleBundle = ActivityOptions.makeScaleUpAnimation(view,0,0,view.getWidth(), view.getHeight()).toBundle();

                startActivity(intent/*, scaleBundle*/);
                return true;
            }
        });

        //listener for short click on list item - starts calculation
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i(TAG, position + " short click");
                if(mDualPane) {
                    mListView.setItemChecked(position, true);
                }
                showDetails(position);
            }
        });

        //swipe to delete w/ undo setup
        mListView.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(EnhancedListView enhancedListView, final int position) {
                final Formula item = mAdapter.getItem(position);
                long id = item.getId();

                //TODO flicker problem see https://github.com/timroes/EnhancedListView/issues/10
                Uri uri = FormulaSQLHelper.Formulas.contentItemUri(id);
                getActivity().getContentResolver().delete(uri, null, null);

                //mResolver.delete(FormulaProvider.Formulas.contentUri(), FormulaProvider.Formulas._ID, new String[] {item.getId()+""});
                //mAdapter.remove(position);
                return new EnhancedListView.Undoable() {
                    @Override
                    public void undo() {
                        getActivity().getContentResolver().
                                insert(FormulaSQLHelper.Formulas.contentUri(), Formula.getValues(item));
                    }
                };
            }
        });

        //set other properties
        mListView.setUndoStyle(EnhancedListView.UndoStyle.MULTILEVEL_POPUP);
        mListView.enableSwipeToDismiss();
        mListView.setSwipingLayout(R.id.row);

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            mListView.setSwipeDirection(EnhancedListView.SwipeDirection.START);
            // Make sure our UI is in the correct state.
            showDetails(mCurCheckPosition);
        } else {
            mListView.setSwipeDirection(EnhancedListView.SwipeDirection.BOTH);
            mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            mListView.clearChoices();
            /*for (int i = 0; i < mListView.getCount(); i++) //TODO selection problem
                mListView.setItemChecked(i, false);
            mListView.post(new Runnable() {
                @Override
                public void run() {
                    mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                }
            });
            mAdapter.notifyDataSetChanged();*/
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStop() {
        if(mListView != null) {
            mListView.discardUndo();
        }
        super.onStop();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        // Don't care about this. just hide keyboard
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        mCurNameFilter = !TextUtils.isEmpty(newText) ? newText : null;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    // inflating new buttons to action bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.formula_list_menu, menu);

        //set search button properly
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)search.getActionView();

        // Associate searchable configuration with the SearchView
        //SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(getActivity().getApplicationContext(), MainActivity.class)));
        searchView.setOnQueryTextListener(this);

        // backwards compatible listeners to reset list after search widget was closed
        MenuItemCompat.setOnActionExpandListener(search, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //reset items in mListView after search is closed
                mCurNameFilter = null;
                getActivity().getSupportLoaderManager().restartLoader(0, null, FormulaListFragment.this);
                return true;
            }
        });

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI. //TODO share button problem
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

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch(item.getItemId()) {
            case R.id.action_new: {
                Intent intent = new Intent(getActivity(), CreationActivity.class);
                //put false so creation activity create new formula
                intent.putExtra(FormulaListFragment.F_EDIT, false);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //disable buttons if drawer is visible
        boolean drawer = mDrawerLayout.isDrawerVisible(Gravity.LEFT);
        menu.findItem(R.id.action_settings).setVisible(!drawer);
        menu.findItem(R.id.action_search).setVisible(!drawer);
        menu.findItem(R.id.action_new).setVisible(!drawer);
        MenuItem m = menu.findItem(R.id.action_share);
        if(m != null) {
            m.setVisible(!drawer);
            //Log.i(TAG, "prepare menu list fragment disable share " + m.isVisible());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save current choice
        outState.putInt(CHOICE, mCurCheckPosition);
        outState.putSerializable(NAME_FILTER, mCurNameFilter);
        outState.putBooleanArray(CATS_FILTER, mCurCategoryFilter);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    private void showDetails(int index) {
        mCurCheckPosition = index;

        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            mListView.setItemChecked(index, true);

            // Check what fragment is currently shown, replace if needed.
            CalculationFragment details = (CalculationFragment) getFragmentManager().findFragmentById(R.id.details);
            if (details == null || details.getShownId() != mAdapter.getItemId(index)) {
                Formula f = mAdapter.getItem(index);
                fetchParams(f);

                // Make new fragment to show this selection.
                details = new CalculationFragment(f);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.details, details);

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

    private void fetchParams(Formula f){
        Cursor c = getActivity().getContentResolver().query(
                FormulaSQLHelper.Parameters.contentUri(),
                null,
                FormulaSQLHelper.Parameters.FORMULA_ID + " = " + f.getId(),
                null,null);
        c.moveToFirst();
        while (!c.isAfterLast())  {
            Parameter p = new Parameter();
            p.setId(c.getLong(c.getColumnIndex(FormulaSQLHelper.Parameters._ID)));
            p.setName(c.getString(c.getColumnIndex(FormulaSQLHelper.Parameters.NAME)));
            p.setType(c.getInt(c.getColumnIndex(FormulaSQLHelper.Parameters.TYPE)));
            f.getParams().add(p);
            c.moveToNext();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.

        StringBuilder selection = new StringBuilder();
        String[] selectionArgs = null;
        String and = " and ";
        if (mCurNameFilter != null) {
            //searching for names - via SearchView
            //TODO maybe use fulltext search fts3
            selection.append(FormulaSQLHelper.Formulas.NAME + " LIKE ?");
            selectionArgs = new String[]{"%" + mCurNameFilter + "%"};
        }
        int categoryCount = countActiveCategories();
        if(categoryCount != NUMBER_OF_CATEGORIES) {
            //filtering for categories
            if(mCurNameFilter != null) {
                selection.append(and);
            }
            StringBuilder chosenCategories = new StringBuilder(" (");
            int commas = categoryCount - 1;
            for(int i=0; i<NUMBER_OF_CATEGORIES; i++) {
                if(mCurCategoryFilter[i]){
                    chosenCategories.append(" '");
                    chosenCategories.append(categoryNames[i]);
                    chosenCategories.append("' ");
                    if(commas != 0) {
                        chosenCategories.append(", ");
                        commas--;
                    }
                }
            }
            chosenCategories.append(") ");

            selection.append(FormulaSQLHelper.Formulas.CATEGORY + " IN ");
            selection.append(chosenCategories.toString());
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        //Log.i(TAG, selection.toString());
        return new CursorLoader(getActivity(), FormulaSQLHelper.Formulas.contentUri(), null, selection.toString(), selectionArgs, null);
    }

    private int countActiveCategories(){
        int count = 0;
        for (Boolean b : mCurCategoryFilter) {
            if (b) count++;
        }
        return count;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
    }
}
