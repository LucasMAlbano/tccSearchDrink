package br.com.alura.searchdrink.activity;

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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    private String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        this.helper = new FormularioHelper(this);

        database = FirebaseDatabase.getInstance().getReference();

        uId = getUid();

        database.child("bares").child(uId).child("perfil").addListenerForSingleValueEvent(new ValueEventListener() {
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

//        database.child("bares").child(uId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                Map<String, String> mapBar = dataSnapshot.getValue(Map.class);
//
////                String nome = mapBar.get("nome");
////                String endereco = mapBar.get("endereco");
////                String telefone = mapBar.get("telefone");
////                String site = mapBar.get("site");
////
////                Bar bar = new Bar(nome, endereco, telefone, site);
////
////                helper.preencheFormulario(bar);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

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
//                BarDAO dao = new BarDAO(this);

//                if(bar.getuId() == 0) {
//                    dao.insere(bar);
//                } else{
//                    dao.altera(bar);
//                }
//                dao.close();

                        Toast.makeText(FormularioActivity.this, "Bar " + bar.getNome() + " salvo com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void enviaCadastro(Bar bar) {

        Map<String, Object> valoresBar = bar.toMap();


        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("bares/" + uId + "/perfil/", valoresBar);

        database.updateChildren(childUpdates);
    }
}
