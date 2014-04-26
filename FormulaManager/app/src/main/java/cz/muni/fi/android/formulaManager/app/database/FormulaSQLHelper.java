package cz.muni.fi.android.formulaManager.app.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import cz.muni.fi.android.formulaManager.app.UI.FormulaListFragment;

/**
 * Created by Majo on 22. 4. 2014.
 */
public class FormulaSQLHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "formulaManager.db";
    public static final String TABLE_FORMULAS = "formulas";
    public static final String TABLE_PARAMETERS= "parameters";

    public static final String INDEX_FORMULA_ID_FK= "i_formula_id_fk";

    //TODO add formula columns here, add constraints on columns (NOT NULL to almost all of them), param type probably foreing key or reference
    //TODO create index on formulas' names, maybe on categories too
    private static final String TABLE_FORMULAS_CREATE =
            "CREATE TABLE " + TABLE_FORMULAS + " (" +
                    Formulas._ID + " integer primary key autoincrement, " +
                    Formulas.NAME + " text, " +
                    Formulas.RAWFORMULA + " text, " +
                    Formulas.CATEGORY + " text, " +
                    Formulas.FAVORITE + " integer, " +
                    Formulas.VERSION + " integer " +
                    ");";

    private static final String TABLE_PARAMETERS_CREATE =
            "CREATE TABLE " + TABLE_PARAMETERS + " (" +
                    Parameters._ID + " integer primary key autoincrement, " +
                    Parameters.NAME + " text, " +
                    Parameters.TYPE + " integer, " +
                    Parameters.FORMULA_ID + " integer, " +
                    " foreign key( " + Parameters.FORMULA_ID + " ) references " + TABLE_FORMULAS +" ( " + Formulas._ID + " ) " +
                    ");";

    private static final String INDEX_FORMULA_ID_FK_CREATE =
            "CREATE INDEX " + INDEX_FORMULA_ID_FK + " ON " +
                    TABLE_PARAMETERS + "(" + Parameters.FORMULA_ID +");";

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
        sqLiteDatabase.execSQL(TABLE_FORMULAS_CREATE);
        sqLiteDatabase.execSQL(TABLE_PARAMETERS_CREATE);
        sqLiteDatabase.execSQL(INDEX_FORMULA_ID_FK_CREATE);

        //TODO delete dummy values, add formula columns here
        for (int i=0;i<100;i++){
            //insert formula
            String formula = "INSERT INTO " + TABLE_FORMULAS +
                    " ( " + Formulas.NAME + ", " +
                    Formulas.RAWFORMULA + ", " +
                    Formulas.CATEGORY + ", " +
                    Formulas.FAVORITE + ", " +
                    Formulas.VERSION + " " +
                    ") values ( " +
                    "'formula" + i + "', 'raw', '" + FormulaListFragment.categoryNames[i%4] + "', " + i%2 +", 1 " +
                    " );" ;
            sqLiteDatabase.execSQL(formula);

            //get last id
            Cursor c = sqLiteDatabase.rawQuery("SELECT last_insert_rowid()", null);
            c.moveToFirst();
            long lastId = c.getInt(0);
            c.close();
            c = null;
            Log.i(TAG," last id = " + lastId);

            //insert params
            for(int j = 0; j<i%10;j++ ) {
                String param = "INSERT INTO " + TABLE_PARAMETERS +
                        " ( " + Parameters.NAME + ", " +
                        Parameters.TYPE + ", " +
                        Parameters.FORMULA_ID + " " +
                        ") values ( " +
                        "'par" + j + "', " + j%3 + ", " + lastId +
                        " );" ;
                sqLiteDatabase.execSQL(param);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

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

    interface ParameterColumns {
        String NAME = "name";
        String TYPE = "type";
        String FORMULA_ID = "formula_id";
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
}
