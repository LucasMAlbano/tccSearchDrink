package br.com.alura.searchdrink;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;

import java.io.IOException;

import br.com.alura.searchdrink.activity.FormularioActivity;
import br.com.alura.searchdrink.modelo.Bar;

/**
 * Created by Birbara on 20/07/2016.
 */
public class FormularioHelper {

    private final EditText campoNome;
    private final EditText campoEndereco;
    private final EditText campoTelefone;
    private final EditText campoSite;
//    private final EditText campoSenha;
//    private final RatingBar campoNota;
    private final ImageView campoFoto;

//    private final DatabaseReference database;

    private Bar bar;

    public FormularioHelper(FormularioActivity activity/*, DatabaseReference database*/) {

        this.bar = new Bar();

        this.campoNome = (EditText) activity.findViewById(R.id.formulario_nome);
        this.campoEndereco = (EditText) activity.findViewById(R.id.formulario_endereco);
        this.campoTelefone = (EditText) activity.findViewById(R.id.formulario_telefone);
        this.campoSite = (EditText) activity.findViewById(R.id.formulario_site);
//        this.campoSenha = (EditText) activity.findViewById(R.id.cadastro_senha);
//        this.campoNota = (RatingBar) activity.findViewById(R.id.cadastro_nota);
        this.campoFoto = (ImageView) activity.findViewById(R.id.formulario_foto);

//        this.database = database;

    }

    public Bar pegaBar() {

        bar.setNome(campoNome.getText().toString());
        bar.setEndereco(campoEndereco.getText().toString());
        bar.setTelefone(campoTelefone.getText().toString());
        bar.setSite(campoSite.getText().toString());
//        bar.setNota(Double.valueOf(campoNota.getRating()));
//        bar.setCaminhoFoto(campoFoto.getTag());
//        bar.setSenha(campoSenha.getText().toString());
        return bar;
    }

    public void preencheFormulario(Bar bar) {

        this.bar = bar;
        campoNome.setText(bar.getNome());
        campoEndereco.setText(bar.getEndereco());
        campoTelefone.setText(bar.getTelefone());
        campoSite.setText(bar.getSite());
//        campoNota.setRating((float) bar.getNota());
//        campoSenha.setText(bar.getSenha());

        if (bar.getCaminhoFoto() != null){
//            carregaFoto(bar.getCaminhoFoto(), getContentResolver());
        }
    }

    public void carregaFoto(Uri uri, ContentResolver contentResolver) {

        if(uri != null) {

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                // Log.d(TAG, String.valueOf(bitmap));
                bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);

                campoFoto.setImageBitmap(bitmap);
                campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
                campoFoto.setTag(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
