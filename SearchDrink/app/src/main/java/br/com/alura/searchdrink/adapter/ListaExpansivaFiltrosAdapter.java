package br.com.alura.searchdrink.adapter;

/**
 * Created by Birbara on 10/10/2016.
 */

import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import br.com.alura.searchdrink.R;

public class ListaExpansivaFiltrosAdapter extends BaseExpandableListAdapter {
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
//    public ListaExpansivaFiltrosAdapter(Context context, List<String> listaTitulo,
//                                        HashMap<String, List<String>> mapaLista) {
//        this.context = context;
//        this.listaTitulo = listaTitulo;
//        this.mapaLista = mapaLista;
//    }
//
//    @Override
//    public Object getChild(int groupPosition, int expandedListPosition) {
//        return this.mapaLista.get(this.listaTitulo.get(groupPosition))
//                .get(expandedListPosition);
//    }
//
//    @Override
//    public long getChildId(int groupPosition, int expandedListPosition) {
//        return expandedListPosition;
//    }
//
//    @Override
//    public View getChildView(int groupPosition, final int childPosition,
//                             boolean isLastChild, View convertView, ViewGroup parent) {
//        final String expandedListText = (String) getChild(groupPosition, childPosition);
//        if (convertView == null) {
//            LayoutInflater layoutInflater = (LayoutInflater) this.context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = layoutInflater.inflate(R.layout.list_item_filtro, null);
//        }
//
////        TextView expandedListTextView = (TextView) convertView
////                .findViewById(R.id.expandedListItemSearch);
//        CheckBox expandedListCheckBox = (CheckBox) convertView.findViewById(R.id.expandedListItemSearch);
//        expandedListCheckBox.setText(expandedListText);
//        return convertView;
//    }
//
//    @Override
//    public int getChildrenCount(int groupPosition) {
//        return this.mapaLista.get(this.listaTitulo.get(groupPosition))
//                .size();
//    }
//
//    @Override
//    public Object getGroup(int groupPosition) {
//        return this.listaTitulo.get(groupPosition);
//    }
//
//    @Override
//    public int getGroupCount() {
//        return this.listaTitulo.size();
//    }
//
//    @Override
//    public long getGroupId(int groupPosition) {
//        return groupPosition;
//    }
//
//    @Override
//    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//
//        String listTitle = (String) getGroup(groupPosition);
//
//        if (convertView == null) {
//            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = layoutInflater.inflate(R.layout.list_group_filtro, null);
//        }
//
//        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitleSearch);
//        listTitleTextView.setTypeface(null, Typeface.BOLD);
//        listTitleTextView.setText(listTitle);
//        return convertView;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return false;
//    }
//
//    @Override
//    public boolean isChildSelectable(int groupPosition, int childPosition) {
//        return true;
//    }
}
