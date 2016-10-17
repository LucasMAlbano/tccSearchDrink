package br.com.alura.searchdrink;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

import br.com.alura.searchdrink.activity.FormularioActivity;
import br.com.alura.searchdrink.dao.BarDAO;
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
    private final String uId;

    private final File mypath;

    private Bar bar;
    private List<String> tiposBar;

    private static String OBRIGATORIO = "Obrigatório.";

    private Spinner spinnerBares;

    private ArrayAdapter<String> spinnerAdapter;

    public FormularioHelper(final FormularioActivity activity, List<String> tiposBar, String uId) {

        this.activity = activity;
        this.tiposBar = tiposBar;
        this.uId = uId;
        this.bar = new Bar();

        this.campoNome = (EditText) activity.findViewById(R.id.formulario_nome);
        this.campoEndereco = (EditText) activity.findViewById(R.id.formulario_endereco);
        this.campoTelefone = (EditText) activity.findViewById(R.id.formulario_telefone);
        this.campoSite = (EditText) activity.findViewById(R.id.formulario_site);
        this.campoFoto = (ImageView) activity.findViewById(R.id.formulario_foto);

        addItensEmSpinnerBares();

        ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
        File directory = cw.getDir("profile", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        mypath = new File(directory, "perfil" + uId + ".jpg");
    }

    // add items into spinner dynamically
    public void addItensEmSpinnerBares() {
        spinnerBares = (Spinner) activity.findViewById(R.id.spinnerBares);

        spinnerAdapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, tiposBar);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBares.setAdapter(spinnerAdapter);

    }

    public Bar pegaBar() {

        if (!validaCampos()) {
            return null;
        }

        bar.setNome(campoNome.getText().toString());
        bar.setEndereco(campoEndereco.getText().toString());
        bar.setTelefone(campoTelefone.getText().toString());
        bar.setSite(campoSite.getText().toString());
        bar.setCaminhoFoto((Uri) campoFoto.getTag());
        bar.setTipoBar(String.valueOf(spinnerBares.getSelectedItem()));

        return bar;
    }

    public void preencheFormulario(final Bar bar) {

        this.bar = bar;
        campoNome.setText(bar.getNome());
        campoEndereco.setText(bar.getEndereco());
        campoTelefone.setText(bar.getTelefone());
        campoSite.setText(bar.getSite());
        spinnerBares.setPrompt(bar.getTipoBar());

        if (mypath != null){
            campoFoto.setImageBitmap(BitmapFactory.decodeFile(mypath.getAbsolutePath()));
            campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
            campoFoto.setTag(Uri.parse(mypath.getPath()));
        }

//        carregaFoto(bar.getCaminhoFoto(), contentResolver);

    }


    public void carregaFoto(Uri uri, ContentResolver contentResolver/*, StorageReference storageRef*/) {

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

            Bitmap bitmap = new BarDAO(activity, uId).downloadFotoPerfilFirebase();

            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            campoFoto.setImageBitmap(bitmap);
            campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
            campoFoto.setTag(Uri.parse(localFile.getPath()));

//            try {
//                final File localFile = File.createTempFile("images", "jpg");
//                storageRef.child("perfil").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//
//                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                        bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
//
//                        campoFoto.setImageBitmap(bitmap);
//                        campoFoto.setScaleType(ImageView.ScaleType.FIT_XY);
//                        campoFoto.setTag(Uri.parse(localFile.getPath()));
//
////                    Toast.makeText(PerfilActivity.this, "Sucesso ao fazer download de foto perfil" + bitmap == null ? "null":"nao nulo", Toast.LENGTH_LONG).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(activity, "Falha ao fazer download de foto perfil", Toast.LENGTH_LONG).show();
//                    }
//                });
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
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


    private boolean validaCampos() {

        boolean validador = true;

        String nome = campoNome.getText().toString();
        if (TextUtils.isEmpty(nome)) {
            campoNome.setError(OBRIGATORIO);
            validador = false;
        } else {
            campoNome.setError(null);
        }

        String endereco = campoEndereco.getText().toString();
        if (TextUtils.isEmpty(endereco)) {
            campoEndereco.setError(OBRIGATORIO);
            validador = false;
        } else {
            campoEndereco.setError(null);
        }

        String telefone = campoTelefone.getText().toString();
        if (TextUtils.isEmpty(telefone)) {
            campoTelefone.setError(OBRIGATORIO);
            validador = false;
        } else {
            campoTelefone.setError(null);
        }

        String spinner = spinnerBares.getSelectedItem().toString();
        if (spinner.equals("selecione uma opção")) {
            TextView errorText = (TextView)spinnerBares.getSelectedView();
            errorText.setError(OBRIGATORIO);
            errorText.setTextColor(Color.RED);
            errorText.setText(OBRIGATORIO);
            validador = false;
        }


        return validador;
    }
}
