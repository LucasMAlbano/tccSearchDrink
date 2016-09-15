package br.com.alura.searchdrink.activity;

import android.app.AlertDialog;
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

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.dao.BarDAO;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "login";
    private FirebaseAuth auth;

    private FirebaseAuth.AuthStateListener authListener;
    private EditText campoEmail;
    private EditText campoSenha;
    private TextView campoStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else{
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        campoEmail = (EditText) findViewById(R.id.login_email);
        campoSenha = (EditText) findViewById(R.id.login_senha);
        campoStatus = (TextView) findViewById(R.id.login_status);

        Button entrar = (Button) findViewById(R.id.login_entrar);
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entraConta(campoEmail.getText().toString(), campoSenha.getText().toString());
            }
        });

        Button cadastrar = (Button) findViewById(R.id.login_cadastrar);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                criaConta(campoEmail.getText().toString(), campoSenha.getText().toString());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authListener);
    }

    private void criaConta(String email, String senha) {
        Log.d(TAG, "createAccount:" + email);
        if (!validaCampos()) {
            return;
        }

        showProgressDialog();

        auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Falha na Autenticação",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(LoginActivity.this, "Usuário cadastrado com sucesso!",
                                    Toast.LENGTH_SHORT).show();
                            Intent vaiParaPerfil = new Intent(LoginActivity.this, PerfilActivity.class);
                            startActivity(vaiParaPerfil);
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void entraConta(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validaCampos()) {
            return;
        }

        showProgressDialog();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Falha na Autenticação",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent vaiParaPerfil = new Intent(LoginActivity.this, PerfilActivity.class);
                            startActivity(vaiParaPerfil);
                        }


                        if (!task.isSuccessful()) {
                            campoStatus.setText("Falha na Autenticação");
                            campoStatus.setVisibility(1);
                        }

                        hideProgressDialog();
                    }
                });
    }

    private boolean validaCampos() {
        boolean validador = true;

        String email = campoEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            campoEmail.setError("Obrigatório.");
            validador = false;
        } else {
            campoEmail.setError(null);
        }

        String senha = campoSenha.getText().toString();
        if (TextUtils.isEmpty(senha)) {
            campoSenha.setError("Obrigatório.");
            validador = false;
        } else {
            campoSenha.setError(null);
        }

        return validador;
    }

}
