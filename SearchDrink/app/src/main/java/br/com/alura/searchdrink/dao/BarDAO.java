package br.com.alura.searchdrink.dao;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.alura.searchdrink.FormularioHelper;
import br.com.alura.searchdrink.activity.FormularioActivity;
import br.com.alura.searchdrink.activity.MapaBarActivity;
import br.com.alura.searchdrink.modelo.Bar;

/**
 * Created by Birbara on 20/07/2016.
 */
public class BarDAO {

    private final Context context;

    private DatabaseReference dbBar;
    private DatabaseReference dbUser;
    private StorageReference storageReference;

    private String uId;

    public BarDAO(Context context, String uId){

        this.context = context;

        this.uId = uId;

        dbBar = FirebaseDatabase.getInstance().getReference().child("bares").child(uId);
        dbUser = FirebaseDatabase.getInstance().getReference().child("users").child(uId);
        storageReference = FirebaseStorage.getInstance().getReference().child(uId);
    }


    public void cadastraBar(final Bar bar) {
        dbBar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> valoresBar = bar.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("bares/" + uId + "/perfil/", valoresBar);
//        childUpdates.put("bares/" + uId, valoresBar);
                dbBar.updateChildren(valoresBar);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void pegaBarEPreencheFormulario(final FormularioHelper helper) {

        final Bar[] bar = {null};

        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, String> mapBar = (Map) dataSnapshot.getValue();
                String nome = mapBar.get("nome");
                String email = mapBar.get("email");
                String uriFotoPerfil = String.valueOf(mapBar.get("uriFoto"));


                bar[0] = new Bar(uId, nome, email, uriFotoPerfil, "null", null, null, null);

                helper.preencheFormulario(bar[0]);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    public void uploadFotoPerfilFirebase(Uri uriFoto) {
//        if(uriFoto != null) {
//
//            StorageReference riversRef = storageReference.child(uriFoto.getLastPathSegment());
//            UploadTask uploadTask = storageReference.child("perfil").putFile(uriFoto);
//
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle unsuccessful uploads
//                    Toast.makeText(context, "Falha ao fazer upload de foto", Toast.LENGTH_LONG).show();
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                    dbBar.child("uriFotoPerfil").setValue(downloadUrl);
//                    Toast.makeText(context, "Sucesso ao fazer upload de foto", Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//    }

//    public Bitmap downloadFotoPerfilFirebase(){
//
//        final Bitmap[] bitmap = new Bitmap[1];
//
//        try {
//            final File localFile = File.createTempFile("images", "jpg");
//            storageReference.child("perfil").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//
//                    bitmap[0] = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                    bitmap[0] = Bitmap.createScaledBitmap(bitmap[0], 200, 200, true);
//
////                    Toast.makeText(PerfilBarActivity.this, "Sucesso ao fazer download de foto perfil" + bitmap == null ? "null":"nao nulo", Toast.LENGTH_LONG).show();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(context, "Falha ao fazer download de foto perfil", Toast.LENGTH_LONG).show();
//                }
//            });
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return  bitmap[0];
//    }

    public void enviaCadastroFirebase(Bar bar) {

        Map<String, Object> valoresBar = bar.toMap();


        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("bares/" + uId + "/perfil/", valoresBar);
//        childUpdates.put("bares/" + uId, valoresBar);

        dbBar.updateChildren(valoresBar);
    }


    @NonNull
    private ContentValues pegaDadosDoAluno(Bar bar) {
        ContentValues dados = new ContentValues();
        dados.put("nome", bar.getNome());
        dados.put("endereco", bar.getEndereco());
        dados.put("telefone", bar.getTelefone());
        dados.put("site", bar.getSite());
//        dados.put("caminhoFoto", barClicado.getUriFoto());
        dados.put("email", bar.getEmail());
//        dados.put("senha", barClicado.getSenha());
        return dados;
    }
}
