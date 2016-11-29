package br.com.alura.searchdrink.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.database.DatabaseReference;

import br.com.alura.searchdrink.FormularioHelper;
import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.dao.BarDAO;
import br.com.alura.searchdrink.dao.FormularioDAO;
import br.com.alura.searchdrink.fragment.MapaFragment;
import br.com.alura.searchdrink.modelo.Bar;

public class FormularioActivity extends BaseActivity {

    public static final int CODIGO_CAMERA = 567;
    public static final int CODIGO_GALERIA = 1;

    private FormularioHelper helper;

    private String caminhoFoto = "";

//    private DatabaseReference dbBar;
    private DatabaseReference dbFiltrosBar;
//    private DatabaseReference database;
//    private StorageReference storageReference;

    private String uId;

//    private Uri uriFoto = null;

//    private File mypath;

//    private String tipo;

//    private List<String> tiposBares;

    private BarDAO barDAO;
    private FormularioDAO formularioDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_formulario);

//        Intent intent = getIntent();
//        tipo = (String) intent.getSerializableExtra("tipo");

        uId = getUid();

        barDAO = new BarDAO(this, uId);

        helper = new FormularioHelper(this, uId);


        //Preenche formulário de cadastro
        if (MapaBarActivity.verificadorSeUsuarioEhBar)
            helper.preencheFormulario(PerfilBarActivity.bar);
        else
            barDAO.pegaBarEPreencheFormulario(helper);

//        Bar barClicado = barDAO.pegaBar();
//        helper.preencheFormulario(barClicado);

//        dbBar = FirebaseDatabase.getInstance().getReference().child("bares").child(uId);
//        dbFiltrosBar = FirebaseDatabase.getInstance().getReference().child("filtros").child("tipoBar");

//        storageReference = FirebaseStorage.getInstance().getReference().child(uId);

//        tiposBares = new ArrayList<String>();


//        ValueEventListener listenerTiposBares = pegaTiposBares();
//        dbFiltrosBar.addListenerForSingleValueEvent(listenerTiposBares);

//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        File directory = cw.getDir("profile", Context.MODE_PRIVATE);
//        if (!directory.exists()) {
//            directory.mkdir();
//        }
//        mypath = new File(directory, "perfil" + uId + ".jpg");


//        Button botaoFoto = (Button)findViewById(R.id.formulario_botao_foto);
//        botaoFoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent();
//                // Show only images, no videos or anything else
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                // Always show the chooser (if there are multiple options available)
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), CODIGO_GALERIA);
//            }
//        });
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Cancelar?");
        alertDialogBuilder
                .setMessage("Sim para cancelar!")
                .setCancelable(false)
                .setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == CODIGO_GALERIA && resultCode == RESULT_OK && data != null && data.getData() != null) {
//
//            uriFoto = data.getData();
//
//            formularioDAO.getHelper().carregaFoto(uriFoto, getContentResolver()/*, storageReference*/);
//
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menu_formulario, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_formulario_ok:

                final Bar bar = helper.pegaBar();

                if (bar != null) {

//                    dbBar.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            enviaCadastroFirebase(barClicado);
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });

                    barDAO.enviaCadastroFirebase(bar);

//                    barDAO.uploadFotoPerfilFirebase(uriFoto);
//                    uploadFotoPerfilFirebase();

                    if (MapaBarActivity.verificadorSeUsuarioEhBar == false) {
                        MapaBarActivity.verificadorSeUsuarioEhBar = true;
                        MapaFragment.estabelecimentos.add(bar);
                        Intent vaiParaPerfil = new Intent(FormularioActivity.this, PerfilBarActivity.class);
                        startActivity(vaiParaPerfil);
                    }

//                Toast.makeText(FormularioActivity.this, "Bar " + barClicado.getNome() + " salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }



//    private void uploadFotoPerfilFirebase() {
//        if(uriFoto != null) {
//
//            StorageReference riversRef = storageReference.child(uriFoto.getLastPathSegment());
//            UploadTask uploadTask = storageReference.child("perfil").putFile(uriFoto);
//
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle unsuccessful uploads
//                    Toast.makeText(FormularioActivity.this, "Falha ao fazer upload de foto", Toast.LENGTH_LONG).show();
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                    dbBar.child("uriFotoPerfil").setValue(downloadUrl);
//                    Toast.makeText(FormularioActivity.this, "Sucesso ao fazer upload de foto", Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//    }

//    @NonNull
//    private ValueEventListener pegaTiposBares() {
//        return new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.i("AsyncTask", "doInbackground 2: " + Thread.currentThread().getName());
//
////                synchronized (tiposBares) {
//
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
//                        String tipo = String.valueOf(map.get("nome"));
//                        tiposBares.add(tipo);
//                    }
//
//                tiposBares.add(0, "selecione uma opção");
//
////                    tiposBares.notifyAll();
//
////                }
//
//                FormularioActivity.this.helper = new FormularioHelper(FormularioActivity.this, tiposBares);
//
//                ValueEventListener listenerPreenche = preencheFormulario();
//                dbBar.addListenerForSingleValueEvent(listenerPreenche);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w("AsyncTask", "loadPost:onCancelled", databaseError.toException());
//                // ...
//            }
//        };
//    }

//    private ValueEventListener preencheFormulario() {
//        return new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Map <String, String> mapBar = (Map)dataSnapshot.getValue();
//                String nome = mapBar.get("nome");
//                String endereco = mapBar.get("endereco");
//                String telefone = mapBar.get("telefone");
//                String site = mapBar.get("site");
//                String email = mapBar.get("email");
//                String tipoBar = mapBar.get("tipoBar");
//
//
//                Bar barClicado = new Bar(nome, endereco, telefone, site, email, tipoBar);
//
//                helper.preencheFormulario(barClicado, mypath);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//    }
}
