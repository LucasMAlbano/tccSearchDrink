package br.com.alura.searchdrink.activity;

import android.app.Dialog;
import android.content.Intent;
import android.drm.DrmManagerClient;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.adapter.BebidaAdapterTeste;
import br.com.alura.searchdrink.adapter.BebidasAdapter;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.modelo.Bebida;

public class PerfilActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

//    private BebidaAdapterTeste adapterTeste;

//    private RecyclerView bebidasRecicler;

//    private DatabaseReference bebidasReference;


    private ListView listaBebidas;
    private TextView bemVindo;
    private ImageView fotoPerfil;
    private ImageView miniFotoPerfil;
    private TextView nomeBar;
    private TextView emailBar;
//    private ProgressBar progressBar;

//    private DatabaseReference database;

    private DatabaseReference dbBar;

    private StorageReference storageRef;

    private String uId;

    private List<Bebida> bebidas;
    private BebidasAdapter bebidasAdapter;

    private Bar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        database = FirebaseDatabase.getInstance().getReference();

        uId = getUid();

        dbBar = FirebaseDatabase.getInstance().getReference().child("bares").child(uId);

        storageRef = FirebaseStorage.getInstance().getReference().child(uId);


//        bebidasRecicler = (RecyclerView) findViewById(R.id.recycler_comments);
//        bebidasRecicler.setLayoutManager(new LinearLayoutManager(this));

//        bebidasReference = FirebaseDatabase.getInstance().getReference()
//                .child("bares").child(uId).child("bebidas");



        fotoPerfil = (ImageView) findViewById(R.id.perfil_foto);

        bemVindo = (TextView) findViewById(R.id.perfil_bemvindo);

        listaBebidas = (ListView) findViewById(R.id.perfil_lista_bebidas);

        miniFotoPerfil = (ImageView) findViewById(R.id.perfil_miniFoto);
        nomeBar = (TextView) findViewById(R.id.perfil_nomeBar);
        emailBar = (TextView) findViewById(R.id.perfil_emailBar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

//        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Button novaBebida = (Button) findViewById(R.id.nova_bebida);
        novaBebida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastraNovaBebida(view);
                carregaListaBebidas();
            }
        });

        registerForContextMenu(listaBebidas);
    }

    @Override
    protected void onStart() {
        super.onStart();

        bar = new Bar();

        bebidas = new ArrayList<>();
        bebidasAdapter = new BebidasAdapter(this, bebidas);
        listaBebidas.setAdapter(bebidasAdapter);
//        adapterTeste = new BebidaAdapterTeste(this, bebidasReference);
//        bebidasRecicler.setAdapter(adapterTeste);

        carregaFotoPerfil();
        //        Picasso.with(this).load(bar.getCaminhoFoto()).into(fotoPerfil);
    }

    @Override
    protected void onResume() {
        super.onResume();

        showProgressDialog();

        dbBar.addListenerForSingleValueEvent(new ValueEventListener() {
//        database.child("bares").child(uId).child("perfil").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> mapBar = (Map)dataSnapshot.getValue();
                String nome = mapBar.get("nome");
                String email = mapBar.get("email");
                bemVindo.setText("Bem vindo(a) " + nome + "!");
                Toast.makeText(PerfilActivity.this, nome + " " + email, Toast.LENGTH_LONG).show();
//                nomeBar.setText(nome);
//                emailBar.setText(email);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        carregaBar();

        carregaListaBebidas();

        hideProgressDialog();
    }

    @Override
    public void onStop() {
        super.onStop();

//        listaBebidas.removeAllViews();
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

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//        final Bebida bebida = (Bebida) listaBebidas.getItemAtPosition(info.position);

        final int index = info.position;
        final Bebida bebida = bebidas.get(index);

        MenuItem itemEditar = menu.add("Editar Bebida");
        editaBebida(bebida, itemEditar);
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

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else */if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        else if(id == R.id.nav_editar_perfil){
            startActivity(new Intent(this, FormularioActivity.class));
//            finish();
            onResume();
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

    private void carregaBar() {

        dbBar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map <String, Object> mapBar = (HashMap<String, Object>)dataSnapshot.getValue();
                String nome = String.valueOf(mapBar.get("nome"));
                String email = String.valueOf(mapBar.get("email"));
                String endereco = String.valueOf(mapBar.get("endereco"));
                String site = String.valueOf(mapBar.get("site"));
                String telefone = String.valueOf(mapBar.get("telefone"));
                String uriFotoPerfil = String.valueOf(mapBar.get("uriFotoPerfil"));

                bar = new Bar(nome, email, endereco, site, telefone, Uri.parse(uriFotoPerfil));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void carregaListaBebidas() {

//        bebidas = new ArrayList<>();
//        listaBebidas.setAdapter(bebidasAdapter);

        dbBar.child("bebidas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                bebidas.removeAll(bebidas);

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Map <String, Object> mapBebidas = (HashMap<String, Object>)snapshot.getValue();
                    String nome = String.valueOf(mapBebidas.get("nome"));
                    double preco = Double.parseDouble(String.valueOf(mapBebidas.get("preco")));
                    String idBebida = snapshot.getKey();
                    Bebida bebida = new Bebida(nome, preco, idBebida);

                    bebidas.add(bebida);
                }
//                Toast.makeText(PerfilActivity.this, bebidas.get(0).getIdFirebase(), Toast.LENGTH_LONG).show();
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

        String idBebida = dbBar.child("bebidas").push().getKey();
        Bebida bebida = new Bebida(nome, preco, idBebida);
        bebidas.add(bebida);

        Map<String, Object> valoresBebida = bebida.toMap();

//        database.child("bares").child(uId).child("bebidas").setValue(bebida);

        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("bares/" + uId + "/bebidas/" + idBebida, valoresBebida);
        childUpdates.put("/bebidas/" + idBebida, valoresBebida);

        dbBar.updateChildren(childUpdates);
    }

    private void editaBebida(final Bebida bebida, MenuItem itemEditar) {
        itemEditar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                final Dialog dialog = new Dialog(PerfilActivity.this);
                dialog.setContentView(R.layout.dialog_bebida);
                dialog.setTitle("Editar Bebida");

//                final TextView dialogId = (TextView) dialog.findViewById(R.id.dialog_bebida_id);
//                dialogId.setText(bebida.getIdFirebase());

                final EditText dialogNome = (EditText) dialog.findViewById(R.id.dialog_bebida_nome);
                dialogNome.setText(bebida.getNome());

                final EditText dialogPreco = (EditText) dialog.findViewById(R.id.dialog_bebida_preco);
                dialogPreco.setText(String.valueOf(bebida.getPreco()));

                Button dialogSalvar = (Button) dialog.findViewById(R.id.dialog_bebida_salvar);
                Button dialogCancelar = (Button) dialog.findViewById(R.id.dialog_bebida_cancelar);

//                Toast.makeText(PerfilActivity.this, String.valueOf(index), Toast.LENGTH_SHORT).show();
                Toast.makeText(PerfilActivity.this, bebida.getIdFirebase(), Toast.LENGTH_SHORT).show();

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

    private void editaBebidaFirebase(Bebida bebida) {

        String idBebida = bebida.getIdFirebase();
        Map<String, Object> valoresBebida = bebida.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("bares/" + uId + "/bebidas/" + idBebida, valoresBebida);
        childUpdates.put("/bebidas/" + idBebida, valoresBebida);

        dbBar.updateChildren(childUpdates);
    }

    private void carregaFotoPerfil() {
        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.child("perfil").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

                    fotoPerfil.setImageBitmap(bitmap);
                    fotoPerfil.setScaleType(ImageView.ScaleType.FIT_XY);
                    fotoPerfil.setTag(Uri.parse(localFile.getPath()));

//                    Toast.makeText(PerfilActivity.this, "Sucesso ao fazer download de foto perfil" + bitmap == null ? "null":"nao nulo", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PerfilActivity.this, "Falha ao fazer download de foto perfil", Toast.LENGTH_LONG).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
