package br.com.alura.searchdrink.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
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
import br.com.alura.searchdrink.modelo.Bebida;

import static android.R.attr.key;

public class PerfilActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listaBebidas;
    private TextView bemVindo;

    private DatabaseReference database;

    private String uId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance().getReference();

        listaBebidas = (ListView) findViewById(R.id.perfil_lista_bebidas);

        uId = getUid();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Button novaBebida = (Button) findViewById(R.id.nova_bebida);
        novaBebida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog = new Dialog(PerfilActivity.this);
                dialog.setContentView(R.layout.dialog_bebida);
                dialog.setTitle("Adicionar Bebida");

                EditText dialogNome = (EditText) findViewById(R.id.dialog_bebida_nome);
                EditText dialogPreco = (EditText) findViewById(R.id.dialog_bebida_preco);
                Button dialogSalvar = (Button) findViewById(R.id.dialog_bebida_salvar);
                Button dialogCancelar = (Button) findViewById(R.id.dialog_bebida_cancelar);

                dialogSalvar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        carregaLista();
                    }
                });
                dialogCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        bemVindo = (TextView) findViewById(R.id.perfil_bemvindo);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        database.child("bares").child(uId).child("perfil").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> mapBar = (Map)dataSnapshot.getValue();
                bemVindo.setText("Bem vindo " + mapBar.get("nome") + "!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        else if(id == R.id.nav_editar_perfil){
            startActivity(new Intent(this, FormularioActivity.class));
            finish();
        }

        else if (id == R.id.nav_sair){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void carregaLista() {


        String idBebida = database.child("bares").child(uId).child("bebidas").push().getKey();

        Map<String, Object> postValues = post.toMap();

        Bebida bebida = new Bebida(nome, preco);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + idBebida, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + idBebida, postValues);

        database.updateChildren(childUpdates);

//        database.child("bares").child(uId).child("bebidas").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                String idBebida = dataSnapshot.getKey();
//
//                Map<String, String> mapBebidas = (Map)dataSnapshot.getValue();
//                String nome = mapBebidas.get(idBebida, "nome");
//                String preco = mapBebidas.get("preco");
//
//                bemVindo.setText("Bem vindo " + mapBebidas.get("nome") + "!");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        Bebida dao = new Bebida();
        List<Bebida> bebidas = new ArrayList<>();
//        List<Bebida> bars = dao.buscaAlunos();
//        dao.close();

        BebidasAdapter adapter = new BebidasAdapter(PerfilActivity.this, bebidas);

        listaBebidas.setAdapter(adapter);
    }
}
