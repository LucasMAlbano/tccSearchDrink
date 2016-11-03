package br.com.alura.searchdrink.activity;

import android.app.Dialog;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.adapter.BebidasAdapter;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.modelo.Bebida;
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

    private DatabaseReference dbBar;
    private DatabaseReference dbUser;

    private boolean deuNota = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_perfil_bar);


//        bar = (Bar) getIntent().getSerializableExtra("bar");
        bar = ListaBaresActivity.bar;

        dbBar = FirebaseDatabase.getInstance().getReference().child("bares").child(bar.getuId()).child("notas");
        dbUser = FirebaseDatabase.getInstance().getReference().child("users").child(getUid()).child("notas");

        campoNome = (TextView) findViewById(R.id.visual_perfil_nome);
        campoEndereco = (TextView) findViewById(R.id.visual_perfil_endereco);
        campoTipoBar = (TextView) findViewById(R.id.visual_lista_tipo_bar);

        botaoLigar = (Button) findViewById(R.id.visual_perfil_botao_ligar);
        botaoSite = (Button) findViewById(R.id.visual_perfil_botao_site);
        botaoDarNota = (Button) findViewById(R.id.visual_perfil_botao_nota);

        listaBebidas = (ListView) findViewById(R.id.visual_perfil_lista_bebidas);

    }

    @Override
    protected void onStart() {
        super.onStart();

        pegaNotaUsuarioEComparaSeJaDeuNota();

        BebidasAdapter bebidasAdapter = new BebidasAdapter(VisualPerfilBarActivity.this, bar.getBebidas());
        listaBebidas.setAdapter(bebidasAdapter);

        campoNome.setText(bar.getNome());
        campoEndereco.setText(bar.getEndereco());
        campoTipoBar.setText(bar.getTipoBar().toUpperCase());
    }

    private void pegaNotaUsuarioEComparaSeJaDeuNota() {

        final Map<String, Nota> notasUsuario = new HashMap<>();
        final Map<String, Nota> notasBar= new HashMap<>();

        final Nota[] notaDada = new Nota[1];

        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                    String nota = String.valueOf(map.get("nota"));

                    if (!nota.equals(null) && !nota.equals("null") && !nota.equals("") ) {
                        Nota n = new Nota(Double.parseDouble(nota),snapshot.getKey());
                        notasUsuario.put(snapshot.getKey(), n);
                    }
                }

                dbBar.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                            String nota = String.valueOf(map.get("nota"));

                            if (!nota.equals(null) && !nota.equals("null") && !nota.equals("")) {
                                Nota n = new Nota(Double.parseDouble(nota),snapshot.getKey());
                                notasBar.put(snapshot.getKey(), n);
                            }
                        }

                        if (notasUsuario.size() != 0 && notasBar.size() != 0){
                            for (Map.Entry<String, Nota> r : notasUsuario.entrySet()){
                                if (notasBar.containsKey(r.getKey())){
                                    deuNota = true;
                                    notaDada[0] = new Nota(r.getValue().getValorNota(), r.getValue().getuId());
                                }
                            }
                        }

                        botaoDarNota.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (deuNota == true){
                                    botaoDarNota.setText("Mudar sua nota");
                                }
                                else{
                                    final Dialog dialog = new Dialog(VisualPerfilBarActivity.this);
                                    dialog.setContentView(R.layout.dialog_dar_nota);
                                    dialog.setTitle("Dar Nota");

                                    final RatingBar dialogNota = (RatingBar) dialog.findViewById(R.id.dialog_dar_nota_nota);
                                    final Button dialogSalvar = (Button) dialog.findViewById(R.id.dialog_dar_nota_botao_salvar);
                                    final Button dialogCancelar = (Button) dialog.findViewById(R.id.dialog_dar_nota_botao_cancelar);

                                    dialogSalvar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            if (deuNota == false) {
                                                String idNota = dbBar.push().getKey();
                                                Nota notaNova = new Nota(dialogNota.getRating(), idNota);

                                                Map<String, Object> valoresNota = notaNova.toMap();

                                                Map<String, Object> childUpdates = new HashMap<>();
                                                childUpdates.put(idNota, valoresNota);

                                                dbBar.updateChildren(childUpdates);
                                                dbUser.updateChildren(childUpdates);

                                                notaDada[0] = notaNova;
                                                deuNota = true;
                                            }

                                            else{
                                                dialogNota.setRating((float)notaDada[0].getValorNota());

                                                String idNota = notaDada[0].getuId();
                                                Nota notaModificada = new Nota(dialogNota.getRating(), notaDada[0].getuId());

                                                Map<String, Object> valoresNota = notaModificada.toMap();

                                                Map<String, Object> childUpdates = new HashMap<>();
                                                childUpdates.put(idNota, valoresNota);

                                                dbBar.updateChildren(childUpdates);
                                                dbUser.updateChildren(childUpdates);
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
