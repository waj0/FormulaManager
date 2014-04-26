package cz.muni.fi.android.formulaManager.app.UI;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Martin on 26. 4. 2014.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> functionGroups; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> functions;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
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

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = new TextView(context);
        }
        TextView text = (TextView) convertView;
        text.setText(childText);
//		convertView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(activity, tempChild.get(childPosition),
//						Toast.LENGTH_SHORT).show();
//			}
//		});
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
            convertView = new TextView(context);
        }

        ((TextView) convertView).setText(headerTitle);
        convertView.setTag(headerTitle);

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