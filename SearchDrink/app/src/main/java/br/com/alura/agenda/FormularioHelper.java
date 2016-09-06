package br.com.alura.agenda;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import br.com.alura.agenda.modelo.Bar;

/**
 * Created by Birbara on 20/07/2016.
 */
public class FormularioHelper {

    private final EditText campoNome;
    private final EditText campoEndereco;
    private final EditText campoTelefone;
    private final EditText campoSite;
    private final RatingBar campoNota;
    private final ImageView campoFoto;
    private Bar bar;

    public FormularioHelper(FormularioActivity activity) {

        this.bar = new Bar();

        this.campoNome = (EditText) activity.findViewById(R.id.formulario_nome);
        this.campoEndereco = (EditText) activity.findViewById(R.id.formulario_endereco);
        this.campoTelefone = (EditText) activity.findViewById(R.id.formulario_telefone);
        this.campoSite = (EditText) activity.findViewById(R.id.formulario_site);
        this.campoNota = (RatingBar) activity.findViewById(R.id.formulario_nota);
        this.campoFoto = (ImageView) activity.findViewById(R.id.formulario_foto);

    }

    public Bar pegaAluno() {

        bar.setNome(campoNome.getText().toString());
        bar.setEndereco(campoEndereco.getText().toString());
        bar.setTelefone(campoTelefone.getText().toString());
        bar.setSite(campoSite.getText().toString());
        bar.setNota(Double.valueOf(campoNota.getRating()));
        bar.setCaminhoFoto((String)campoFoto.getTag());
        return bar;
    }

    public void preencheFormulario(Bar bar) {

        this.bar = bar;
        campoNome.setText(bar.getNome());
        campoEndereco.setText(bar.getEndereco());
        campoTelefone.setText(bar.getTelefone());
        campoSite.setText(bar.getSite());
        campoNota.setRating((float) bar.getNota());

        if (bar.getCaminhoFoto() != null){
            carregaFoto(bar.getCaminhoFoto());
        }
    }

    public void carregaFoto(String caminhoFoto) {

        if(caminhoFoto != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(caminhoFoto);
            bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
            campoFoto.setImageBitmap(bitmap);
            campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
            campoFoto.setTag(caminhoFoto);
        }
    }
}
