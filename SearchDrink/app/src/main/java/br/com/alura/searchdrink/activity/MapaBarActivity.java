package br.com.alura.searchdrink.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.alura.searchdrink.Localizador;
import br.com.alura.searchdrink.adapter.BaresAdapter;
import br.com.alura.searchdrink.fragment.MapaFragment;
import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.modelo.Bar;

//import com.google.android.gms.maps.SupportMapFragment;


public class MapaBarActivity extends BaseActivity {

    public static boolean verificadorSeUsuarioEhBar = false;

    private static final int REQUEST_PERMISSOES = 1;
    private MapaFragment mapaFragment;

    DatabaseReference database;

    FloatingActionMenu botaoMenu;
    FloatingActionButton floatingLogin, floatingFiltrar, floatingPesquisar, floatingReflesh;/*, floatingListar*/
    ImageButton botaoCentralizar;

    private String uId;
    private String uriFoto;


    final List<Bar> bares = new ArrayList<>();
    BaresAdapter baresAdapter;

    private SearchView searchView;
    private ListView listView;
    private Button botaoFecharBusca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mapa_bar);

        uId = getUid();

        database = FirebaseDatabase.getInstance().getReference().child("bares");

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tx = manager.beginTransaction();

        botaoCentralizar = (ImageButton) findViewById(R.id.mapa_bar_botao_centralizar);

        mapaFragment = new MapaFragment(/*database, */botaoCentralizar);

        tx.replace(R.id.frame_mapa, mapaFragment);
        tx.commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] permissoes = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(permissoes, REQUEST_PERMISSOES);
            }
        }

        inicializaBuscaBaresPorNome();

        inicializaBotoesFlutuantes();
    }





    @Override
    protected void onStart() {
        super.onStart();

        verificaSeUsuarioEhBar();
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Sair da aplicação??");
        alertDialogBuilder
                .setMessage("Sim para sair!")
                .setCancelable(false)
                .setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_PERMISSOES){

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED){

                new Localizador(this, mapaFragment);
            }
        }
    }

    private void vaiParaPerfil() {

        if (verificadorSeUsuarioEhBar) {
            Intent vaiParaPerfilBar = new Intent(MapaBarActivity.this, PerfilBarActivity.class);
            startActivity(vaiParaPerfilBar);
        } else {
            Intent vaiParaPerfilUsuario = new Intent(MapaBarActivity.this, PerfilUsuarioActivity.class);
            startActivity(vaiParaPerfilUsuario);
        }
    }

    private void verificaSeUsuarioEhBar() {

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(uId)) {
                        verificadorSeUsuarioEhBar = true;
                        break;
                    }
                }

                if (verificadorSeUsuarioEhBar)
                    floatingLogin.setImageResource(R.mipmap.ic_perfilbar);
                else
                    floatingLogin.setImageResource(R.mipmap.ic_perfiluser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializaBuscaBaresPorNome() {

        searchView = (SearchView) findViewById(R.id.mapa_bar_search);
        listView = (ListView) findViewById(R.id.mapa_bar_lista);
        botaoFecharBusca = (Button) findViewById(R.id.mapa_bar_botao_fecharBusca);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Bar barClicaco = (Bar) adapterView.getItemAtPosition(i);
                MapaFragment.centralizaEm(barClicaco.getCoordenada(), "busca");
                listView.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
                botaoMenu.setVisibility(View.VISIBLE);
                botaoCentralizar.setVisibility(View.VISIBLE);
                botaoFecharBusca.setVisibility(View.GONE);
            }
        });

        botaoFecharBusca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setVisibility(View.GONE);
                searchView.setVisibility(View.GONE);
                botaoFecharBusca.setVisibility(View.GONE);
                botaoMenu.setVisibility(View.VISIBLE);
                botaoCentralizar.setVisibility(View.VISIBLE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                bares.removeAll(bares);

                for (Bar bar : MapaFragment.estabelecimentos) {
                    if (bar.getNome().toUpperCase().contains(query.toUpperCase())) {
                        bares.add(bar);
                    }
                }

                if (bares.size()!= 0){
                    listView.setAdapter(null);
                    baresAdapter = new BaresAdapter(MapaBarActivity.this, bares);
                    listView.setAdapter(baresAdapter);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                bares.removeAll(bares);

                for (Bar bar : MapaFragment.estabelecimentos) {
                    if (!bares.contains(bar)) {
                        if (bar.getNome().toUpperCase().contains(newText.toUpperCase())) {
                            bares.add(bar);
                        }
                    }
                }

                if (bares.size()!= 0){
                    listView.setAdapter(null);
                    baresAdapter = new BaresAdapter(MapaBarActivity.this, bares);
                    listView.setAdapter(baresAdapter);
                }

                return false;
            }
        });
    }

    private void inicializaBotoesFlutuantes() {
        botaoMenu = (FloatingActionMenu) findViewById(R.id.mapa_bar_button_menu);
        floatingLogin = (FloatingActionButton) findViewById(R.id.mapa_bar_floating_login);
        floatingFiltrar = (FloatingActionButton) findViewById(R.id.mapa_bar_floating_filtrar);
        floatingPesquisar = (FloatingActionButton) findViewById(R.id.mapa_bar_floating_pesquisar);
        floatingReflesh = (FloatingActionButton) findViewById(R.id.mapa_bar_floating_reflesh);

        floatingLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                boolean ehBar = verificaSeEhBar(uId);
//                Toast.makeText(MapaBarActivity.this, String.valueOf(ehBar), Toast.LENGTH_SHORT).show();
                vaiParaPerfil();
            }
        });
        floatingFiltrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent vaiParaLista = new Intent(MapaBarActivity.this, ListaBaresActivity.class);
//                vaiParaLista.putExtra("estabelecimentos", (Serializable) mapaFragment.getEstabelecimentos());
                startActivity(vaiParaLista);
            }
        });
        floatingPesquisar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchView.setVisibility(View.VISIBLE);
                searchView.onActionViewExpanded();
                listView.setVisibility(View.VISIBLE);
                botaoFecharBusca.setVisibility(View.VISIBLE);
                botaoMenu.setVisibility(View.GONE);
                botaoMenu.close(true);
                botaoCentralizar.setVisibility(View.GONE);
            }
        });
        floatingReflesh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapaFragment.baixaEstabelecimentosFirebase();
            }
        });
    }
}
