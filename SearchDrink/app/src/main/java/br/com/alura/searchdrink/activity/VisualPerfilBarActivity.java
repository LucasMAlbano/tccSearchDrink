package br.com.alura.searchdrink.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_perfil_bar);

        bar = (Bar) getIntent().getSerializableExtra("bar");

        dbBar = FirebaseDatabase.getInstance().getReference().child("bares").child(bar.getuId()).child("notas");
        dbBar = FirebaseDatabase.getInstance().getReference().child("users").child(getUid()).child("notas");

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

        List<Bebida> bebidas = new ArrayList<Bebida>();
        bebidas.add(new Bebida("teste", "teste", 0.0, "teste"));
        BebidasAdapter bebidasAdapter = new BebidasAdapter(VisualPerfilBarActivity.this, bebidas);
        listaBebidas.setAdapter(bebidasAdapter);

        campoNome.setText(bar.getNome());
        campoEndereco.setText(bar.getEndereco());
        campoTipoBar.setText(bar.getTipoBar().toUpperCase());
    }

    private void pegaNotaUsuarioEComparaSeJaDeuNota() {

        final Map<String, Double> notasUsuario = new HashMap<String, Double>();
        final Map<String, Double> notasBar= new HashMap<String, Double>();

        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                    String nota = String.valueOf(map.get("nota"));

                    if (!nota.equals(null) && !nota.equals("null") && !nota.equals("") )
                        notasUsuario.put(snapshot.getKey(), Double.parseDouble(nota));
                }

                dbBar.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                            String nota = String.valueOf(map.get("nota"));

                            if (!nota.equals(null) && !nota.equals("null") && !nota.equals(""))
                                notasBar.put(snapshot.getKey(), Double.parseDouble(nota));
                        }

                        
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
