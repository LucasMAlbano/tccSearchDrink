package br.com.alura.searchdrink.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.com.alura.searchdrink.FormularioHelper;
import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.dao.BarDAO;
import br.com.alura.searchdrink.modelo.Bar;

public class FormularioActivity extends BaseActivity {

    public static final int CODIGO_CAMERA = 567;
    public static final int CODIGO_GALERIA = 1;

    private FormularioHelper helper;
    private String caminhoFoto = "";

    private DatabaseReference database;
    private StorageReference storageReference;

    private String uId;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        this.helper = new FormularioHelper(this);

        database = FirebaseDatabase.getInstance().getReference();

        uId = getUid();

//        storageReference = FirebaseStorage.getInstance().getReference("gs://wazebar.appspot.com").child(uId).child("images");
        storageReference = FirebaseStorage.getInstance().getReference().child(uId);

        database.child("bares").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map <String, String> mapBar = (Map)dataSnapshot.getValue();
                String nome = mapBar.get("nome");
                String endereco = mapBar.get("endereco");
                String telefone = mapBar.get("telefone");
                String site = mapBar.get("site");
                String email = mapBar.get("email");

                Bar bar = new Bar(nome, endereco, telefone, site, email);

                helper.preencheFormulario(bar);
                
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

            uri = data.getData();

            helper.carregaFoto(uri, getContentResolver());

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

                database.child("bares").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        enviaCadastro(bar);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

//                if(uri != null) {
//                    StorageReference riversRef = storageReference.child(uri.getLastPathSegment());
                    UploadTask uploadTask = storageReference.child(uri.getLastPathSegment()).putFile(uri);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(FormularioActivity.this, "Falha", Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(FormularioActivity.this, "Sucesso", Toast.LENGTH_LONG).show();
                        }
                    });
//                }

//                Toast.makeText(FormularioActivity.this, "Bar " + bar.getNome() + " salvo com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void enviaCadastro(Bar bar) {

        Map<String, Object> valoresBar = bar.toMap();


        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("bares/" + uId + "/perfil/", valoresBar);
        childUpdates.put("bares/" + uId, valoresBar);

        database.updateChildren(childUpdates);
    }
}
