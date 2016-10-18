package br.com.alura.searchdrink.adapter;

/**
 * Created by Birbara on 10/10/2016.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import br.com.alura.searchdrink.R;

// Eclipse wanted me to use a sparse array instead of my hashmaps, I just suppressed that suggestion
//@SuppressLint("UseSparseArrays")
public class ListaExpansivaFiltrosAdapterTeste extends BaseExpandableListAdapter {

    private Context mContext;

    private HashMap<String, List<String>> mListDataChild;

    private ArrayList<String> mListDataGroup;

    private HashMap<Integer, boolean[]> mChildCheckStates;

    private CheckBox mCheckBox;
    private TextView mGroupText;

    private String groupText;
    private String childText;

    public ListaExpansivaFiltrosAdapterTeste(Context context,
                                          ArrayList<String> listDataGroup, HashMap<String, List<String>> listDataChild) {

        mContext = context;
        mListDataGroup = listDataGroup;
        mListDataChild = listDataChild;

        mChildCheckStates = new HashMap<>();
    }

    @Override
    public int getGroupCount() {
        return mListDataGroup.size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return mListDataGroup.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        groupText = getGroup(groupPosition);

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group_filtro, null);

            mGroupText = (TextView) convertView.findViewById(R.id.listTitleSearch);

            convertView.setTag(mGroupText);
        } else {

            mGroupText = (TextView) convertView.getTag();
        }

        mGroupText.setTypeface(null, Typeface.BOLD);
        mGroupText.setText(groupText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListDataChild.get(mListDataGroup.get(groupPosition)).size();
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return mListDataChild.get(mListDataGroup.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;

        childText = getChild(mGroupPosition, mChildPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_filtro, null);

            mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox);

            convertView.setTag(R.layout.list_item_filtro, mCheckBox);

        } else {
            mCheckBox = (CheckBox) convertView.getTag(R.layout.list_item_filtro);
        }

        mCheckBox.setText(childText);
        mCheckBox.setOnCheckedChangeListener(null);

        if (mChildCheckStates.containsKey(mGroupPosition)) {
            boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
            mCheckBox.setChecked(getChecked[mChildPosition]);

        } else {
            boolean getChecked[] = new boolean[getChildrenCount(mGroupPosition)];
            for(int i = 0; i < getChecked.length; i ++) {
                getChecked[i] = true;
                mCheckBox.setChecked(getChecked[i]);
            }
            mChildCheckStates.put(mGroupPosition, getChecked);
        }

        mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);

                } else {

                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public HashMap<Integer, boolean[]> getmChildCheckStates(){
        return this.mChildCheckStates;
    }
}
