package br.com.alura.searchdrink.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.fragment.MapaFragment;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.modelo.User;
import br.com.alura.searchdrink.task.ImageLoadTask;

public class PerfilUsuarioActivity extends BaseActivity {

    private Button buttonSouBar;
    private Button buttonSair;
    private TextView campoBemVindo;
    private ImageView campoFoto;

    private String uId;
    private User user;

    private DatabaseReference dbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_perfil_usuario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);

        uId = getUid();

        dbUser = FirebaseDatabase.getInstance().getReference().child("users");

        campoBemVindo = (TextView) findViewById(R.id.perfil_usuario_bemvindo);

        campoFoto = (ImageView) findViewById(R.id.perfil_usuario_foto);

        buttonSouBar = (Button) findViewById(R.id.perfil_usuario_button_sou_bar);
        buttonSair = (Button) findViewById(R.id.perfil_usuario_button_sair);

        buttonSouBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilUsuarioActivity.this, FormularioActivity.class);
//                        intent.putExtra("tipo", "cadastro");
                startActivity(intent);

                finish();
            }
        });

        buttonSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(PerfilUsuarioActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    if (snapshot.getKey().equals(uId)){
                        Map <String, Object> mapUser = (HashMap<String, Object>)snapshot.getValue();
                        String nome = String.valueOf(mapUser.get("nome"));
                        String email = String.valueOf(mapUser.get("email"));
                        String id = String.valueOf(mapUser.get("uId"));
                        String uriFoto = String.valueOf(mapUser.get("uriFoto"));

                        user = new User(id, nome, email, uriFoto);

                        break;
                    }
                }

                if (user != null){
                    campoBemVindo.setText("Bem vindo(a) " + user.getNome() + "!");
                    if(!user.getUriFoto().equals(null) && !user.getUriFoto().equals("null") && !user.getUriFoto().equals(""))
                        new ImageLoadTask(user.getUriFoto(), campoFoto).execute();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
