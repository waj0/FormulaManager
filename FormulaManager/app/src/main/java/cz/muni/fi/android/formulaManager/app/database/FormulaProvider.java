package cz.muni.fi.android.formulaManager.app.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

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

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, Formulas.PATH_FORMULAS, FORMULAS);
        sUriMatcher.addURI(AUTHORITY, Formulas.PATH_FORMULAS + "/*", FORMULAS_ITEM);
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
                qb.appendWhere(Formulas._ID + " = " + uri.getLastPathSegment());
                break;
            }
            case FORMULAS: {
                if (sortOrder == null) {
                    defaultOrderBy = Formulas.DEFAULT_ORDER_BY;
                } else {
                    defaultOrderBy = sortOrder;
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported uri: " + uri);
        }
        qb.setTables(FormulaSQLHelper.TABLE_FORMULAS);

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
            case FORMULAS: return Formulas.CONTENT_TYPE;
            case FORMULAS_ITEM: return Formulas.CONTENT_ITEM_TYPE;
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
                noteUri = ContentUris.withAppendedId(Formulas.contentUri(), rowId);
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
        final int type = sUriMatcher.match(uri);
        switch (type) {
            case FORMULAS: {
                rowsDeleted = db.delete(FormulaSQLHelper.TABLE_FORMULAS, selection, selectionArgs);
                //Log.i(TAG, "remove id: " + selectionArgs[0] + " - " + rowsDeleted);
                break;
            }
            case  FORMULAS_ITEM: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(FormulaSQLHelper.TABLE_FORMULAS,
                            Formulas._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = db.delete(FormulaSQLHelper.TABLE_FORMULAS,
                            Formulas._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            }
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        SQLiteDatabase db = FormulaSQLHelper.getInstance(getContext()).getWritableDatabase();
        int rowsUpdated = 0;
        final int type = sUriMatcher.match(uri);
        switch (type) {
            case FORMULAS: {
                rowsUpdated = db.update(FormulaSQLHelper.TABLE_FORMULAS,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case FORMULAS_ITEM:{
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(FormulaSQLHelper.TABLE_FORMULAS,
                            contentValues,
                            Formulas._ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = db.update(FormulaSQLHelper.TABLE_FORMULAS,
                            contentValues,
                            Formulas._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            }
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        //TODO add formula columns here
        String[] available = { Formulas._ID, Formulas.NAME,
                Formulas.RAWFORMULA, Formulas.CATEGORY,
                Formulas.VERSION, Formulas.FAVORITE};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

    interface FormulaColumns {
        //TODO add formula columns here
        String NAME = "name";
        String RAWFORMULA = "rawFormula";
        String CATEGORY = "category";
        //String SVGFORMULA = "svgFormula";
        String VERSION = "version";
        String FAVORITE = "favorite";
    }

    public static class Formulas implements BaseColumns, FormulaColumns {
        /**
         * uri path for the list of formulas
         */
        static final String PATH_FORMULAS = "formulas";

        /**
         * mime/content type for list of formulas
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cz.muni.fi.android.formulaManager.app.formula";

        /**
         * mime/content type for formula in the list of formulas
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cz.muni.fi.android.formulaManager.app.formula";

        /**
         * ordered by {@link cz.muni.fi.android.formulaManager.app.database.FormulaProvider.FormulaColumns#NAME}
         */
        public static final String DEFAULT_ORDER_BY = NAME;

        private Formulas()
        {
        }

        /**
         * Gets uri for list of formulas.
         */
        public static Uri contentUri()
        {
            final Uri.Builder b = AUTHORITY_URI.buildUpon();
            b.appendPath(PATH_FORMULAS);
            return b.build();
        }

        /**
         * Gets uri for one formula in the list of formulas by given {@code id}.
         */
        public static Uri contentItemUri(long id)
        {
            final Uri.Builder b = AUTHORITY_URI.buildUpon();
            b.appendPath(PATH_FORMULAS);
            b.appendPath(String.valueOf(id));
            return b.build();
        }

    }
}
