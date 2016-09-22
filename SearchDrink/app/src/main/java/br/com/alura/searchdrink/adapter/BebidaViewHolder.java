package br.com.alura.searchdrink.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.com.alura.searchdrink.R;

/**
 * Created by Birbara on 22/09/2016.
 */
public class BebidaViewHolder extends RecyclerView.ViewHolder {

    public TextView nomeView;
    public TextView precoVuew;

    public BebidaViewHolder(View itemView) {
        super(itemView);

        nomeView = (TextView) itemView.findViewById(R.id.item_nome);
        precoVuew = (TextView) itemView.findViewById(R.id.item_preco);
    }
}
