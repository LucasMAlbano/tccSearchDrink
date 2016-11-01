package br.com.alura.searchdrink.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.adapter.BaresAdapter;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.task.GeoTask;

public class ListaBaresActivity extends BaseActivity implements GeoTask.Geo {

    public static String ApiKey = "AIzaSyAnE8Q44pkOA_ek3gCaS4tATj99LMOuhOM";

    private String TAG = "ListaBaresActivity";

    public static String bebidaSelecionada = null;
    public static HashMap<String, Boolean> mapaBaresSelecionados = null;

//    private ExpandableListView expandableFiltroBebidas;
//    private ExpandableListView expandableFiltroBares;

    private ListView listaEstabelecimentos;
    private Button buscaLista;
    private Button buscaFiltros;

//    private ExpandableFiltroAdapterTeste bebidasAdapter;
//    private ExpandableFiltroAdapterTeste baresAdapter;

    private BaresAdapter adapter;


//    private HashMap<String, List<String>> mapaFiltrosBebidas = new HashMap<>();
//    private HashMap<String, List<String>> mapaFiltrosBares= new HashMap<>();

//    private ArrayList<String> filtrosTitulosBebidas;
//    private ArrayList<String> filtrosTitulosBares;

    private List<Bar> estabalecimentos;

//    List<String> tiposBebidas = new ArrayList<>();

    private SeekBar valor = null;
    private SeekBar distancia = null;
    private TextView mostravalor = null;
    private TextView mostradistancia = null;
    private Button botaoSelecionarFiltros;
    private TextView campoStatusFiltro;
    private Button botaoRemoverFiltros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_bares_);

        Intent veioMapaBar = getIntent();
        estabalecimentos = (List<Bar>) veioMapaBar.getSerializableExtra("estabelecimentos");

        listaEstabelecimentos = (ListView) findViewById(R.id.lista_bares_lista);
        adapter = new BaresAdapter(this, estabalecimentos);
        listaEstabelecimentos.setAdapter(adapter);

//        expandableFiltroBebidas = (ExpandableListView) findViewById(R.id.lista_bares_expandable_bebidas);
//        expandableFiltroBares = (ExpandableListView) findViewById(R.id.lista_bares_expandable_bares);

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


//        carregaFiltroBares();
//        carregaFiltroBebidas();

//        Toast.makeText(this, estabalecimentos.get(0).getEndereco() + " / " + estabalecimentos.get(1).getEndereco(),Toast.LENGTH_SHORT).show();
//        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + "ruadrmarianoj.m.ferraz,190,centro,Osasco,SP" + "&destinations=" + "barueri,sp" + "&mode=driving&language=fr-FR&avoid=tolls&key=" + ApiKey;
//        new GeoTask(this).execute(url);

        listaEstabelecimentos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) { //AdapterView<?> adapterView = tiposBebidas, View view = item, int i = posicao, long l = id
                Bar bar = (Bar) lista.getItemAtPosition(position);
                carregaPerfilBar(bar);
            }
        });



        filtroDanilo();
        botaoSelecionarFiltros = (Button) findViewById(R.id.lista_bares_button_busca);
        botaoSelecionarFiltros.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent(ListaBaresActivity.this, FiltroActivity.class);
//                it.putExtra("estabelecimentos", (Serializable) estabalecimentos);
                startActivity(it);
            }
        });



        registerForContextMenu(listaEstabelecimentos);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (bebidaSelecionada != null){

            campoStatusFiltro = (TextView) findViewById(R.id.lista_bares_statusFiltro);
            botaoRemoverFiltros = (Button) findViewById(R.id.lista_bares_botao_removerFiltros);
            campoStatusFiltro.setVisibility(View.VISIBLE);
            botaoRemoverFiltros.setVisibility(View.VISIBLE);
            botaoSelecionarFiltros.setVisibility(View.GONE);

            List<String> l = new ArrayList<>();
            for (Map.Entry<String, Boolean> r : mapaBaresSelecionados.entrySet()){
                if (r.getValue().equals(true))
                    l.add(r.getKey());
            }

            campoStatusFiltro.setText("Bebida: " + bebidaSelecionada + "\nBares: " + l);
            botaoRemoverFiltros.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bebidaSelecionada = null;
                    mapaBaresSelecionados = null;
                    campoStatusFiltro.setVisibility(View.GONE);
                    botaoRemoverFiltros.setVisibility(View.GONE);
                    botaoSelecionarFiltros.setVisibility(View.VISIBLE);
                }
            });


        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        final Bar bar = (Bar) listaEstabelecimentos.getItemAtPosition(info.position);

        //item site
        MenuItem itemPerfil = menu.add("Ver perfil");
        itemPerfil.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                carregaPerfilBar(bar);

                return false;
            }
        });

    }

    private void carregaPerfilBar(Bar bar) {

        Intent intent = new Intent(this, VisualPerfilBarActivity.class);
        intent.putExtra("bar", bar);
        startActivity(intent);

//        final Dialog dialog = newDialog(ListaBaresActivity.this);
//        dialog.setContentView(R.layout.dialog_lista_bar);
//        dialog.setTitle("Perfil " + bar.getNome());
//
//        final TextView campoNome = (TextView) dialog.findViewById(R.id.dialog_lista_nome);
//        final TextView campoEndereco = (TextView) dialog.findViewById(R.id.dialog_lista_endereco);
//        final TextView campoTipoBar = (TextView) dialog.findViewById(R.id.dialog_lista_tipo_bar);
//
//        final Button buttonLigar = (Button) dialog.findViewById(R.id.dialog_lista_button_ligar);
//        final Button buttonSite = (Button) dialog.findViewById(R.id.dialog_lista_button_site);
//
//        final ListView tiposBebidas = (ListView) dialog.findViewById(R.id.dialog_lista_bebidas);
//        List<Bebida> bebidas = new ArrayList<Bebida>();
//        bebidas.add(new Bebida("teste", "teste", 0.0, "teste"));
//        BebidasAdapter bebidasAdapter = new BebidasAdapter(ListaBaresActivity.this, bebidas);
//        tiposBebidas.setAdapter(bebidasAdapter);
//
//        campoNome.setText(bar.getNome());
//        campoEndereco.setText(bar.getEndereco());
//        campoTipoBar.setText(bar.getTipoBar());
    }

//    private void carregaFiltroBares(){
//
//        final List<String> tiposBares = new ArrayList<>();
////        for (Bar bar : estabalecimentos) {
////            if(bar.getTipoBar() != null && !tiposBares.contains(bar.getTipoBar()))  tiposBares.add(bar.getTipoBar());
////        }
////
////        String titulo = "tipos de bares";
////
////        mapaFiltrosBares.put(titulo, tiposBares);
////
////        filtrosTitulosBares = new ArrayList<String>();
////        filtrosTitulosBares.add(titulo);
////
////        baresAdapter = new ExpandableFiltroAdapterTeste(ListaBaresActivity.this, filtrosTitulosBares, mapaFiltrosBares);
////        expandableFiltroBares.setAdapter(baresAdapter);
//
//        dbFiltrosBar.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
//                    String tipo = String.valueOf(map.get("nome"));
//                    tiposBares.add(tipo);
//                }
//
//                String titulo = "tipos de bares";
//
//                mapaFiltrosBares.put(titulo, tiposBares);
//
//                filtrosTitulosBares = new ArrayList<String>();
//                filtrosTitulosBares.add(titulo);
//
//                baresAdapter = new ExpandableFiltroAdapterTeste(ListaBaresActivity.this, filtrosTitulosBares, mapaFiltrosBares);
//                expandableFiltroBares.setAdapter(baresAdapter);
//
//
//                final HashMap<Integer, boolean[]> hash = baresAdapter.getChildsEstados();
//                Log.i("carrega bares", (hash == null? "hash é nulo" : "hash não é nulo"));
//                final boolean[] b = hash.get(0);
//                Log.i("carrega bares", (b == null? "b é nulo" : "b não é nulo"));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//            }
//        });
//    }
//
//    private void carregaFiltroBebidas() {
//
//        showProgressDialog();
//
//        dbFiltroBebidas.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                    List<String> tiposBebidas = new ArrayList<>();
//                    String nomeTitulo = snapshot.getKey();
//
//                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//
//                        if (snapshot.getKey().equals("bebidas geladas") || snapshot.getKey().equals("bebidas quentes")) {
//                            for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
//                                Map<String, Object> map = (HashMap<String, Object>) snapshot2.getValue();
//                                String nome = String.valueOf(map.get("nome"));
//
//                                tiposBebidas.add(nome);
//                            }
//
//                        } else {
//                            Map<String, Object> map = (HashMap<String, Object>) snapshot1.getValue();
//                            String nome = String.valueOf(map.get("nome"));
//
//                            tiposBebidas.add(nome);
//                        }
//                    }
//
//                    mapaFiltrosBebidas.put(nomeTitulo, tiposBebidas);
//                }
//
//                filtrosTitulosBebidas = new ArrayList<String>(mapaFiltrosBebidas.keySet());
//                bebidasAdapter = new ExpandableFiltroAdapterTeste(ListaBaresActivity.this, filtrosTitulosBebidas, mapaFiltrosBebidas);
//                expandableFiltroBebidas.setAdapter(bebidasAdapter);
//
//                final HashMap<Integer, boolean[]> hash = bebidasAdapter.getChildsEstados();
//                Log.i("resolucao 1", (hash == null? "hash é nulo" : "hash não é nulo"));
//                final boolean[] b = hash.get(0);
//                Log.i("resolucao 1", (b == null? "b é nulo" : "b não é nulo"));
//
//                hideProgressDialog();
//
//                expandableFiltroBebidas.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//                    @Override
//                    public void onGroupExpand(int groupPosition) {
//                        Toast.makeText(getApplicationContext(),
//                                filtrosTitulosBebidas.get(groupPosition) + " List Expanded.",
//                                Toast.LENGTH_SHORT).show();
//
//                        final HashMap<Integer, boolean[]> hash = bebidasAdapter.getChildsEstados();
//                        Log.i("resolucao 2", (hash == null? "hash é nulo" : "hash não é nulo"));
//                        final boolean[] b = hash.get(0);
//                        Log.i("resolucao 2", (b == null? "b é nulo" : "b não é nulo"));
//                    }
//                });
////
//                expandableFiltroBebidas.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//                    @Override
//                    public void onGroupCollapse(int groupPosition) {
//                        Toast.makeText(getApplicationContext(),
//                                filtrosTitulosBebidas.get(groupPosition) + " List Collapsed.",
//                                Toast.LENGTH_SHORT).show();
//
//                        final HashMap<Integer, boolean[]> hash = bebidasAdapter.getChildsEstados();
//                        Log.i("resolucao 3", (hash == null? "hash é nulo" : "hash não é nulo"));
//                        final boolean[] b = hash.get(0);
//                        Log.i("resolucao 3", (b == null? "b é nulo" : "b não é nulo"));
//                    }
//                });
//
//                expandableFiltroBebidas.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//                    @Override
//                    public boolean onChildClick(ExpandableListView parent, View v,
//                                                int groupPosition, int childPosition, long id) {
//
////                        Toast.makeText(getApplicationContext(), filtrosTitulosBebidas.get(groupPosition) + " -> "
////                                + mapaFiltrosBebidas.get(filtrosTitulosBebidas.get(groupPosition)).get(childPosition),
////                                Toast.LENGTH_SHORT).show();
//
////                        Toast.makeText(getApplicationContext(), mapaFiltrosBebidas.get(groupPosition).get(childPosition), Toast.LENGTH_SHORT).show();
//
//                        final HashMap<Integer, boolean[]> hash = bebidasAdapter.getChildsEstados();
//                        Log.i("resolucao 4", (hash == null? "hash é nulo" : "hash não é nulo"));
//                        final boolean[] b = hash.get(0);
//                        Log.i("resolucao 4", (b == null? "b é nulo" : "b não é nulo"));
//
//                        return false;
//                    }
//                });
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//            }
//        });
//    }

    @Override
    public void setDouble(String result) {
        String res[]=result.split(",");
        Double min=Double.parseDouble(res[0])/60;
        int dist=Integer.parseInt(res[1])/1000;
        Toast.makeText(this, "Duration= " + (int) (min / 60) + " hr " + (int) (min % 60) + " mins", Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Distance= " + dist + " kilometers", Toast.LENGTH_LONG).show();

    }


    private void filtroDanilo() {

        valor = (SeekBar) findViewById(R.id.lista_bares_seekBar_valor);
        distancia = (SeekBar) findViewById(R.id.lista_bares_seekBar_distancia);
        mostravalor = (TextView) findViewById(R.id.lista_bares_mostraValor);
        mostradistancia = (TextView) findViewById(R.id.lista_bares_mostraDistancia);

        distancia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progresChanged2 = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progresChanged2 = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onStopTrackingTouch(SeekBar seekBar) {


                int x = distancia.getMax();
                int media = x / 4;

                for (int i = 1; i < 20; i++) {
                    if (progresChanged2 == 0) {
                        progresChanged2 = 0;
                        break;
                    }
                    if (progresChanged2 < media) {
                        progresChanged2 = media;
                        break;
                    }
                    if (progresChanged2 > media * i && progresChanged2 < media * (i + 1)) {
                        progresChanged2 = media * (i + 1);
                        break;
                    }
                }
                distancia.setProgress(progresChanged2);
                mostradistancia.setText("" + progresChanged2);

                Toast.makeText(ListaBaresActivity.this, "Distância:" + progresChanged2, Toast.LENGTH_SHORT).show();
            }

        });

        valor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {

                int x = valor.getMax();
                int media = x / 10;

                for (int i = 1; i < 20; i++) {
                    if(progressChanged == 0){
                        progressChanged = 0;
                        break;
                    }
                    if (progressChanged < media) {
                        progressChanged = media;
                        break;
                    }
                    if (progressChanged > media * i && progressChanged < media * (i + 1)) {
                        progressChanged = media * (i + 1);
                        break;
                    }
                }

                valor.setProgress(progressChanged);
                mostravalor.setText("" + progressChanged);

                Toast.makeText(ListaBaresActivity.this, "Valor:" + progressChanged,
                        Toast.LENGTH_SHORT).show();
            }



        });
    }
}
