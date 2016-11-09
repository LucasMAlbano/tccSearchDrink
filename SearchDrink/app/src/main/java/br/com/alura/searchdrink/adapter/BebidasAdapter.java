package br.com.alura.searchdrink.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

        ImageView campoFoto = (ImageView) view.findViewById(R.id.item_bebida_foto);
        final String[] tiposBebida = bebida.getNome().split(":");
        preencheCampoFotoBebida(campoFoto, tiposBebida[0]);

        TextView campoNome = (TextView) view.findViewById(R.id.item_bebida_nome);
        campoNome.setText(bebida.getNome() + " - " + bebida.getQuantidade());

        TextView campoPreco = (TextView) view.findViewById(R.id.item_bebida_preco);
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

    private void preencheCampoFotoBebida(ImageView campoFoto, String tipoBebida) {
        if(tipoBebida.equals("cerveja"))
            campoFoto.setBackgroundResource(R.mipmap.ic_cerveja);

        else if (tipoBebida.equals("chopp"))
            campoFoto.setBackgroundResource(R.mipmap.ic_chopp);

        else if (tipoBebida.equals("catuaba") || tipoBebida.equals("pinga") || tipoBebida.equals("tequila") ||
                tipoBebida.equals("vodka") || tipoBebida.equals("wisky"))
            campoFoto.setBackgroundResource(R.mipmap.ic_wisky);

        else if (tipoBebida.equals("drink"))
            campoFoto.setBackgroundResource(R.mipmap.ic_drink);

        else if (tipoBebida.equals("isotônico") || tipoBebida.equals("energético"))
            campoFoto.setBackgroundResource(R.mipmap.ic_isotonico);

        else if (tipoBebida.equals("refrigerante"))
            campoFoto.setBackgroundResource(R.mipmap.ic_refri);

        else if (tipoBebida.equals("suco"))
            campoFoto.setBackgroundResource(R.mipmap.ic_suco);
    }
}
