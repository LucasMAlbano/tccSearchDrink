package br.com.alura.searchdrink.activity;

import android.app.Activity;
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

import br.com.alura.searchdrink.CadastroHelper;
import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.dao.BarDAO;
import br.com.alura.searchdrink.modelo.Bar;

public class CadastroActivity extends AppCompatActivity {

    public static final int CODIGO_CAMERA = 567;
    public static final int CODIGO_GALERIA = 1234;

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


                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, CODIGO_GALERIA);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (resultCode == Activity.RESULT_OK && requestCode == CODIGO_CAMERA){
//            //imagem da camera
//            helper.carregaFoto(caminhoFoto);
//        }

        if(requestCode == CODIGO_GALERIA && resultCode == RESULT_OK){
            //imagem da galeria

            //1
            Uri selectedImage = data.getData();
            helper.carregaFoto(selectedImage.toString());

            //2
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//            ImageView imageView = (ImageView) findViewById(R.id.cadastro_foto);
//            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            //3
//            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//            helper.carregaFoto(picturePath);


            //4
//            Uri photoUri = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String filePath = cursor.getString(columnIndex);
//            cursor.close();
//            Log.v("Load Image", "Gallery File Path=====>>>"+filePath);
//            Log.v("Load Image", "Image List Size=====>>>"+filePath.toString());
//            Bitmap bitmap = BitmapFactory.decodeFile(filePath.trim());
//            bitmap = Bitmap.createScaledBitmap(bitmap,500, 500, true);
//            Drawable d=new BitmapDrawable(bitmap);

            Toast.makeText(getApplicationContext(), selectedImage.toString(), Toast.LENGTH_LONG).show();
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
                dao.close();

                Toast.makeText(CadastroActivity.this, "Bar " + bar.getNome() + " salvo com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
