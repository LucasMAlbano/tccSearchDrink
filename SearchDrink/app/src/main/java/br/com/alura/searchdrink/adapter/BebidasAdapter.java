package br.com.alura.searchdrink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.modelo.Bebida;

/**
 * Created by Birbara on 04/08/2016.
 */
public class BebidasAdapter extends BaseAdapter {

    private final List<Bebida> bebidas;
    private final Context context;

    public BebidasAdapter(Context context, List<Bebida> bebidas){
        this.context = context;
        this.bebidas = bebidas;
    }

    @Override
    public int getCount() {
        return bebidas.size();
    }

    @Override
    public Object getItem(int position) {
        return bebidas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return bebidas.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bebida bebida = bebidas.get(position);

        LayoutInflater inflater = LayoutInflater.from(context);

        // reaproveitamento de view
        View view = convertView;
        if(view == null) {
            view = inflater.inflate(R.layout.list_item_bebida, parent, false);
        }

        TextView campoNome = (TextView) view.findViewById(R.id.item_nome);
        campoNome.setText(bebida.getNome() + " - " + bebida.getQuantidade());

        TextView campoPreco = (TextView) view.findViewById(R.id.item_preco);
        campoPreco.setText(String.valueOf(bebida.getPreco()));

//        TextView campoIdFirebase = (TextView) view.findViewById(R.id.item_idFirebase);
//        campoIdFirebase.setText(bebida.getIdFirebase());

        // preenchimento de modo paisagem
//        TextView campoEndereco = (TextView) view.findViewById(R.id.item_endereco);
//        TextView campoSite = (TextView) view.findViewById(R.id.item_site);
//        if (campoEndereco != null && campoSite != null){
//            campoEndereco.setText(bebida.getEndereco());
//            campoSite.setText(bebida.getSite());
//        }

//        ImageView campoFoto = (ImageView) view.findViewById(R.id.item_foto);
//        if(bebida.getUriFoto() != null) {
//            Bitmap bitmap = BitmapFactory.decodeFile(bebida.getUriFoto());
//            bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
//            campoFoto.setImageBitmap(bitmap);
//            campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
//        }

        return view;
    }
}
