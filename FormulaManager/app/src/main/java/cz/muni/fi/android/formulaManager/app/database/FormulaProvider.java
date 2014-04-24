package cz.muni.fi.android.formulaManager.app.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Majo on 22. 4. 2014.
 */
public class FormulaProvider extends ContentProvider {

    public static final String TAG = "cz.muni.fi.android.formulaManager.app.FormulaProvider";

    public static final String AUTHORITY = "cz.muni.fi.android.formulaManager.app";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    private static final int FORMULAS = 1;
    private static final int FORMULAS_ITEM = 2;
    private static final int PARAMETERS = 3;
    private static final int PARAMETERS_ITEM = 4;

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, FormulaSQLHelper.Formulas.PATH_FORMULAS, FORMULAS);
        sUriMatcher.addURI(AUTHORITY, FormulaSQLHelper.Formulas.PATH_FORMULAS + "/*", FORMULAS_ITEM);
        sUriMatcher.addURI(AUTHORITY, FormulaSQLHelper.Parameters.PATH_PARAMETERS, PARAMETERS);
        sUriMatcher.addURI(AUTHORITY, FormulaSQLHelper.Parameters.PATH_PARAMETERS + "/*", PARAMETERS_ITEM);
    }

    private FormulaSQLHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = FormulaSQLHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        String defaultOrderBy = null;
        String groupBy = null;

        final int type = sUriMatcher.match(uri);
        switch (type) {
            case FORMULAS_ITEM: {
                qb.appendWhere(FormulaSQLHelper.Formulas._ID + " = " + uri.getLastPathSegment());
                qb.setTables(FormulaSQLHelper.TABLE_FORMULAS);
                break;
            }
            case FORMULAS: {
                if (sortOrder == null) {
                    defaultOrderBy = FormulaSQLHelper.Formulas.DEFAULT_ORDER_BY;
                } else {
                    defaultOrderBy = sortOrder;
                }
                qb.setTables(FormulaSQLHelper.TABLE_FORMULAS);
                break;
            }
            case PARAMETERS_ITEM:{
                qb.appendWhere(FormulaSQLHelper.Parameters._ID + " = " + uri.getLastPathSegment());
                qb.setTables(FormulaSQLHelper.TABLE_PARAMETERS);
                break;
            }
            case PARAMETERS:{
                if (sortOrder == null) {
                    defaultOrderBy = FormulaSQLHelper.Parameters.DEFAULT_ORDER_BY;
                } else {
                    defaultOrderBy = sortOrder;
                }
                qb.setTables(FormulaSQLHelper.TABLE_PARAMETERS);
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported uri: " + uri);
        }

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = defaultOrderBy;
        }

        Cursor c = qb.query(mDatabaseHelper.getWritableDatabase(), projection, selection, selectionArgs, groupBy, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }


    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case FORMULAS: return FormulaSQLHelper.Formulas.CONTENT_TYPE;
            case FORMULAS_ITEM: return FormulaSQLHelper.Formulas.CONTENT_ITEM_TYPE;
            default: return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Validate the requested uri
        long rowId = 0;
        Uri noteUri = null;
        SQLiteDatabase db = FormulaSQLHelper.getInstance(getContext()).getWritableDatabase();
        final int type = sUriMatcher.match(uri);
        switch (type){
            case FORMULAS: {
                rowId = db.insert(FormulaSQLHelper.TABLE_FORMULAS, null, values);
                //Log.i(TAG, "formula rowID:" + rowId);
                noteUri = ContentUris.withAppendedId(FormulaSQLHelper.Formulas.contentUri(), rowId);
                break;
            }
            case PARAMETERS: {
                rowId = db.insert(FormulaSQLHelper.TABLE_PARAMETERS, null, values);
                //Log.i(TAG, "formula rowID:" + rowId);
                noteUri = ContentUris.withAppendedId(FormulaSQLHelper.Parameters.contentUri(), rowId);
                break;
            }
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(noteUri, null);
        return noteUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Validate the requested uri
        SQLiteDatabase db = FormulaSQLHelper.getInstance(getContext()).getWritableDatabase();
        int rowsDeleted = 0;
        String table = null;
        String id = null;
        final int type = sUriMatcher.match(uri);
        switch (type) {
            case FORMULAS_ITEM: {
                //update selection
                id = uri.getLastPathSegment();
                selection = FormulaSQLHelper.Formulas._ID + "=" + id +
                        (TextUtils.isEmpty(selection) ? "" :  (" and " + selection));
                // no break;
            }
            case FORMULAS: {
                //set table
                table = FormulaSQLHelper.TABLE_FORMULAS;
                break;
            }
            case  PARAMETERS_ITEM: {
                //update selection
                id = uri.getLastPathSegment();
                selection = FormulaSQLHelper.Parameters._ID + "=" + id +
                        (TextUtils.isEmpty(selection) ? "" :  (" and " + selection));
                // no break;
            }
            case PARAMETERS: {
                //set table
                table = FormulaSQLHelper.TABLE_PARAMETERS;
                break;
            }
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }
        //run command and return
        rowsDeleted = db.delete(table,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // Validate the requested uri
        SQLiteDatabase db = FormulaSQLHelper.getInstance(getContext()).getWritableDatabase();
        int rowsUpdated = 0;
        String table = null;
        String id = null;
        final int type = sUriMatcher.match(uri);
        switch (type) {
            case FORMULAS_ITEM:{
                //update selection
                id = uri.getLastPathSegment();
                selection = FormulaSQLHelper.Formulas._ID + "=" + id +
                        (TextUtils.isEmpty(selection) ? "" :  (" and " + selection));
                // no break;
            }
            case FORMULAS: {
                //set table
                table = FormulaSQLHelper.TABLE_FORMULAS;
                break;
            }
            case PARAMETERS_ITEM:{
                //update selection
                id = uri.getLastPathSegment();
                selection = FormulaSQLHelper.Parameters._ID + "=" + id +
                        (TextUtils.isEmpty(selection) ? "" :  (" and " + selection));
                // no break;
            }
            case PARAMETERS: {
                //set table
                table = FormulaSQLHelper.TABLE_PARAMETERS;
                break;
            }
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }
        //run command and return
        rowsUpdated = db.update(table, contentValues, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        //TODO add formula columns here
        String[] available = { FormulaSQLHelper.Formulas._ID, FormulaSQLHelper.Formulas.NAME,
                FormulaSQLHelper.Formulas.RAWFORMULA, FormulaSQLHelper.Formulas.CATEGORY,
                FormulaSQLHelper.Formulas.VERSION, FormulaSQLHelper.Formulas.FAVORITE};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
