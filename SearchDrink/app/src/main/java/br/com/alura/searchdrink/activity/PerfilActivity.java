package br.com.alura.searchdrink.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.adapter.BebidasAdapter;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.modelo.Bebida;

public class PerfilActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView campoListaBebidas;
    private TextView campoBemVindo;
    private ImageView campoFotoPerfil;
    private TextView campoNomeBar;
    private TextView campoEmailBar;
//    private ProgressBar progressBar;

    private DatabaseReference dbBar;
    private DatabaseReference dbFiltroBebidas;
    private StorageReference storageRef;

    private String uId;

    private List<Bebida> bebidas;
    private BebidasAdapter bebidasAdapter;

    private Bar bar;

    private Bitmap bitmap = null;
    private File mypath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        database = FirebaseDatabase.getInstance().getReference();

        uId = getUid();

        dbBar = FirebaseDatabase.getInstance().getReference().child("bares").child(uId);
        dbFiltroBebidas = FirebaseDatabase.getInstance().getReference().child("filtros").child("tipoBebida");

        storageRef = FirebaseStorage.getInstance().getReference().child(uId);


//        bebidasRecicler = (RecyclerView) findViewById(R.id.recycler_comments);
//        bebidasRecicler.setLayoutManager(new LinearLayoutManager(this));

//        bebidasReference = FirebaseDatabase.getInstance().getReference()
//                .child("bares").child(uId).child("bebidas");


        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("profile", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        mypath = new File(directory, "perfil" + uId + ".jpg");



        campoFotoPerfil = (ImageView) findViewById(R.id.perfil_foto);
//        campoFotoPerfil.setVisibility(View.GONE);

        campoBemVindo = (TextView) findViewById(R.id.perfil_bemvindo);

        campoListaBebidas = (ListView) findViewById(R.id.perfil_lista_bebidas);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

//        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View v = navigationView.getHeaderView(0);
        campoNomeBar = (TextView) v.findViewById(R.id.perfil_nomeBar);
        campoEmailBar = (TextView) v.findViewById(R.id.perfil_emailBar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Button novaBebida = (Button) findViewById(R.id.nova_bebida);
        novaBebida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastraNovaBebida(view);
                showProgressDialog();
                carregaListaBebidas();
            }
        });

        registerForContextMenu(campoListaBebidas);
    }

    @Override
    protected void onStart() {
        super.onStart();

        bar = new Bar();

        bebidas = new ArrayList<>();
        bebidasAdapter = new BebidasAdapter(this, bebidas);
        campoListaBebidas.setAdapter(bebidasAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        showProgressDialog();

        iniciaPerfil();

        //        Picasso.with(this).load(bar.getCaminhoFoto()).into(campoFotoPerfil);


    }

    @Override
    public void onStop() {
        super.onStop();

//        campoListaBebidas.removeAllViews();
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
//        final Bebida bebida = (Bebida) campoListaBebidas.getItemAtPosition(info.position);

        final int index = info.position;
        final Bebida bebida = bebidas.get(index);

        MenuItem itemEditar = menu.add("Editar Bebida");
        editaBebida(bebida, itemEditar);

        MenuItem itemDeletar = menu.add("Deletar Bebida");
        deletaBebida(bebida, itemDeletar);
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

                campoBemVindo.setText("Bem vindo(a) " + nome + "!");
                campoNomeBar.setText(nome);
                campoEmailBar.setText(email);

                bar = new Bar(nome, email, endereco, site, telefone, Uri.parse(uriFotoPerfil));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void carregaListaBebidas() {

        dbBar.child("bebidas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                bebidas.removeAll(bebidas);

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Map <String, Object> mapBebidas = (HashMap<String, Object>)snapshot.getValue();
                    String nome = String.valueOf(mapBebidas.get("nome"));
                    String quantidade = String.valueOf(mapBebidas.get("quantidade"));
                    double preco = Double.parseDouble(String.valueOf(mapBebidas.get("preco")));
                    String idBebida = snapshot.getKey();
                    Bebida bebida = new Bebida(nome, quantidade, preco, idBebida);

                    bebidas.add(bebida);
                }
//                Toast.makeText(PerfilActivity.this, bebidas.get(0).getIdFirebase(), Toast.LENGTH_LONG).show();
                bebidasAdapter.notifyDataSetChanged();

                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void cadastraNovaBebida(View view) {
        final Dialog dialog = new Dialog(PerfilActivity.this);
        dialog.setContentView(R.layout.dialog_cadastrar_bebida);
        dialog.setTitle("Adicionar Bebida");

        final Spinner dialogNome = (Spinner) dialog.findViewById(R.id.dialog_spinner_bebida_nome);

        showProgressDialog();

        Map<String, List<String>> mapNomesBebidas = new HashMap<>();
        final List<String> b = new ArrayList<>();

        dbFiltroBebidas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

//                    Map<String, Object> mapBebidas = (HashMap<String, Object>) snapshot.getValue();

                    Log.i("teste", snapshot.getKey());

                    for(DataSnapshot snapshot1 : snapshot.getChildren()) {

                        if (snapshot.getKey().equals("bebidas geladas") || snapshot.getKey().equals("bebidas quentes")) {
                            for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                                Map<String, Object> map = (HashMap<String, Object>) snapshot2.getValue();
                                String nome = String.valueOf(snapshot1.getKey() + ": " + map.get("nome"));
                                b.add(nome);
                            }

                        } else {
                            Map<String, Object> map = (HashMap<String, Object>) snapshot1.getValue();
                            String nome = String.valueOf(snapshot.getKey() + ": " + map.get("nome"));
                            b.add(nome);
                        }
                    }
                }

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(PerfilActivity.this,
                        android.R.layout.simple_spinner_item, b);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dialogNome.setAdapter(spinnerAdapter);

                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final Spinner dialogQuantidade = (Spinner) dialog.findViewById(R.id.dialog_spinner_bebida_quantidade);
        final EditText dialogPreco = (EditText) dialog.findViewById(R.id.dialog_bebida_preco);
        Button dialogSalvar = (Button) dialog.findViewById(R.id.dialog_bebida_salvar);
        Button dialogCancelar = (Button) dialog.findViewById(R.id.dialog_bebida_cancelar);

        dialogSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = String.valueOf(dialogNome.getSelectedItem()).split(": ")[1];
                String quantidade = String.valueOf(dialogQuantidade.getSelectedItem());
                double preco = Double.parseDouble(dialogPreco.getText().toString());
//                Bebida bebida = new Bebida(nome, preco);
                cadastraBebidaFirebase(nome, quantidade, preco);
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

    private void cadastraBebidaFirebase(String nome, String quantidade, double preco) {

        String idBebida = dbBar.child("bebidas").push().getKey();
        Bebida bebida = new Bebida(nome, quantidade, preco, idBebida);
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
                dialog.setContentView(R.layout.dialog_editar_bebida);
                dialog.setTitle("Editar Bebida");

//                final TextView dialogId = (TextView) dialog.findViewById(R.id.dialog_bebida_id);
//                dialogId.setText(bebida.getIdFirebase());

                final TextView dialogNome = (TextView) dialog.findViewById(R.id.dialog_bebida_nome);
                dialogNome.setText(bebida.getNome());

                final Spinner dialogQuantidade = (Spinner) dialog.findViewById(R.id.dialog_spinner_bebida_quantidade);

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
                        String quantidade = String.valueOf(dialogQuantidade.getSelectedItem());
                        Bebida bebidaEditada = new Bebida(nome, quantidade, preco, bebida.getIdFirebase());
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

    private void deletaBebida(final Bebida bebida, MenuItem itemDeletar) {
        itemDeletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                final Dialog dialog = new Dialog(PerfilActivity.this);
                dialog.setContentView(R.layout.dialog_deletar_bebida);
                dialog.setTitle("Confirmar para deletar...");

                final TextView dialogNome = (TextView) dialog.findViewById(R.id.dialog_bebida_nome);
                dialogNome.setText(bebida.getNome() + " - " + bebida.getQuantidade());

                final TextView dialogPreco = (TextView) dialog.findViewById(R.id.dialog_bebida_preco);
                dialogPreco.setText("R$ " + String.valueOf(bebida.getPreco()));

                Button dialogSalvar = (Button) dialog.findViewById(R.id.dialog_bebida_salvar);
                Button dialogCancelar = (Button) dialog.findViewById(R.id.dialog_bebida_cancelar);

                dialogSalvar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deletaBebidaFirebase(bebida);
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

    private void deletaBebidaFirebase(Bebida bebida) {

        dbBar.child("bebidas").child(bebida.getIdFirebase()).removeValue();

    }

    private void carregaFotoPerfil() {

        if (mypath != null){
            campoFotoPerfil.setImageBitmap(BitmapFactory.decodeFile(mypath.getAbsolutePath()));
            campoFotoPerfil.setScaleType(ImageView.ScaleType.FIT_XY);
            campoFotoPerfil.setTag(Uri.parse(mypath.getPath()));
//            campoFotoPerfil.setVisibility(View.VISIBLE);
        }

        baixaFotoPerfilFirebase();

    }

    private void baixaFotoPerfilFirebase() {
        try {

            final File localFile = File.createTempFile("images", "jpg");

            storageRef.child("perfil").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

//                    campoFotoPerfil.setVisibility(View.VISIBLE);
                    campoFotoPerfil.setImageBitmap(bitmap);
                    campoFotoPerfil.setScaleType(ImageView.ScaleType.FIT_XY);
                    campoFotoPerfil.setTag(Uri.parse(localFile.getPath()));

                    //salvando imagem no app

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(mypath);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();
                    } catch (Exception e) {
                        Log.e("SAVE_IMAGE", e.getMessage(), e);
                    }

                    Toast.makeText(PerfilActivity.this, "Sucesso ao fazer download de foto perfil" + bitmap == null ? "null":"nao nulo", Toast.LENGTH_LONG).show();
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


    private void iniciaPerfil() {
        carregaBar();
        carregaListaBebidas();
        carregaFotoPerfil();
    }

}
