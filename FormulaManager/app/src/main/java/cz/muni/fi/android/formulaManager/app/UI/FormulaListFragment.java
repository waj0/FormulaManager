package cz.muni.fi.android.formulaManager.app.UI;


import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
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
import android.widget.TextView;
import android.widget.Toast;

import cz.muni.fi.android.formulaManager.app.entity.Formula;
import cz.muni.fi.android.formulaManager.app.entity.Parameter;
import cz.muni.fi.android.formulaManager.app.R;
import cz.muni.fi.android.formulaManager.app.database.FormulaSQLHelper;
import cz.muni.fi.android.formulaManager.app.service.Updater;
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

    private String[] categoryNames;
    private int categoriesCount;
    private boolean[] mCurCategoryFilter;
    String mCurNameFilter = null;

    private CheckedListener mFavOnCheckedChangeListener = new CheckedListener();

    private SimpleCursorAdapter mAdapter;
    private EnhancedListView mListView;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    protected BroadcastReceiver mConnectivityChangedReceiver;
    private BroadcastReceiver mUpdateGuiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // swap new cursor for
            Log.d(TAG, "Updating GUI");
            loadCategoriesFromDB();
            setDrawerCategories();
            getActivity().getSupportLoaderManager().restartLoader(0, null, FormulaListFragment.this);
            /*Cursor c = getActivity().getContentResolver().query(FormulaSQLHelper.Formulas.contentUri(),
                    null, null, null, null);
            mAdapter.swapCursor(c);*/
        }
    };

    public FormulaListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register to receive messages.
        // We are registering an observer (mUpdateGuiReceiver) to receive Intents
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(mUpdateGuiReceiver, new IntentFilter("update-UI"));
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);

        Cursor c = getActivity().getContentResolver().query(FormulaSQLHelper.Formulas.contentUri(),
                null, null, null, null);

        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.row_layout, c,
                new String[] { FormulaSQLHelper.Formulas.NAME, FormulaSQLHelper.Formulas.CATEGORY, FormulaSQLHelper.Formulas.FAVORITE},
                new int[] { R.id.formula_name, R.id.formula_category, R.id.formula_favorite},0);

        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                int nameColumn = cursor.getColumnIndex(FormulaSQLHelper.Formulas.NAME);
                int categoryColumn = cursor.getColumnIndex(FormulaSQLHelper.Formulas.CATEGORY);
                int favoriteColumn = cursor.getColumnIndex(FormulaSQLHelper.Formulas.FAVORITE);

                if (columnIndex == nameColumn || columnIndex == categoryColumn) {
                    TextView textView = (TextView) view;
                    textView.setText(cursor.getString(columnIndex));
                    return true;
                }

                if(columnIndex == favoriteColumn) {
                    view.setTag(cursor.getLong(cursor.getColumnIndex(FormulaSQLHelper.Formulas._ID)));
                    CheckBox chB = (CheckBox) view;

                    chB.setOnCheckedChangeListener(null);
                    int favorite = cursor.getInt(columnIndex);
                    chB.setChecked(favorite!=0);
                    chB.setOnCheckedChangeListener(mFavOnCheckedChangeListener);

                    return true;
                }
                return false;
            }
        });

        loadCategoriesFromDB();

        mDrawerLayout = (DrawerLayout) inflater.inflate(R.layout.formula_list_layout, container, false);
        return mDrawerLayout;
    }

    private void loadCategoriesFromDB() {
        Cursor cats = getActivity().getContentResolver().query(FormulaSQLHelper.Categories.contentUri(),null,null,null,null);
        categoriesCount = cats.getCount() + 1;//+1 for favorites
        mCurCategoryFilter = new boolean[categoriesCount];
        for(int i = 0; i<categoriesCount;i++){
            mCurCategoryFilter[i] = true;
        }
        categoryNames = new String[categoriesCount];
        categoryNames[0] = getString(cz.muni.fi.android.formulaManager.app.R.string.show_favs);

        cats.moveToFirst();
        int columnIndex = cats.getColumnIndex(FormulaSQLHelper.Categories.NAME);
        for(int i = 1; i < categoriesCount; i++) {
            categoryNames[i] = cats.getString(columnIndex);
            cats.moveToNext();
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mUpdateGuiReceiver);
        super.onDestroy();
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
        setDrawerCategories();

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
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        //listener for long click on list item - starts editation of formula
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, position + " long click");

                Formula f = getFormula(position);
                if(f.getCategory() == null || f.getCategory().isEmpty()){
                    Toast.makeText(getActivity(), "You can not edit default formulas", Toast.LENGTH_SHORT).show();
                    return true;
                }

                Intent intent = new Intent(getActivity(), CreationActivity.class);
                intent.putExtra(FORMULA, f);
                //put true so creation activity edit existing formula
                intent.putExtra(F_EDIT, true);

                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if(currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN) {
                    //set animation
                    ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0,
                            0, view.getWidth(), view.getHeight());
                    getActivity().startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }

                return true;
            }
        });

        //listener for short click on list item - starts calculation
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.i(TAG, position + " short click");

                mListView.setItemChecked(position, true);
                view.setSelected(true);
                View text = view.findViewById(R.id.formula_name);
                text.setSelected(true);
                ((TextView) text).setHorizontallyScrolling(true);

                showDetails(position);
            }
        });

        //swipe to delete w/ undo setup
        mListView.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(EnhancedListView enhancedListView, final int position) {
                final Formula item = getFormula(position);
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

        TextView empty = new TextView(getActivity());
        empty.setText(R.string.label_empty);
        mListView.setEmptyView(empty);


        mListView.setSwipeDirection(EnhancedListView.SwipeDirection.START);
        if (mDualPane) {
            showDetails(mCurCheckPosition);
        }
    }

    private void setDrawerCategories(){
        LinearLayout drawerList = (LinearLayout) getActivity().findViewById(R.id.drawer_list);
        LinearLayout.LayoutParams lptext = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        lptext.setMargins(0,0,0,8);
        lptext.weight = 1;
        LinearLayout.LayoutParams lpbox = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpbox.setMargins(0,0,0,8);

        ViewGroup.MarginLayoutParams mlpRow = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mlpRow.setMargins(0,0,0,8);

        for(int i = 0; i < categoriesCount; i++) {
            final int pos = i;
            boolean fill = (i!=0) ; //first should be false, others true
            mCurCategoryFilter[i] = fill;

            LinearLayout drawerRow = new LinearLayout(getActivity());
            drawerRow.setLayoutParams(mlpRow);
            drawerRow.setOrientation(LinearLayout.HORIZONTAL);
            drawerRow.setId(i);
            View check = mDrawerLayout.findViewById(i);

            if(check == null) {
                TextView text = new TextView(getActivity());
                text.setText(categoryNames[i]);
                text.setTextColor(Color.WHITE);
                text.setLayoutParams(lptext);
                drawerRow.addView(text);

                CheckBox category = new CheckBox(getActivity());
                category.setChecked(fill);
                category.setLayoutParams(lpbox);
                category.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mCurCategoryFilter[pos] = b;
                        getActivity().getSupportLoaderManager().restartLoader(0, null, FormulaListFragment.this);
                    }
                });
                drawerRow.addView(category);

                drawerList.addView(drawerRow);
            }
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
        getActivity().getSupportLoaderManager().restartLoader(0, null, FormulaListFragment.this);
        return true;
    }

    // inflating new buttons to action bar
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.formula_list_menu, menu);

        //set search button properly
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)search.getActionView();
        searchView.setQueryHint(getActivity().getString(R.string.search_hint));

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

                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if(currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN) {
                    //set animation
                    View button = getActivity().findViewById(R.id.action_new);
                    ActivityOptions options = ActivityOptions.makeScaleUpAnimation(button, 0,
                            0, button.getWidth(), button.getHeight());
                    getActivity().startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
                return true;
            }
            case R.id.action_refresh:
                if(mConnectivityChangedReceiver != null) {
                    // already waiting for connection.
                    return  true;
                }
                ConnectivityManager mgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = mgr.getActiveNetworkInfo();
                if (info == null || !info.isConnected())
                {

                    mConnectivityChangedReceiver = new BroadcastReceiver()
                    {
                        @Override
                        public void onReceive(Context context, Intent intent)
                        {
                            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                            if (!noConnectivity)
                            {
                                startUpdater();
                                try
                                {
                                    getActivity().unregisterReceiver(mConnectivityChangedReceiver);
                                }
                                catch (Exception e)
                                {
                                    Log.d(TAG, "Handled exception during unregistering receiver: " + e.getMessage());
                                }
                                mConnectivityChangedReceiver = null;
                                Log.d(TAG,"Connection found - update started!");
                            }

                        }
                    };
                    getActivity().registerReceiver(mConnectivityChangedReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
                } else {
                    startUpdater();
                }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startUpdater() {
        Log.d(TAG, "Update service will be started");
        final Intent service = new Intent(getActivity(), Updater.class);
        getActivity().startService(service);
        Toast.makeText(getActivity(),
                "Update started!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
        MenuItem m = menu.findItem(R.id.action_share);

        //disable buttons if drawer is visible
        boolean drawer = mDrawerLayout.isDrawerVisible(Gravity.LEFT);
        menu.findItem(R.id.action_settings).setVisible(!drawer);
        menu.findItem(R.id.action_search).setVisible(!drawer);
        menu.findItem(R.id.action_new).setVisible(!drawer);
        menu.findItem(R.id.action_refresh).setVisible(!drawer);
        if(m != null) {
            menu.findItem(R.id.action_share).setVisible(!drawer && mDualPane);
            Log.i(TAG, "disable/enable share button" + m.isVisible());
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
                Formula f = getFormula(index);

                // Make new fragment to show this selection.
                details = new CalculationFragment(f);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.pick_from_list, R.anim.slide_out);

                ft.replace(R.id.details, details);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), CalculationActivity.class);
            intent.putExtra(FORMULA, getFormula(index));

            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if(currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN) {
                //set animation
                View item = mListView.getChildAt(mCurCheckPosition);
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(item, 0,
                        0, item.getWidth(), item.getHeight());
                getActivity().startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }
            startActivity(intent);
        }
    }

    /**
     * get formula from listview at specific position
     * @param position position in listview
     * @return
     */
    private Formula getFormula(int position) {
        Formula f = new Formula();
        Cursor c = mAdapter.getCursor();
        if(c.getCount()==0) {
            return null;
        }
        c.moveToPosition(position);

        f.setId(c.getLong(c.getColumnIndex(FormulaSQLHelper.Formulas._ID)));
        f.setName(c.getString(c.getColumnIndex(FormulaSQLHelper.Formulas.NAME)));
        f.setRawFormula(c.getString(c.getColumnIndex(FormulaSQLHelper.Formulas.RAW_FORMULA)));
        f.setSvgFormula(c.getString(c.getColumnIndex(FormulaSQLHelper.Formulas.SVG_FORMULA)));
        f.setCategory(c.getString(c.getColumnIndex(FormulaSQLHelper.Formulas.CATEGORY)));
        f.setFavorite(c.getInt(c.getColumnIndex(FormulaSQLHelper.Formulas.FAVORITE)) != 0);

        fetchParams(f);

        return f;
    }


    /**
     * fetch parameters of formula from database
     * @param f formula
     */
    private void fetchParams(Formula f){
        Cursor c = getActivity().getContentResolver().query(
                FormulaSQLHelper.Parameters.contentUri(),
                null,FormulaSQLHelper.Parameters.FORMULA_ID +"=?",new String[] {f.getId().toString()},null);
        c.moveToFirst();

        while (!c.isAfterLast())  {
            Parameter p = new Parameter();
            p.setId(c.getLong(c.getColumnIndex(FormulaSQLHelper.Parameters._ID)));
            p.setName(c.getString(c.getColumnIndex(FormulaSQLHelper.Parameters.NAME)));
            p.setType(Parameter.ParameterType.fromIntValue(c.getInt(c.getColumnIndex(FormulaSQLHelper.Parameters.TYPE))));

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
        String or = " or ";
        if (mCurNameFilter != null) {
            //searching for names - via SearchView
            //TODO maybe use fulltext search fts3
            selection.append(FormulaSQLHelper.Formulas.NAME + " LIKE ?");
            selectionArgs = new String[]{"%" + mCurNameFilter + "%"};
        }

        if(mCurCategoryFilter[0]){
            //show only favorites
            if(selection.length() != 0) {
                selection.append(and);
            }
            selection.append(FormulaSQLHelper.Formulas.FAVORITE + " = 1 ");
        }
        int categoryCount = countActiveCategories();
        if(categoryCount != categoriesCount) {
            //filtering for categories
            if(selection.length() != 0) {
                selection.append(and);
            }
            StringBuilder chosenCategories = new StringBuilder(" (");
            int commas = categoryCount - 1;
            for(int i=0; i< categoriesCount; i++) {
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

            selection.append(" ( ");
            selection.append(FormulaSQLHelper.Formulas.CATEGORY + " IN ");
            selection.append(chosenCategories.toString());
            selection.append(or);
            selection.append(FormulaSQLHelper.Formulas.CATEGORY + " IS NULL ");
            selection.append(or);
            selection.append(FormulaSQLHelper.Formulas.CATEGORY + " = '' ");
            selection.append(" ) ");
        }
        //Log.e(TAG, selection.toString());

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
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

    private class CheckedListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean newState) {
            Log.i(TAG, "" + buttonView.getTag());
            long id = (Long) buttonView.getTag();
            ContentValues cv = new ContentValues();
            cv.put(FormulaSQLHelper.Formulas.FAVORITE, (newState?1:0));
            int rows = getActivity().getContentResolver().update(FormulaSQLHelper.Formulas.contentItemUri(id),cv,null,null);
            Log.i(TAG, "updated " + rows + " " + id);
        }
    }
}
