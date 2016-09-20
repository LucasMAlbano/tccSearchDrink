package br.com.alura.searchdrink.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.modelo.Bar;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "login";

    private FirebaseAuth auth;
    private DatabaseReference database;
//    private FirebaseAuth.AuthStateListener authListener;

    private EditText campoEmail;
    private EditText campoSenha;
    private TextView campoStatus;
    private static String OBRIGATORIO = "Obrigatório.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

//        authListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if(user != null){
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                }
//                else{
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//            }
//        };

        campoEmail = (EditText) findViewById(R.id.login_email);
        campoSenha = (EditText) findViewById(R.id.login_senha);
        campoStatus = (TextView) findViewById(R.id.login_status);

        Button entrar = (Button) findViewById(R.id.login_entrar);
        Button cadastrar = (Button) findViewById(R.id.login_cadastrar);

        //click
        entrar.setOnClickListener(this);
        cadastrar.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        auth.addAuthStateListener(authListener);

        if (auth.getCurrentUser() != null) {
            onAuthSuccess();
        }
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        auth.removeAuthStateListener(authListener);
//    }

    private void entraConta() {
        Log.d(TAG, "login");
        if (!validaCampos()) {
            return;
        }

        showProgressDialog();

        String email = campoEmail.getText().toString();
        String senha = campoSenha.getText().toString();

        auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onAuthSuccess();
                        }
                        else{
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Falha na Autenticação",
                                    Toast.LENGTH_SHORT).show();
                            campoStatus.setText("Falha na Autenticação");
                            campoStatus.setVisibility(1);
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void cadastraConta() {
        Log.d(TAG, "cadastro");
        if (!validaCampos()) {
            return;
        }

        showProgressDialog();

        String email = campoEmail.getText().toString();
        String senha = campoSenha.getText().toString();

        auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            writeNewUser(task.getResult().getUser());
                            onAuthSuccess();
                            Toast.makeText(LoginActivity.this, "Usuário cadastrado com sucesso!",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LoginActivity.this, "Falha na Autenticação",
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }
                });
    }

    private boolean validaCampos() {
        boolean validador = true;

        String email = campoEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            campoEmail.setError(OBRIGATORIO);
            validador = false;
        } else {
            campoEmail.setError(null);
        }

        String senha = campoSenha.getText().toString();
        if (TextUtils.isEmpty(senha)) {
            campoSenha.setError(OBRIGATORIO);
            validador = false;
        } else {
            campoSenha.setError(null);
        }

        return validador;
    }

    private void onAuthSuccess() {

        Intent vaiParaPerfil = new Intent(LoginActivity.this, PerfilActivity.class);
        startActivity(vaiParaPerfil);

        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void writeNewUser(FirebaseUser user) {
        Bar usuario = new Bar(usernameFromEmail(user.getEmail()), user.getEmail());

        database.child("bares").child(user.getUid()).setValue(usuario.getEmail());
        database.child("bares").child(user.getUid()).child("perfil").child("nome").setValue(usuario.getNome());
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.login_entrar) {
            entraConta();
        } else if (i == R.id.login_cadastrar) {
            cadastraConta();
        }
    }
}
