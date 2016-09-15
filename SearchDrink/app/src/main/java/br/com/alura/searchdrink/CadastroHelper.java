package br.com.alura.searchdrink;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.io.IOException;

import br.com.alura.searchdrink.activity.CadastroActivity;
import br.com.alura.searchdrink.modelo.Bar;

/**
 * Created by Birbara on 20/07/2016.
 */
public class CadastroHelper {

    private final EditText campoNome;
    private final EditText campoEndereco;
    private final EditText campoTelefone;
    private final EditText campoSite;
    private final EditText campoSenha;
    private final RatingBar campoNota;
    private final ImageView campoFoto;
    private Bar bar;

    public CadastroHelper(CadastroActivity activity) {

        this.bar = new Bar();

        this.campoNome = (EditText) activity.findViewById(R.id.cadastro_nome);
        this.campoEndereco = (EditText) activity.findViewById(R.id.cadastro_endereco);
        this.campoTelefone = (EditText) activity.findViewById(R.id.cadastro_telefone);
        this.campoSite = (EditText) activity.findViewById(R.id.cadastro_site);
        this.campoSenha = (EditText) activity.findViewById(R.id.cadastro_senha);
        this.campoNota = (RatingBar) activity.findViewById(R.id.cadastro_nota);
        this.campoFoto = (ImageView) activity.findViewById(R.id.cadastro_foto);

    }

    public Bar pegaBar() {

        bar.setNome(campoNome.getText().toString());
        bar.setEndereco(campoEndereco.getText().toString());
        bar.setTelefone(campoTelefone.getText().toString());
        bar.setSite(campoSite.getText().toString());
//        bar.setNota(Double.valueOf(campoNota.getRating()));
        bar.setCaminhoFoto((String)campoFoto.getTag());
        bar.setSenha(campoSenha.getText().toString());
        return bar;
    }

    public void preencheFormulario(Bar bar) {

        this.bar = bar;
        campoNome.setText(bar.getNome());
        campoEndereco.setText(bar.getEndereco());
        campoTelefone.setText(bar.getTelefone());
        campoSite.setText(bar.getSite());
//        campoNota.setRating((float) bar.getNota());
        campoSenha.setText(bar.getSenha());

        if (bar.getCaminhoFoto() != null){
//            carregaFoto(bar.getCaminhoFoto(), getContentResolver());
        }
    }

    public void carregaFoto(Uri caminhoFoto, ContentResolver contentResolver) {

        if(caminhoFoto != null) {

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, caminhoFoto);
                // Log.d(TAG, String.valueOf(bitmap));
                bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);

                campoFoto.setImageBitmap(bitmap);
                campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
                campoFoto.setTag(caminhoFoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
