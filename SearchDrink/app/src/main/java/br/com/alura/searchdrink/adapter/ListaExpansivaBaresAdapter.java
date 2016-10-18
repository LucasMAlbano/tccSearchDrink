package br.com.alura.searchdrink.adapter;

/**
 * Created by Birbara on 10/10/2016.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import br.com.alura.searchdrink.R;

public class ListaExpansivaBaresAdapter extends BaseExpandableListAdapter {
    @Override
    public int getGroupCount() {
        return 0;
    }

    @Override
    public int getChildrenCount(int i) {
        return 0;
    }

    @Override
    public Object getGroup(int i) {
        return null;
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

//    private Context context;
//    private List<String> listaTitulo;
//    private HashMap<String, List<String>> mapaLista;
//
//    public ListaExpansivaBaresAdapter(Context context, List<String> listaTitulo,
//                                      HashMap<String, List<String>> mapaLista) {
//        this.context = context;
//        this.listaTitulo = listaTitulo;
//        this.mapaLista = mapaLista;
//    }
//
//    @Override
//    public Object getChild(int groupPosition, int childPosition) {
//        return this.mapaLista.get(this.listaTitulo.get(groupPosition))
//                .get(childPosition);
//    }
//
//    @Override
//    public long getChildId(int groupPosition, int childPosition) {
//        return childPosition;
//    }
//
//    @Override
//    public View getChildView(int groupPosition, final int childPosition,
//                             boolean isLastChild, View convertView, ViewGroup parent) {
//
//        final String expandedListText = (String) getChild(groupPosition, childPosition);
//
//        if (convertView == null) {
//            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = layoutInflater.inflate(R.layout.list_item_filtro, null);
//        }
//
////        TextView expandedListTextView = (TextView) convertView
////                .findViewById(R.id.expandedListItemSearch);
//        CheckBox expandedListTextView = (CheckBox) convertView.findViewById(R.id.expandedListItemSearch);
//        expandedListTextView.setText(expandedListText);
//        return convertView;
//    }
//
//    @Override
//    public int getChildrenCount(int listPosition) {
//        return this.mapaLista.get(this.listaTitulo.get(listPosition))
//                .size();
//    }
//
//    @Override
//    public Object getGroup(int listPosition) {
//        return this.listaTitulo.get(listPosition);
//    }
//
//    @Override
//    public int getGroupCount() {
//        return this.listaTitulo.size();
//    }
//
//    @Override
//    public long getGroupId(int listPosition) {
//        return listPosition;
//    }
//
//    @Override
//    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//
//        String listTitle = (String) getGroup(listPosition);
//
//        if (convertView == null) {
//            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = layoutInflater.inflate(R.layout.list_group_filtro, null);
//        }
//
//        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitleSearch);
//        listTitleTextView.setTypeface(null, Typeface.BOLD);
//        listTitleTextView.setText(listTitle);
//
//        return convertView;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return false;
//    }
//
//    @Override
//    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
//        return true;
//    }
}
