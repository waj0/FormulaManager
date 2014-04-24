package cz.muni.fi.android.formulaManager.app;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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
        ret.setFavorite(fav != 0);
        //TODO ret.setParams();

        return ret;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return inflater.inflate(R.layout.row_layout, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.formula_name)).setText(cursor.getString(cursor.getColumnIndex(FormulaSQLHelper.Formulas.NAME)));
        ((TextView) view.findViewById(R.id.category)).setText(cursor.getString(cursor.getColumnIndex(FormulaSQLHelper.Formulas.CATEGORY)));
        ImageButton button = (ImageButton) view.findViewById(R.id.favorite);
        int favorite = cursor.getInt(cursor.getColumnIndex(FormulaSQLHelper.Formulas.CATEGORY));

        if(favorite != 0){
            button.setImageResource(R.drawable.ic_action_not_important);
        }else{
            button.setImageResource(R.drawable.ic_action_important);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO set onclicklistener
            }
        });

    }

//            public void onClick(View view) {
//                ImageButton button = (ImageButton) view;
//                Formula f = mItems.get(pos);
//                boolean change = f.isFavorite();
//                if(change){
//                    button.setImageResource(R.drawable.ic_action_not_important);
//                }else{
//                    button.setImageResource(R.drawable.ic_action_important);
//                }
//                f.setFavorite(!change);
//                //TODO change favorite value in DB
//            }
//        });
//
//

}
