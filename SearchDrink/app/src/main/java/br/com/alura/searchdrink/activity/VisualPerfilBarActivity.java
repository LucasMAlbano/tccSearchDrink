package br.com.alura.searchdrink.activity;

import android.app.Dialog;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.adapter.BebidasAdapter;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.modelo.Nota;

public class VisualPerfilBarActivity extends BaseActivity {

    private Bar bar;
    private TextView campoNome;
    private TextView campoEndereco;
    private TextView campoTipoBar;
    private Button botaoLigar;
    private Button botaoSite;
    private Button botaoDarNota;
    private ListView listaBebidas;
    private RatingBar campoNotaBar;

    private DatabaseReference dbBar;
    private DatabaseReference dbUser;

    private String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_perfil_bar);


//        bar = (Bar) getIntent().getSerializableExtra("bar");
        bar = ListaBaresActivity.bar;

        uId = getUid();

        dbBar = FirebaseDatabase.getInstance().getReference().child("bares").child(bar.getuId()).child("notas");
        dbUser = FirebaseDatabase.getInstance().getReference().child("users").child(uId).child("notas");

        campoNome = (TextView) findViewById(R.id.visual_perfil_nome);
        campoEndereco = (TextView) findViewById(R.id.visual_perfil_endereco);
        campoTipoBar = (TextView) findViewById(R.id.visual_lista_tipo_bar);

        botaoLigar = (Button) findViewById(R.id.visual_perfil_botao_ligar);
        botaoSite = (Button) findViewById(R.id.visual_perfil_botao_site);
        botaoDarNota = (Button) findViewById(R.id.visual_perfil_botao_nota);

        campoNotaBar = (RatingBar) findViewById(R.id.visual_perfil_nota);

        listaBebidas = (ListView) findViewById(R.id.visual_perfil_lista_bebidas);

    }

    @Override
    protected void onStart() {
        super.onStart();

        adicionaOuModificaNotaData();

        BebidasAdapter bebidasAdapter = new BebidasAdapter(VisualPerfilBarActivity.this, bar.getBebidas());
        listaBebidas.setAdapter(bebidasAdapter);

        campoNome.setText(bar.getNome());
        campoEndereco.setText(bar.getEndereco());
        campoTipoBar.setText(bar.getTipoBar().toUpperCase());

        campoNotaBar.setRating((float)bar.getMediaNotas());
        campoNotaBar.setEnabled(false);
    }

    private void adicionaOuModificaNotaData() {

        final Map<String, Nota> notasUsuario = new HashMap<>();
        final Map<String, Nota> notasBar= new HashMap<>();

        final boolean[] deuNota = {false};
        final Nota[] notaDada = new Nota[1];

        //comeca varrendo as notas do usuario
        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                    String idNota = String.valueOf(map.get("uId"));
                    String valorNota = String.valueOf(map.get("valorNota"));

                    //adiciona ao mapa de notas do usuario
                    if (!valorNota.equals(null) && !valorNota.equals("null") && !valorNota.equals("")) {
                        Nota n = new Nota(Double.parseDouble(valorNota),idNota);
                        notasUsuario.put(idNota, n);
                    }
                }

                //varre as notas do bar
                dbBar.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                            String idNota = String.valueOf(map.get("uId"));
                            String valorNota = String.valueOf(map.get("valorNota"));

                            //adiciona ao mapa de notas do bar
                            if (!valorNota.equals(null) && !valorNota.equals("null") && !valorNota.equals("")) {
                                Nota n = new Nota(Double.parseDouble(valorNota),idNota);
                                notasBar.put(idNota, n);
                            }
                        }

                        //compara se o usuario ja deu nota para o bar
                        if (notasUsuario.size() != 0 && notasBar.size() != 0){
                            for (Map.Entry<String, Nota> r : notasUsuario.entrySet()){
                                if (notasBar.containsKey(r.getKey())){
                                    deuNota[0] = true;
                                    notaDada[0] = new Nota(r.getValue().getValorNota(), r.getValue().getuId());
                                    botaoDarNota.setText("Mudar sua nota");
                                }
                            }
                        }

                        botaoDarNota.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final Dialog dialog = new Dialog(VisualPerfilBarActivity.this);
                                dialog.setContentView(R.layout.dialog_dar_nota);
                                dialog.setTitle(botaoDarNota.getText());

                                final RatingBar dialogNota = (RatingBar) dialog.findViewById(R.id.dialog_dar_nota_nota);
                                final Button dialogSalvar = (Button) dialog.findViewById(R.id.dialog_dar_nota_botao_salvar);
                                final Button dialogCancelar = (Button) dialog.findViewById(R.id.dialog_dar_nota_botao_cancelar);

                                if (deuNota[0] == true)
                                    dialogNota.setRating((float)notaDada[0].getValorNota());

                                dialogSalvar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        //adiciona nova nota inexistente do usuario para o bar
                                        if (deuNota[0] == false) {
                                            String idNota = dbBar.push().getKey();
                                            Nota notaNova = new Nota(dialogNota.getRating(), idNota);

                                            Map<String, Object> valoresNota = notaNova.toMap();

                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put(idNota, valoresNota);

                                            dbBar.updateChildren(childUpdates);
                                            dbUser.updateChildren(childUpdates);

                                            notaDada[0] = notaNova;
                                            deuNota[0] = true;
                                        }

                                        //edita a nota do usuario para o bar
                                        else {
                                            String idNota = notaDada[0].getuId();
                                            Nota notaModificada = new Nota(Double.valueOf(dialogNota.getRating()), idNota);

                                            Log.i("perfil", String.valueOf(notaModificada.getValorNota()));
                                            Map<String, Object> valoresNota = notaModificada.toMap();

                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put(idNota, valoresNota);

                                            dbBar.updateChildren(childUpdates);
                                            dbUser.updateChildren(childUpdates);

                                            notaDada[0] = notaModificada;
                                        }

                                        dialog.dismiss();
                                    }
                                });

                                dialogCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
