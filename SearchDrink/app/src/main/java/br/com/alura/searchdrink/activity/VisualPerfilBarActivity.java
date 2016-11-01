package br.com.alura.searchdrink.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.adapter.BebidasAdapter;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.modelo.Bebida;

public class VisualPerfilBarActivity extends AppCompatActivity {

    private Bar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_perfil_bar);

        bar = (Bar) getIntent().getSerializableExtra("bar");


    }

    @Override
    protected void onStart() {
        super.onStart();

        final TextView campoNome = (TextView) findViewById(R.id.visual_lista_nome);
        final TextView campoEndereco = (TextView) findViewById(R.id.visual_lista_endereco);
        final TextView campoTipoBar = (TextView) findViewById(R.id.visual_lista_tipo_bar);

        final Button buttonLigar = (Button) findViewById(R.id.visual_lista_button_ligar);
        final Button buttonSite = (Button) findViewById(R.id.visual_lista_button_site);

        final ListView listaBebidas = (ListView) findViewById(R.id.visual_lista_bebidas);
        List<Bebida> bebidas = new ArrayList<Bebida>();
        bebidas.add(new Bebida("teste", "teste", 0.0, "teste"));
        BebidasAdapter bebidasAdapter = new BebidasAdapter(VisualPerfilBarActivity.this, bebidas);
        listaBebidas.setAdapter(bebidasAdapter);

        campoNome.setText(bar.getNome());
        campoEndereco.setText(bar.getEndereco());
        campoTipoBar.setText(bar.getTipoBar().toUpperCase());
    }
}
