package br.com.alura.searchdrink.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

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
import br.com.alura.searchdrink.adapter.ListaExpansivaBebidasAdapter;
import br.com.alura.searchdrink.modelo.Bar;

public class ListaBaresActivity extends BaseActivity {

    private ExpandableListView listaBebidas;
    private ListView listaEstabelecimentos;
    private Button buscaLista;
    private Button buscaFiltros;

    private ExpandableListAdapter listaBebidasAdapter;
    private List<String> listaFiltrosTitulos;

    private DatabaseReference dbFiltrosBar;
    private DatabaseReference dbFiltroBebidas;

    private HashMap<String, List<String>> mapaFiltros = new HashMap<>();
    private HashMap<String, List<Bar>> mapaEstabelecimentos = new HashMap<>();

    private List<Bar> estabalecimentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_bares);

        Intent veioMapaBar = getIntent();
        estabalecimentos = (List<Bar>) veioMapaBar.getSerializableExtra("lista");

        dbFiltrosBar = FirebaseDatabase.getInstance().getReference().child("filtros").child("tipoBar");
        dbFiltroBebidas = FirebaseDatabase.getInstance().getReference().child("filtros").child("tipoBebida");

        listaEstabelecimentos = (ListView) findViewById(R.id.busca_lista);

        listaBebidas = (ExpandableListView) findViewById(R.id.listaFiltros);

        buscaLista = (Button) findViewById(R.id.busca_botao_lista);
        buscaFiltros = (Button) findViewById(R.id.busca_botao_filtros);
        buscaLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscaLista.setTextColor(Color.parseColor("#F1C332"));
                buscaFiltros.setTextColor(Color.parseColor("#c4c4c4"));
                listaEstabelecimentos.setVisibility(View.VISIBLE);
                listaBebidas.setVisibility(View.GONE);
            }
        });
        buscaFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscaFiltros.setTextColor(Color.parseColor("#F1C332"));
                buscaLista.setTextColor(Color.parseColor("#c4c4c4"));
                listaBebidas.setVisibility(View.VISIBLE);
                listaEstabelecimentos.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        carregaBebidas();
    }

    private void carregaBebidas() {

        showProgressDialog();

        dbFiltroBebidas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    List<String> lista = new ArrayList<>();
                    String nomeTitulo = snapshot.getKey();

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        if (snapshot.getKey().equals("bebidas geladas") || snapshot.getKey().equals("bebidas quentes")) {
                            for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                                Map<String, Object> map = (HashMap<String, Object>) snapshot2.getValue();
                                String nome = String.valueOf(snapshot1.getKey() + ": " + map.get("nome"));

//                                Toast.makeText(SearchActivity.this, nome, Toast.LENGTH_SHORT).show();
                                lista.add(nome);
                            }

                        } else {
                            Map<String, Object> map = (HashMap<String, Object>) snapshot1.getValue();
                            String nome = String.valueOf(snapshot.getKey() + ": " + map.get("nome"));

                            lista.add(nome);
                        }
                    }

                    mapaFiltros.put(nomeTitulo, lista);
                }

                listaFiltrosTitulos = new ArrayList<String>(mapaFiltros.keySet());
                listaBebidasAdapter = new ListaExpansivaBebidasAdapter(ListaBaresActivity.this, listaFiltrosTitulos, mapaFiltros);
                listaBebidas.setAdapter(listaBebidasAdapter);

                hideProgressDialog();

//                listaBebidas.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//
//                    @Override
//                    public void onGroupExpand(int groupPosition) {
//                        Toast.makeText(getApplicationContext(),
//                                listaFiltrosTitulos.get(groupPosition) + " List Expanded.",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//                listaBebidas.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//
//                    @Override
//                    public void onGroupCollapse(int groupPosition) {
//                        Toast.makeText(getApplicationContext(),
//                                listaFiltrosTitulos.get(groupPosition) + " List Collapsed.",
//                                Toast.LENGTH_SHORT).show();
//
//                    }
//                });

                listaBebidas.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v,
                                                int groupPosition, int childPosition, long id) {

                        Toast.makeText(getApplicationContext(), listaFiltrosTitulos.get(groupPosition) + " -> "
                                        + mapaFiltros.get(listaFiltrosTitulos.get(groupPosition)).get(childPosition),
                                Toast.LENGTH_SHORT).show();

                        return false;
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
