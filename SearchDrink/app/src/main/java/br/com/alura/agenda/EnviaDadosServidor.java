package br.com.alura.agenda;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import br.com.alura.agenda.converter.AlunoConverter;
import br.com.alura.agenda.dao.BarDAO;
import br.com.alura.agenda.modelo.Bar;

/**
 * Created by Birbara on 24/08/2016.
 */
public class EnviaDadosServidor extends AsyncTask<Void, Void, String> {

    private Context context;
    private ProgressDialog alertDialog;

    public EnviaDadosServidor(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        alertDialog = ProgressDialog.show(context, "Aguarde", "Enviando para o servidor...", true, true);
        alertDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {

        WebClient webClient = new WebClient();
        AlunoConverter converter = new AlunoConverter();
        BarDAO dao = new BarDAO(context);
        List<Bar> bars = dao.buscaAlunos();
        dao.close();
        String json = converter.toJson(bars);
        String resposta = webClient.post(json);

        return resposta;
    }

    @Override
    protected void onPostExecute(String resposta) {

        alertDialog.dismiss();

        Toast.makeText(context, resposta, Toast.LENGTH_LONG).show();
    }
}
