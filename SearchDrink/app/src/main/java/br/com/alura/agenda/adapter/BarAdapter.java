package br.com.alura.agenda.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.alura.agenda.R;
import br.com.alura.agenda.modelo.Bar;

/**
 * Created by Birbara on 04/08/2016.
 */
public class BarAdapter extends BaseAdapter {

    private final List<Bar> bars;
    private final Context context;

    public BarAdapter(Context context, List<Bar> bars){
        this.context = context;
        this.bars = bars;
    }

    @Override
    public int getCount() {
        return bars.size();
    }

    @Override
    public Object getItem(int position) {
        return bars.get(position);
    }

    @Override
    public long getItemId(int position) {
        return bars.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bar bar = bars.get(position);

        LayoutInflater inflater = LayoutInflater.from(context);

        // reaproveitamento de view
        View view = convertView;
        if(view == null) {
            view = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView campoNome = (TextView) view.findViewById(R.id.item_nome);
        campoNome.setText(bar.getNome());

        TextView campoTelefone = (TextView) view.findViewById(R.id.item_telefone);
        campoTelefone.setText(bar.getTelefone());

        // preenchimento de modo paisagem
        TextView campoEndereco = (TextView) view.findViewById(R.id.item_endereco);
        TextView campoSite = (TextView) view.findViewById(R.id.item_site);
        if (campoEndereco != null && campoSite != null){
            campoEndereco.setText(bar.getEndereco());
            campoSite.setText(bar.getSite());
        }

        ImageView campoFoto = (ImageView) view.findViewById(R.id.item_foto);
        if(bar.getCaminhoFoto() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(bar.getCaminhoFoto());
            bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
            campoFoto.setImageBitmap(bitmap);
            campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        return view;
    }
}
