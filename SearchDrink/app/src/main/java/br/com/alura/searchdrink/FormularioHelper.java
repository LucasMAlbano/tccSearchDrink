package br.com.alura.searchdrink;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import br.com.alura.searchdrink.activity.FormularioActivity;
import br.com.alura.searchdrink.activity.PerfilActivity;
import br.com.alura.searchdrink.modelo.Bar;

/**
 * Created by Birbara on 20/07/2016.
 */
public class FormularioHelper {

    private final EditText campoNome;
    private final EditText campoEndereco;
    private final EditText campoTelefone;
    private final EditText campoSite;
    private final ImageView campoFoto;

    private final FormularioActivity activity;

    private Bar bar;

    private StorageReference storageRef;

    public FormularioHelper(FormularioActivity activity, StorageReference storageRef) {

        this.storageRef = storageRef;

        this.activity = activity;

        this.bar = new Bar();

        this.campoNome = (EditText) activity.findViewById(R.id.formulario_nome);
        this.campoEndereco = (EditText) activity.findViewById(R.id.formulario_endereco);
        this.campoTelefone = (EditText) activity.findViewById(R.id.formulario_telefone);
        this.campoSite = (EditText) activity.findViewById(R.id.formulario_site);
        this.campoFoto = (ImageView) activity.findViewById(R.id.formulario_foto);
    }

    public Bar pegaBar() {

        bar.setNome(campoNome.getText().toString());
        bar.setEndereco(campoEndereco.getText().toString());
        bar.setTelefone(campoTelefone.getText().toString());
        bar.setSite(campoSite.getText().toString());
        bar.setCaminhoFoto((Uri) campoFoto.getTag());

        return bar;
    }

    public void preencheFormulario(final Bar bar, File mypath) {

        this.bar = bar;
        campoNome.setText(bar.getNome());
        campoEndereco.setText(bar.getEndereco());
        campoTelefone.setText(bar.getTelefone());
        campoSite.setText(bar.getSite());

        if (mypath != null){
            campoFoto.setImageBitmap(BitmapFactory.decodeFile(mypath.getAbsolutePath()));
            campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
            campoFoto.setTag(Uri.parse(mypath.getPath()));
        }

//        carregaFoto(bar.getCaminhoFoto(), contentResolver);

    }


    public void carregaFoto(Uri uri, ContentResolver contentResolver) {

        if(uri != null) {

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                // Log.d(TAG, String.valueOf(bitmap));
                bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

                campoFoto.setImageBitmap(bitmap);
                campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
                campoFoto.setTag(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else{

            try {
                final File localFile = File.createTempFile("images", "jpg");
                storageRef.child("perfil").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

                        campoFoto.setImageBitmap(bitmap);
                        campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
                        campoFoto.setTag(Uri.parse(localFile.getPath()));

//                    Toast.makeText(PerfilActivity.this, "Sucesso ao fazer download de foto perfil" + bitmap == null ? "null":"nao nulo", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Falha ao fazer download de foto perfil", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        Picasso.with(activity).load(uri).into(campoFoto);
    }

//    private void carregaFotoPerfilFirebase(StorageReference storageRef) {
//        try {
//            final File localFile = File.createTempFile("images", "jpg");
//            storageRef.child("perfil").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//
//                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                    bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
//
//                    campoFoto.setImageBitmap(bitmap);
//                    campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
//                    campoFoto.setTag(Uri.parse(localFile.getPath()));
//
////                    Toast.makeText(PerfilActivity.this, "Sucesso ao fazer download de foto perfil" + bitmap == null ? "null":"nao nulo", Toast.LENGTH_LONG).show();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(activity, "Falha ao fazer download de foto perfil", Toast.LENGTH_LONG).show();
//                }
//            });
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
