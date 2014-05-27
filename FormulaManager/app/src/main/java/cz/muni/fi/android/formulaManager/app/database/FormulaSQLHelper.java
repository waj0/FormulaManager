package cz.muni.fi.android.formulaManager.app.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Majo on 22. 4. 2014.
 */
public class FormulaSQLHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "formulaManager.db";
    public static final String TABLE_FORMULAS = "formulas";
    public static final String TABLE_PARAMETERS= "parameters";
    public static final String TABLE_CATEGORIES= "categories";
    public static final String TABLE_VERSION= "version";

    public static final String INDEX_PARAMETERS_FORMULA_ID = "i_parameters_formula_id";
    public static final String INDEX_FORMULA_CATEGORY = "i_formula_category";
    public static final String INDEX_FORMULA_NAME = "i_formula_name";

    //TODO add formula columns here,
    /**
     * simple table for list of categories
     */
    private static final String TABLE_CATEGORIES_CREATE =
            "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                    Categories.NAME + " text primary key" +
                    ");";

    /**
     * table for formulas, category must be value from categories table or null
     */
    private static final String TABLE_FORMULAS_CREATE =
            "CREATE TABLE " + TABLE_FORMULAS + " (" +
                    Formulas._ID + " integer primary key autoincrement, " +
                    Formulas.NAME + " text not null unique, " +
                    Formulas.RAW_FORMULA + " text not null, " +
                    Formulas.SVG_FORMULA + " text, " +
                    Formulas.CATEGORY + " text, " +
                    Formulas.FAVORITE + " integer, " +
                    " foreign key( " + Formulas.CATEGORY + " ) references " + TABLE_CATEGORIES +" ( " + Categories.NAME + " ) " +
                    ");";

    /**
     * table for parameters of formulas
     */
    private static final String TABLE_PARAMETERS_CREATE =
            "CREATE TABLE " + TABLE_PARAMETERS + " (" +
                    Parameters._ID + " integer primary key autoincrement, " +
                    Parameters.NAME + " text not null, " +
                    Parameters.TYPE + " integer, " +
                    Parameters.FORMULA_ID + " integer, " +
                    " foreign key( " + Parameters.FORMULA_ID + " ) references " + TABLE_FORMULAS +" ( " + Formulas._ID + " ), " +
                    "unique(" +Parameters.NAME + ", "+ Parameters.FORMULA_ID +") on conflict replace" +
                    ");";

    /**
     * index on formula_id (foreign keys) of parameters
     */
    private static final String INDEX_PARAMETERS_FORMULA_ID_CREATE =
            "CREATE INDEX " + INDEX_PARAMETERS_FORMULA_ID + " ON " +
                    TABLE_PARAMETERS + "(" + Parameters.FORMULA_ID +");";

    /**
     * index on formula names
     */
    private static final String INDEX_FORMULA_NAME_CREATE =
            "CREATE INDEX " + INDEX_FORMULA_NAME + " ON " +
                    TABLE_FORMULAS + "(" + Formulas.NAME +");";

    /**
     * index on formula categories
     */
    private static final String INDEX_FORMULA_CATEGORY_CREATE =
            "CREATE INDEX " + INDEX_FORMULA_CATEGORY + " ON " +
                    TABLE_FORMULAS + "(" + Formulas.CATEGORY +");";


    /**
     * simple table for version of database
     */
    private static final String TABLE_VERSION_CREATE =
            "CREATE TABLE " + TABLE_VERSION + " ( " +
                    " version integer " +
                    ");";

    private static final String TAG = "cz.muni.fi.android.formulaManager.app.database.FormulaSQLHelper";

    private static FormulaSQLHelper singleton;

    public static FormulaSQLHelper getInstance(Context context) {
        if (singleton == null) {
            singleton = new FormulaSQLHelper(context);
        }
        return singleton;
    }

    public FormulaSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //create tables
        sqLiteDatabase.execSQL(TABLE_CATEGORIES_CREATE);
        sqLiteDatabase.execSQL(TABLE_FORMULAS_CREATE);
        sqLiteDatabase.execSQL(TABLE_PARAMETERS_CREATE);
        sqLiteDatabase.execSQL(TABLE_VERSION_CREATE);
        String version = "INSERT INTO " + TABLE_VERSION +
                " ( version ) values ( 1 );";
        sqLiteDatabase.execSQL(version);

        //create indexes
        sqLiteDatabase.execSQL(INDEX_PARAMETERS_FORMULA_ID_CREATE);
        sqLiteDatabase.execSQL(INDEX_FORMULA_NAME_CREATE);
        sqLiteDatabase.execSQL(INDEX_FORMULA_CATEGORY_CREATE);

        //TODO delete dummy values
//        for (int i=0;i<4;i++){
//            String category = "INSERT INTO " + TABLE_CATEGORIES +
//                    " ( " + CategoryColumns.NAME + " ) values ( " +
//                    "'category" + i + "' " +
//                    " );" ;
//            sqLiteDatabase.execSQL(category);
//        }
//
//        for (int i=0;i<15;i++){
//            //insert formula
//            String formula = "INSERT INTO " + TABLE_FORMULAS +
//                    " ( " + Formulas.NAME + ", " +
//                    Formulas.RAW_FORMULA + ", " +
//                    Formulas.SVG_FORMULA + ", " +
//                    Formulas.CATEGORY + ", " +
//                    Formulas.FAVORITE + " " +
//                    ") values ( " +
//                    "'formula" + i + "', 'raw', 'svg', '" + "category" + (i+3)%4 + "', " + i%2 +
//                    " );" ;
//            sqLiteDatabase.execSQL(formula);
//
//            long lastId = getLastID(sqLiteDatabase);
//
//            //insert params
//            for(int j = 0; j<i%10;j++ ) {
//                String param = "INSERT INTO " + TABLE_PARAMETERS +
//                        " ( " + Parameters.NAME + ", " +
//                        Parameters.TYPE + ", " +
//                        Parameters.FORMULA_ID + " " +
//                        ") values ( " +
//                        "'par" + j + "', " + 1 + ", " + lastId +
//                        " );" ;
//                sqLiteDatabase.execSQL(param);
//            }
//        }
    }

    public long getLastID(SQLiteDatabase sqLiteDatabase) {

        Cursor c = sqLiteDatabase.rawQuery("SELECT last_insert_rowid()", null);
        c.moveToFirst();
        long lastId = c.getInt(0);
        c.close();
        c = null;
        Log.i(TAG," last id = " + lastId);

        return lastId;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int newVersion, int oldVersion) {
//
//        sqLiteDatabase.execSQL("drop table if exists "+ TABLE_FORMULAS);
//        sqLiteDatabase.execSQL("drop table if exists "+ TABLE_CATEGORIES);
//        sqLiteDatabase.execSQL("drop table if exists "+ TABLE_VERSION);
//        sqLiteDatabase.execSQL("drop table if exists "+ TABLE_PARAMETERS);
//    onCreate(sqLiteDatabase);
    }

    //
    // Columns definitions of all tables
    //

    interface FormulaColumns {
        //TODO add formula columns here
        String NAME = "name";
        String RAW_FORMULA = "rawFormula";
        String CATEGORY = "category";
        String SVG_FORMULA = "svgFormula";
        String FAVORITE = "favorite";
    }

    interface ParameterColumns {
        String NAME = "name";
        String TYPE = "type";
        String FORMULA_ID = "formula_id";
    }

    interface CategoryColumns {
        String NAME = "name";
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
         * ordered by {@link cz.muni.fi.android.formulaManager.app.database.FormulaSQLHelper.FormulaColumns#NAME}
         */
        public static final String DEFAULT_ORDER_BY = CATEGORY;

        private Formulas()
        {
        }

        /**
         * Gets uri for list of formulas.
         */
        public static Uri contentUri()
        {
            final Uri.Builder b = FormulaProvider.AUTHORITY_URI.buildUpon();
            b.appendPath(PATH_FORMULAS);
            return b.build();
        }

        /**
         * Gets uri for one formula in the list of formulas by given {@code id}.
         */
        public static Uri contentItemUri(long id)
        {
            final Uri.Builder b = FormulaProvider.AUTHORITY_URI.buildUpon();
            b.appendPath(PATH_FORMULAS);
            b.appendPath(String.valueOf(id));
            return b.build();
        }
    }

    public static class Parameters implements BaseColumns, ParameterColumns {
        /**
         * uri path for the list of parameters
         */
        static final String PATH_PARAMETERS = "parameters";

        /**
         * mime/content type for list of parameters
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cz.muni.fi.android.formulaManager.app.parameter";

        /**
         * mime/content type for parameter in the list of parameters
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.cz.muni.fi.android.formulaManager.app.parameter";

        /**
         * ordered by {@link cz.muni.fi.android.formulaManager.app.database.FormulaSQLHelper.ParameterColumns#NAME}
         */
        public static final String DEFAULT_ORDER_BY = NAME;

        private Parameters()
        {
        }

        /**
         * Gets uri for list of parameters.
         */
        public static Uri contentUri()
        {
            final Uri.Builder b = FormulaProvider.AUTHORITY_URI.buildUpon();
            b.appendPath(PATH_PARAMETERS);
            return b.build();
        }

        /**
         * Gets uri for one parameter in the list of parameters by given {@code id}.
         */
        public static Uri contentItemUri(long id)
        {
            final Uri.Builder b = FormulaProvider.AUTHORITY_URI.buildUpon();
            b.appendPath(PATH_PARAMETERS);
            b.appendPath(String.valueOf(id));
            return b.build();
        }
    }

    public static class Categories implements CategoryColumns {
        /**
         * uri path for the list of categories
         */
        static final String PATH_CATEGORIES = "categories";

        /**
         * mime/content type for list of categories
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.cz.muni.fi.android.formulaManager.app.category";

        /**
         * ordered by {@link cz.muni.fi.android.formulaManager.app.database.FormulaSQLHelper.CategoryColumns#NAME}
         */
        public static final String DEFAULT_ORDER_BY = NAME;

        private Categories()
        {
        }

        /**
         * Gets uri for list of categories.
         */
        public static Uri contentUri()
        {
            final Uri.Builder b = FormulaProvider.AUTHORITY_URI.buildUpon();
            b.appendPath(PATH_CATEGORIES);
            return b.build();
        }
    }
}
