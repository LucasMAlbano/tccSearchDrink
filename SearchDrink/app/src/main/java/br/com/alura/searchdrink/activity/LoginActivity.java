package br.com.alura.searchdrink.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.modelo.Bar;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "login";
    private static String OBRIGATORIO = "Obrigatório.";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference database;

    private CallbackManager mCallbackManager;

    private EditText campoEmail;
    private EditText campoSenha;
    private TextView campoStatus;
    private CheckBox cadastroCheckBox;
    private LinearLayout cadastroFirebase;
    private LoginButton buttonFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_login);

        database = FirebaseDatabase.getInstance().getReference().child("bares");
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            // User is signed in
            Intent intent = new Intent(getApplicationContext(), MapaBarActivity.class);
            String uid = mAuth.getCurrentUser().getUid();
            String image=mAuth.getCurrentUser().getPhotoUrl().toString();
            intent.putExtra("user_id", uid);
            if(image!=null || image!=""){
                intent.putExtra("profile_picture",image);
            }
            startActivity(intent);
            finish();
            Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

        //FaceBook
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button_facebook);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                signInWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });




        cadastroFirebase = (LinearLayout) findViewById(R.id.login_cadastro_firebase);

        cadastroCheckBox = (CheckBox) findViewById(R.id.login_cadastro_check);
        cadastroCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cadastroFirebase.setVisibility(View.VISIBLE);
                } else {
                    cadastroFirebase.setVisibility(View.GONE);
                }
            }
        });

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


        mAuth.addAuthStateListener(mAuthListener);

//        if (mAuth.getCurrentUser() != null) {
//            onAuthSuccess();
//        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //FaceBook
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    //

    private void signInWithFacebook(AccessToken token) {
        Log.d(TAG, "signInWithFacebook:" + token);

        showProgressDialog();


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            String uid=task.getResult().getUser().getUid();
                            String name=task.getResult().getUser().getDisplayName();
                            String email=task.getResult().getUser().getEmail();
                            String image=task.getResult().getUser().getPhotoUrl().toString();

                            //Create a new User and Save it in Firebase database
                            Bar user = new Bar(uid,name,email);

                            database.child(uid).setValue(user);

                            Intent intent = new Intent(getApplicationContext(), PerfilActivity.class);
                            intent.putExtra("user_id",uid);
                            intent.putExtra("profile_picture",image);
                            startActivity(intent);
                            finish();
                        }

                        hideProgressDialog();
                    }
                });
    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        else{
                            Intent vaiParaMapa = new Intent(LoginActivity.this, MapaBarActivity.class);
                            startActivity(vaiParaMapa);

                            finish();
                        }
                    }
                });
    }

    private void entraConta() {
        Log.d(TAG, "login");
        if (!validaCampos()) {
            return;
        }

        showProgressDialog();

        String email = campoEmail.getText().toString();
        String senha = campoSenha.getText().toString();

        mAuth.signInWithEmailAndPassword(email, senha)
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
                            campoStatus.setText("Email ou senha inválidos.");
                            campoStatus.setVisibility(View.VISIBLE);
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

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            writeNewUser(task.getResult().getUser());
//                            onAuthSuccess();

                            Intent vaiParaFormulario = new Intent(LoginActivity.this, FormularioActivity.class);
                            vaiParaFormulario.putExtra("tipo", "cadastro");
                            startActivity(vaiParaFormulario);

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

        database.child("bares").child(user.getUid()).setValue(usuario);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();

        if (i == R.id.login_entrar) {
            entraConta();
        }

        else if (i == R.id.login_cadastrar) {
            cadastraConta();
        }
    }
}
