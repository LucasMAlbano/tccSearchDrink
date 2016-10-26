package br.com.alura.searchdrink.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.modelo.Bar;

public class PerfilUsuarioActivity extends BaseActivity {

    private Button buttonSouBar;
    private Button buttonSair;

    private DatabaseReference dbBar;
    private DatabaseReference dbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_perfil_usuario);

        dbUser = FirebaseDatabase.getInstance().getReference().child("users");
        dbBar = FirebaseDatabase.getInstance().getReference().child("bares");

        buttonSouBar = (Button) findViewById(R.id.perfil_usuario_button_sou_bar);
        buttonSair = (Button) findViewById(R.id.perfil_usuario_button_sair);

        buttonSouBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Bar bar = dataSnapshot.getValue(Bar.class);
                        dbBar.child(dataSnapshot.getKey()).setValue(bar);

                        Intent intent = new Intent(PerfilUsuarioActivity.this, PerfilBarActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        buttonSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(PerfilUsuarioActivity.this, LoginActivity.class));
            }
        });
    }
}
