package cz.fi.android.formulamanager.app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Majo on 9. 4. 2014.
 * simple adapter for testing purposes
 */
public class FormulaAdapter extends BaseAdapter {
    //TODO change to CursorAdapter later, with DB

    private static final String TAG = "cz.fi.android.formulamanager.FormulaAdapter";

    private LayoutInflater inflater;

    private List<Formula> mItems = new ArrayList<Formula>();

    public FormulaAdapter(Context context) {
        super();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        resetItems();
    }

    void resetItems() {
        Log.i(TAG, "list reset");
        mItems.clear();
        //TODO delete dummy values
        for(int i=0;i<20;i++){
            Formula f = new Formula();
            f.setId((long) i);
            f.setName("dummy F " + i);
            f.setRawFormula("some parsable string" + i);
            for(int j = 0;j<i+2;j++) {
                Parameter p = new Parameter();
                p.setId((long)j);
                p.setType(Parameter.PARAM_REGULAR);
                p.setName("p " + j);
                f.addParam(p);
            }
            mItems.add(f);
        }
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mItems.remove(position);
        notifyDataSetChanged();
    }

    public void insert(int position, Formula item) {
        mItems.add(position, item);
        notifyDataSetChanged();
    }

    public int indexOf(Formula f) {
        return mItems.indexOf(f);
    }

    public void add(Formula f) {
        mItems.add(f);
        notifyDataSetChanged();
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mItems.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     * data set.
     * @return The data at the specified position.
     */
    @Override
    public Formula getItem(int position) {
        return mItems.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position The position of the item within the adapter's data set of the item whose view
     * we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     * is non-null and of an appropriate type before using. If it is not possible to convert
     * this view to display the correct data, this method can create a new view.
     * Heterogeneous lists can specify their number of view types, so that this View is
     * always of the right type (see {@link #getViewTypeCount()} and
     * {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.row_layout, parent, false);
            // Clicking the delete icon, will read the position of the item stored in
            // the tag and delete it from the list. So we don't need to generate a new
            // onClickListener every time the content of this view changes.
            /*final View origView = convertView;
            convertView.findViewById(R.id.action_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListView.delete(((ViewHolder)origView.getTag()).position);
                }
            });*/

            holder = new ViewHolder();
            assert convertView != null;
            holder.mTextView = (TextView) convertView.findViewById(R.id.label);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.position = position;
        holder.mTextView.setText(mItems.get(position).getName());

        return convertView;
    }

    private class ViewHolder {
        TextView mTextView;
        int position;
    }

}
