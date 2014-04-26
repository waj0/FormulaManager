package cz.muni.fi.android.formulaManager.app;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import cz.muni.fi.android.formulaManager.app.database.FormulaProvider;
import cz.muni.fi.android.formulaManager.app.database.FormulaSQLHelper;

/**
 * Created by Majo on 9. 4. 2014.
 */
public class FormulaAdapter extends CursorAdapter{

    private static final String TAG = "cz.muni.fi.android.formulaManager.FormulaAdapter";

    LayoutInflater inflater;

    //TODO implement methods for category filter, resetitems, update stuff, insert stuff favorites changes and so on

    public FormulaAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


  /*  @Override
    public long getItemId(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        Log.i(TAG, cursor.getCount() + " / " + position);
        Log.i(TAG, "column index: " + cursor.getColumnCount());
        return cursor.getLong(cursor.getColumnIndex(FormulaProvider.Formulas._ID));
    }*/

    @Override
    public Formula getItem(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);

        Formula ret = new Formula();
        ret.setId(cursor.getLong(cursor.getColumnIndex(FormulaSQLHelper.Formulas._ID)));
        ret.setName(cursor.getString(cursor.getColumnIndex(FormulaSQLHelper.Formulas.NAME)));
        ret.setRawFormula(cursor.getString(cursor.getColumnIndex(FormulaSQLHelper.Formulas.RAWFORMULA)));
        ret.setCategory(cursor.getString(cursor.getColumnIndex(FormulaSQLHelper.Formulas.CATEGORY)));
        int fav = cursor.getInt(cursor.getColumnIndex(FormulaSQLHelper.Formulas.FAVORITE));
        //Log.i(TAG, "favorite: " + fav);
        ret.setFavorite(fav != 0);

        return ret;
    }

    @Override
    public View newView(final Context context, final Cursor cursor, ViewGroup viewGroup) {
        View ret = inflater.inflate(R.layout.row_layout, viewGroup, false);
//        Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
//        animation.setDuration(2000);
//        ret.setAnimation(animation);
        final long id = cursor.getLong(cursor.getColumnIndex(FormulaSQLHelper.Formulas._ID));
        final int favorite = cursor.getInt(cursor.getColumnIndex(FormulaSQLHelper.Formulas.FAVORITE));
        CheckBox favCheckBox = (CheckBox) ret.findViewById(R.id.favorite);
        favCheckBox.setChecked(favorite!=0);

       /* favCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean newState) {
                ContentValues cv = new ContentValues();
                cv.put(FormulaSQLHelper.Formulas.FAVORITE, (newState?1:0));
                int rows = context.getContentResolver().update(FormulaSQLHelper.Formulas.contentItemUri(id),cv,FormulaSQLHelper.Formulas._ID + " = " + id,null);
                Log.i(TAG, "updated " + rows);
            }
        });*/

        return ret;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ((TextView) view.findViewById(R.id.formula_name)).setText(cursor.getString(cursor.getColumnIndex(FormulaSQLHelper.Formulas.NAME)));
        ((TextView) view.findViewById(R.id.category)).setText(cursor.getString(cursor.getColumnIndex(FormulaSQLHelper.Formulas.CATEGORY)));
       /* Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        animation.setDuration(500);
        view.setAnimation(animation);*/
        final long id = cursor.getLong(cursor.getColumnIndex(FormulaSQLHelper.Formulas._ID));
        final int favorite = cursor.getInt(cursor.getColumnIndex(FormulaSQLHelper.Formulas.FAVORITE));
        CheckBox favCheckBox = (CheckBox) view.findViewById(R.id.favorite);
        favCheckBox.setChecked(favorite!=0);
      /*  favCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean newState) {
                ContentValues cv = new ContentValues();
                cv.put(FormulaSQLHelper.Formulas.FAVORITE, (newState?1:0));
                int rows = context.getContentResolver().update(FormulaSQLHelper.Formulas.contentItemUri(id),cv,null,null);
                Log.i(TAG, "updated " + rows);
            }
        });*/

    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
