package br.com.alura.searchdrink.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.ContextMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class PerfilActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listaBebidas;
    private TextView bemVindo;
//    private ProgressBar progressBar;

    private DatabaseReference database;

    private String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance().getReference();

        uId = getUid();

        listaBebidas = (ListView) findViewById(R.id.perfil_lista_bebidas);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        bemVindo = (TextView) findViewById(R.id.perfil_bemvindo);

//        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Button novaBebida = (Button) findViewById(R.id.nova_bebida);
        novaBebida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastraNovaBebida(view);
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(PerfilActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>());
                listaBebidas.setAdapter(adapter);
                carregaListaBebidas();
            }
        });

        registerForContextMenu(listaBebidas);
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

        carregaListaBebidas();
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        final Bebida bebida = (Bebida) listaBebidas.getItemAtPosition(info.position);

        MenuItem itemEditar = menu.add("Editar Bebida");
        itemEditar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                final Dialog dialog = new Dialog(PerfilActivity.this);
                dialog.setContentView(R.layout.dialog_bebida);
                dialog.setTitle("Editar Bebida");

                final EditText dialogNome = (EditText) dialog.findViewById(R.id.dialog_bebida_nome);
                dialogNome.setText(bebida.getNome());

                final EditText dialogPreco = (EditText) dialog.findViewById(R.id.dialog_bebida_preco);
                dialogPreco.setText(String.valueOf(bebida.getPreco()));

                Button dialogSalvar = (Button) dialog.findViewById(R.id.dialog_bebida_salvar);
                Button dialogCancelar = (Button) dialog.findViewById(R.id.dialog_bebida_cancelar);

//                Toast.makeText(PerfilActivity.this, bebida.getIdFirebase(), Toast.LENGTH_SHORT).show();

                dialogSalvar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nome = dialogNome.getText().toString();
                        double preco = Double.parseDouble(dialogPreco.getText().toString());
                        Bebida bebidaEditada = new Bebida(nome, preco, bebida.getIdFirebase());
                        editaBebidaFirebase(bebidaEditada);
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

                return false;
            }
        });
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
            return true;
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

    private void carregaListaBebidas() {

        final List<Bebida> bebidas = new ArrayList<>();
        final BebidasAdapter bebidasAdapter = new BebidasAdapter(this, bebidas);
        listaBebidas.setAdapter(bebidasAdapter);

        database.child("bares").child(uId).child("bebidas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Map <String, Object> mapBebidas = (HashMap<String, Object>)snapshot.getValue();
                    String nome = (String) mapBebidas.get("nome");
                    Double preco = (double) mapBebidas.get("preco");
                    String idBebida = dataSnapshot.getKey();
                    Bebida bebida = new Bebida(nome, preco, idBebida);

                    bebidas.add(bebida);
                }

                bebidasAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void cadastraNovaBebida(View view) {
        final Dialog dialog = new Dialog(PerfilActivity.this);
        dialog.setContentView(R.layout.dialog_bebida);
        dialog.setTitle("Adicionar Bebida");

        final EditText dialogNome = (EditText) dialog.findViewById(R.id.dialog_bebida_nome);
        final EditText dialogPreco = (EditText) dialog.findViewById(R.id.dialog_bebida_preco);
        Button dialogSalvar = (Button) dialog.findViewById(R.id.dialog_bebida_salvar);
        Button dialogCancelar = (Button) dialog.findViewById(R.id.dialog_bebida_cancelar);

        dialogSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = dialogNome.getText().toString();
                double preco = Double.parseDouble(dialogPreco.getText().toString());
//                Bebida bebida = new Bebida(nome, preco);
                cadastraBebidaFirebase(nome, preco);
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

        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void cadastraBebidaFirebase(String nome, double preco) {

        String idBebida = database.child("bares").child("bebidas").push().getKey();
        Bebida bebida = new Bebida(nome, preco, idBebida);
        Map<String, Object> valoresBebida = bebida.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("bares/" + uId + "/bebidas/" + idBebida, valoresBebida);

        database.updateChildren(childUpdates);
    }

    private void editaBebidaFirebase(Bebida bebida) {

        String idBebida = bebida.getIdFirebase();
        Map<String, Object> valoresBebida = bebida.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("bares/" + uId + "/bebidas/" + idBebida, valoresBebida);

        database.updateChildren(childUpdates);
    }

}
