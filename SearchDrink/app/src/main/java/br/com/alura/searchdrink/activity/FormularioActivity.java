package br.com.alura.searchdrink.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import br.com.alura.searchdrink.FormularioHelper;
import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.modelo.Bar;

public class FormularioActivity extends BaseActivity {

    public static final int CODIGO_CAMERA = 567;
    public static final int CODIGO_GALERIA = 1;

    private FormularioHelper helper;
    private String caminhoFoto = "";

    private DatabaseReference dbBar;
//    private DatabaseReference database;
    private StorageReference storageReference;

    private String uId;
    private Uri uriFoto = null;

    private File mypath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        uId = getUid();

        dbBar = FirebaseDatabase.getInstance().getReference().child("bares").child(uId);

        storageReference = FirebaseStorage.getInstance().getReference().child(uId);

        this.helper = new FormularioHelper(this, storageReference);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("profile", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        mypath = new File(directory, "perfil" + uId + ".jpg");

        dbBar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map <String, String> mapBar = (Map)dataSnapshot.getValue();
                String nome = mapBar.get("nome");
                String endereco = mapBar.get("endereco");
                String telefone = mapBar.get("telefone");
                String site = mapBar.get("site");
                String email = mapBar.get("email");


                Bar bar = new Bar(nome, endereco, telefone, site, email);

                helper.preencheFormulario(bar, mypath);
                
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button botaoFoto = (Button)findViewById(R.id.formulario_botao_foto);
        botaoFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), CODIGO_GALERIA);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_GALERIA && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uriFoto = data.getData();

            helper.carregaFoto(uriFoto, getContentResolver());

        }
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

                dbBar.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        enviaCadastroFirebase(bar);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                uploadFotoPerfilFirebase();


//                Toast.makeText(FormularioActivity.this, "Bar " + bar.getNome() + " salvo com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void enviaCadastroFirebase(Bar bar) {

        Map<String, Object> valoresBar = bar.toMap();


        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("bares/" + uId + "/perfil/", valoresBar);
//        childUpdates.put("bares/" + uId, valoresBar);

        dbBar.updateChildren(valoresBar);
    }

    private void uploadFotoPerfilFirebase() {
        if(uriFoto != null) {

            StorageReference riversRef = storageReference.child(uriFoto.getLastPathSegment());
            UploadTask uploadTask = storageReference.child("perfil").putFile(uriFoto);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(FormularioActivity.this, "Falha ao fazer upload de foto", Toast.LENGTH_LONG).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    dbBar.child("uriFotoPerfil").setValue(downloadUrl);
                    Toast.makeText(FormularioActivity.this, "Sucesso ao fazer upload de foto", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
