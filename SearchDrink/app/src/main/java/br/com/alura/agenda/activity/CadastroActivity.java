package br.com.alura.agenda.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import br.com.alura.agenda.CadastroHelper;
import br.com.alura.agenda.R;
import br.com.alura.agenda.dao.BarDAO;
import br.com.alura.agenda.modelo.Bar;

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

                Intent vaiParaGaleria = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                vaiParaGaleria.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(vaiParaGaleria, "Selecione uma imagem"), CODIGO_GALERIA);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == CODIGO_CAMERA){
            //imagem da camera
            helper.carregaFoto(caminhoFoto);
        }

        else if(requestCode == CODIGO_GALERIA && resultCode == RESULT_OK){
            //imagem da galeria
            Uri selectedImage = data.getData();
            helper.carregaFoto(selectedImage.toString());
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
