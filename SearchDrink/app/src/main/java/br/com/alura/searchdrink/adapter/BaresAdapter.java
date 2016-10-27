package br.com.alura.searchdrink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.modelo.Bebida;

/**
 * Created by Birbara on 04/08/2016.
 */
public class BaresAdapter extends BaseAdapter {

    private final List<Bar> bares;
    private final Context context;

    public BaresAdapter(Context context, List<Bar> bares){
        this.context = context;
        this.bares = bares;
    }

    @Override
    public int getCount() {
        return bares.size();
    }

    @Override
    public Object getItem(int position) {
        return bares.get(position);
    }

    @Override
    public long getItemId(int position) {
        return bares.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bar bar = bares.get(position);

        LayoutInflater inflater = LayoutInflater.from(context);

        // reaproveitamento de view
        View view = convertView;
        if(view == null) {
            view = inflater.inflate(R.layout.list_item_bar, parent, false);
        }

        TextView campoNome = (TextView) view.findViewById(R.id.item_bar_nome);
        campoNome.setText(bar.getNome());

        TextView campoEndereco= (TextView) view.findViewById(R.id.item_bar_endereco);
        campoEndereco.setText(String.valueOf(bar.getEndereco()));

        return view;
    }
}
