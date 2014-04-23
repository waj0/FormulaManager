package cz.muni.fi.android.formulaManager.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cz.muni.fi.android.formulaManager.app.UI.FormulaListFragment;

/**
 * Created by Majo on 22. 4. 2014.
 */
public class FormulaSQLHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "formulaManager.db";
    public static final String TABLE_FORMULAS = "formulas";

    //TODO add formula columns here
    private static final String TABLE_STUDENTS_CREATE =
            "CREATE TABLE " + TABLE_FORMULAS + " (" +
                    FormulaProvider.Formulas._ID + " integer primary key autoincrement," +
                    FormulaProvider.Formulas.NAME + " text, " +
                    FormulaProvider.Formulas.RAWFORMULA + " text, " +
                    FormulaProvider.Formulas.CATEGORY + " text, " +
                    FormulaProvider.Formulas.FAVORITE + " integer, " +
                    FormulaProvider.Formulas.VERSION + " integer " +
                    ");";

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
        sqLiteDatabase.execSQL(TABLE_STUDENTS_CREATE);

        //TODO delete dummy values, add formula columns here
        for (int i=0;i<15;i++){
            String formula = "INSERT INTO " + TABLE_FORMULAS +
                    " ( " + FormulaProvider.Formulas.NAME + ", " +
                    FormulaProvider.Formulas.RAWFORMULA + ", " +
                    FormulaProvider.Formulas.CATEGORY + ", " +
                    FormulaProvider.Formulas.FAVORITE + ", " +
                    FormulaProvider.Formulas.VERSION + " " +
                    ") values ( " +
                    "'formula" + i + "', 'raw', '" + FormulaListFragment.categoryNames[i%4] + "', 0, 1 " +
                    " );" ;
            sqLiteDatabase.execSQL(formula);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
