package cz.fi.android.formulamanager.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Majo on 9. 4. 2014.
 * simple adapter for testing purposes
 */
public class FormulaAdapter extends ArrayAdapter<Formula> {
    //TODO change to CursorAdapter later, with DB

    private final List<Formula> values; //TODO delete dummy values for main list

    private LayoutInflater inflater;

    public FormulaAdapter(Context context, List<Formula> values) {
        super(context, R.layout.row_layout, values);
        this.values = values;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values.get(position).getName());

        return rowView;
    }
}
