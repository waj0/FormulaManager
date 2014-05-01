package cz.muni.fi.android.formulaManager.app.UI;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import cz.muni.fi.android.formulaManager.app.R;

/**
 * Created by Martin on 26. 4. 2014.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private List<String> functionGroups; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> functions;

    public ExpandableListAdapter(Activity context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this.context = context;
        this.functionGroups = listDataHeader;
        this.functions = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.functions.get(this.functionGroups.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.function);
        item.setText(childText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.functions.get(this.functionGroups.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.functionGroups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.functionGroups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_item,
                    null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.function);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(headerTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}