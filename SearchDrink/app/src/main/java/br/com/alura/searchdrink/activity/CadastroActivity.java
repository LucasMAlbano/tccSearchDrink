package br.com.alura.searchdrink.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;

import br.com.alura.searchdrink.CadastroHelper;
import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.dao.BarDAO;
import br.com.alura.searchdrink.modelo.Bar;

public class CadastroActivity extends AppCompatActivity {

    public static final int CODIGO_CAMERA = 567;
    public static final int CODIGO_GALERIA = 1;

    private CadastroHelper helper;
    private String caminhoFoto = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        this.helper = new CadastroHelper(this);

//        Intent intent = getIntent();
//        Bar bar = (Bar) intent.getSerializableExtra("bar");

//        if (bar != null){
//            this.helper.preencheFormulario(bar);
//        }

        Button botaoFoto = (Button)findViewById(R.id.formulario_botao_foto);
        botaoFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent vaiParaCamera= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                caminhoFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
//                File arquivoFoto = new File(caminhoFoto);
//                vaiParaCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(arquivoFoto));
//                startActivityForResult(vaiParaCamera, CODIGO_CAMERA);

//                Intent vaiParaGaleria = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                vaiParaGaleria.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(vaiParaGaleria, "Selecione uma imagem"), CODIGO_GALERIA);

//                Intent i = new Intent(
//                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//                startActivityForResult(i, CODIGO_GALERIA);


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

            Uri uri = data.getData();

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
                Bar bar = helper.pegaBar();
                BarDAO dao = new BarDAO(this);

                if(bar.getId() == 0) {
                    dao.insere(bar);
                } else{
                    dao.altera(bar);
                }
//                dao.close();

                Toast.makeText(CadastroActivity.this, "Bar " + bar.getNome() + " salvo com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
