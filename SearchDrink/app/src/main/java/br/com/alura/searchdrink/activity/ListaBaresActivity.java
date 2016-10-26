package br.com.alura.searchdrink.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import br.com.alura.searchdrink.WebClient;
import br.com.alura.searchdrink.adapter.ExpandableFiltroAdapterTeste;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.task.GeoTask;

public class ListaBaresActivity extends BaseActivity implements GeoTask.Geo {

    public static String ApiKey = "AIzaSyAnE8Q44pkOA_ek3gCaS4tATj99LMOuhOM";


    private String TAG = "ListaBaresActivity";

    private ExpandableListView expandableFiltroBebidas;
    private ExpandableListView expandableFiltroBares;
    private ListView listaEstabelecimentos;
    private Button buscaLista;
    private Button buscaFiltros;

    private ExpandableFiltroAdapterTeste bebidasAdapter;
    private ExpandableFiltroAdapterTeste baresAdapter;

    private DatabaseReference dbFiltrosBar;
    private DatabaseReference dbFiltroBebidas;

    private HashMap<String, List<String>> mapaFiltrosBebidas = new HashMap<>();
    private HashMap<String, List<String>> mapaFiltrosBares= new HashMap<>();

    private ArrayList<String> filtrosTitulosBebidas;
    private ArrayList<String> filtrosTitulosBares;

    private List<Bar> estabalecimentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_bares);

        Intent veioMapaBar = getIntent();
        estabalecimentos = (List<Bar>) veioMapaBar.getSerializableExtra("lista");

        dbFiltrosBar = FirebaseDatabase.getInstance().getReference().child("filtros").child("tipoBar");
        dbFiltroBebidas = FirebaseDatabase.getInstance().getReference().child("filtros").child("tipoBebida");

        listaEstabelecimentos = (ListView) findViewById(R.id.lista_bares_lista);

        expandableFiltroBebidas = (ExpandableListView) findViewById(R.id.lista_bares_expandable_bebidas);
        expandableFiltroBares = (ExpandableListView) findViewById(R.id.lista_bares_expandable_bares);

        final LinearLayout lf = (LinearLayout) findViewById(R.id.lista_bares_filtros);

        buscaLista = (Button) findViewById(R.id.lista_bares_botao_lista);
        buscaFiltros = (Button) findViewById(R.id.lista_bares_botao_filtros);
        buscaLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscaLista.setTextColor(Color.parseColor("#F1C332"));
                buscaFiltros.setTextColor(Color.parseColor("#c4c4c4"));
                listaEstabelecimentos.setVisibility(View.VISIBLE);
                lf.setVisibility(View.GONE);
            }
        });
        buscaFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscaFiltros.setTextColor(Color.parseColor("#F1C332"));
                buscaLista.setTextColor(Color.parseColor("#c4c4c4"));
                lf.setVisibility(View.VISIBLE);
                listaEstabelecimentos.setVisibility(View.GONE);
            }
        });


        carregaFiltroBares();
        carregaFiltroBebidas();

//        Toast.makeText(this, estabalecimentos.get(0).getEndereco() + " / " + estabalecimentos.get(1).getEndereco(),Toast.LENGTH_SHORT).show();
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + "ruadrmarianoj.m.ferraz,190,centro,Osasco,SP" + "&destinations=" + "barueri,sp" + "&mode=driving&language=fr-FR&avoid=tolls&key=" + ApiKey;
        new GeoTask(this).execute(url);
    }


    private void carregaFiltroBares(){

        final List<String> tiposBares = new ArrayList<>();
//        for (Bar bar : estabalecimentos) {
//            if(bar.getTipoBar() != null && !tiposBares.contains(bar.getTipoBar()))  tiposBares.add(bar.getTipoBar());
//        }
//
//        String titulo = "tipos de bares";
//
//        mapaFiltrosBares.put(titulo, tiposBares);
//
//        filtrosTitulosBares = new ArrayList<String>();
//        filtrosTitulosBares.add(titulo);
//
//        baresAdapter = new ExpandableFiltroAdapterTeste(ListaBaresActivity.this, filtrosTitulosBares, mapaFiltrosBares);
//        expandableFiltroBares.setAdapter(baresAdapter);

        dbFiltrosBar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                    String tipo = String.valueOf(map.get("nome"));
                    tiposBares.add(tipo);
                }

                String titulo = "tipos de bares";

                mapaFiltrosBares.put(titulo, tiposBares);

                filtrosTitulosBares = new ArrayList<String>();
                filtrosTitulosBares.add(titulo);

                baresAdapter = new ExpandableFiltroAdapterTeste(ListaBaresActivity.this, filtrosTitulosBares, mapaFiltrosBares);
                expandableFiltroBares.setAdapter(baresAdapter);


                final HashMap<Integer, boolean[]> hash = baresAdapter.getChildsEstados();
                Log.i("carrega bares", (hash == null? "hash é nulo" : "hash não é nulo"));
                final boolean[] b = hash.get(0);
                Log.i("carrega bares", (b == null? "b é nulo" : "b não é nulo"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void carregaFiltroBebidas() {

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
                                String nome = String.valueOf(map.get("nome"));

                                lista.add(nome);
                            }

                        } else {
                            Map<String, Object> map = (HashMap<String, Object>) snapshot1.getValue();
                            String nome = String.valueOf(map.get("nome"));

                            lista.add(nome);
                        }
                    }

                    mapaFiltrosBebidas.put(nomeTitulo, lista);
                }

                filtrosTitulosBebidas = new ArrayList<String>(mapaFiltrosBebidas.keySet());
                bebidasAdapter = new ExpandableFiltroAdapterTeste(ListaBaresActivity.this, filtrosTitulosBebidas, mapaFiltrosBebidas);
                expandableFiltroBebidas.setAdapter(bebidasAdapter);

                final HashMap<Integer, boolean[]> hash = bebidasAdapter.getChildsEstados();
                Log.i("resolucao 1", (hash == null? "hash é nulo" : "hash não é nulo"));
                final boolean[] b = hash.get(0);
                Log.i("resolucao 1", (b == null? "b é nulo" : "b não é nulo"));

                hideProgressDialog();

                expandableFiltroBebidas.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                    @Override
                    public void onGroupExpand(int groupPosition) {
                        Toast.makeText(getApplicationContext(),
                                filtrosTitulosBebidas.get(groupPosition) + " List Expanded.",
                                Toast.LENGTH_SHORT).show();

                        final HashMap<Integer, boolean[]> hash = bebidasAdapter.getChildsEstados();
                        Log.i("resolucao 2", (hash == null? "hash é nulo" : "hash não é nulo"));
                        final boolean[] b = hash.get(0);
                        Log.i("resolucao 2", (b == null? "b é nulo" : "b não é nulo"));
                    }
                });
//
                expandableFiltroBebidas.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                    @Override
                    public void onGroupCollapse(int groupPosition) {
                        Toast.makeText(getApplicationContext(),
                                filtrosTitulosBebidas.get(groupPosition) + " List Collapsed.",
                                Toast.LENGTH_SHORT).show();

                        final HashMap<Integer, boolean[]> hash = bebidasAdapter.getChildsEstados();
                        Log.i("resolucao 3", (hash == null? "hash é nulo" : "hash não é nulo"));
                        final boolean[] b = hash.get(0);
                        Log.i("resolucao 3", (b == null? "b é nulo" : "b não é nulo"));
                    }
                });

                expandableFiltroBebidas.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v,
                                                int groupPosition, int childPosition, long id) {

//                        Toast.makeText(getApplicationContext(), filtrosTitulosBebidas.get(groupPosition) + " -> "
//                                + mapaFiltrosBebidas.get(filtrosTitulosBebidas.get(groupPosition)).get(childPosition),
//                                Toast.LENGTH_SHORT).show();

//                        Toast.makeText(getApplicationContext(), mapaFiltrosBebidas.get(groupPosition).get(childPosition), Toast.LENGTH_SHORT).show();

                        final HashMap<Integer, boolean[]> hash = bebidasAdapter.getChildsEstados();
                        Log.i("resolucao 4", (hash == null? "hash é nulo" : "hash não é nulo"));
                        final boolean[] b = hash.get(0);
                        Log.i("resolucao 4", (b == null? "b é nulo" : "b não é nulo"));

                        return false;
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public void setDouble(String result) {
        String res[]=result.split(",");
        Double min=Double.parseDouble(res[0])/60;
        int dist=Integer.parseInt(res[1])/1000;
        Toast.makeText(this, "Duration= " + (int) (min / 60) + " hr " + (int) (min % 60) + " mins", Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Distance= " + dist + " kilometers", Toast.LENGTH_LONG).show();

    }

}
