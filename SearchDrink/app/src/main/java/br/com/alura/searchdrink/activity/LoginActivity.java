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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.BreakIterator;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.modelo.User;

public class LoginActivity extends BaseActivity
        implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "login";
    private static String OBRIGATORIO = "Obrigatório.";

    private static final int RC_SIGN_IN_GOOGLE = 9001;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference database;

    private CallbackManager mCallbackManager;

    private GoogleApiClient mGoogleApiClient;
    private BreakIterator mStatusTextView;

    private EditText campoEmail;
    private EditText campoSenha;
    private TextView campoStatus;
    private CheckBox cadastroCheckBox;
    private LinearLayout cadastroFirebase;
    private LoginButton loginButtonFacebook;
    private SignInButton loginButtonGoogle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_login);

        database = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();

//        FirebaseUser mUser = mAuth.getCurrentUser();
//        if (mUser != null) {
//            // User is signed in
//            Intent intent = new Intent(getApplicationContext(), PerfilBarActivity.class);
//            String uid = mAuth.getCurrentUser().getUid();
//            String image = mAuth.getCurrentUser().getPhotoUrl().toString();
//            intent.putExtra("user_id", uid);
//            if(image!=null || image!=""){
//                intent.putExtra("profile_picture",image);
//            }
//            startActivity(intent);
//            finish();
//            Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
//        }

        verificaUsuarioLogado();

        inicializaLoginComGoogle();

        inicializaLoginComFacebook();

//        inicializaLoginComEmailSenha();

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

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                try {
                    throw new Exception("Falha login com google");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //

    private void signInWithGoogle() {

        if (mGoogleApiClient.isConnected())
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
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
                            User user = new User(uid,name,email, image);

                            database.child(uid).setValue(user);

                            Intent intent = new Intent(getApplicationContext(), MapaBarActivity.class);
                            intent.putExtra("user_id",uid);
                            intent.putExtra("profile_picture",image);
                            startActivity(intent);
                            finish();
                        }

                        hideProgressDialog();
                    }
                });
    }

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
                            Toast.makeText(LoginActivity.this, "Falha na autenticação / Você já pode ter sido cadastrado com outro provedor!",
                                    Toast.LENGTH_SHORT).show();

                        }else{
                            String uid=task.getResult().getUser().getUid();
                            String name=task.getResult().getUser().getDisplayName();
                            String email=task.getResult().getUser().getEmail();
                            String image=task.getResult().getUser().getPhotoUrl().toString();

                            //Create a new User and Save it in Firebase database
                            User user = new User(uid,name,email, image);

                            database.child(uid).setValue(user);

                            Intent intent = new Intent(getApplicationContext(), MapaBarActivity.class);
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

        Intent vaiParaPerfil = new Intent(LoginActivity.this, PerfilBarActivity.class);

        String uid = mAuth.getCurrentUser().getUid();
        String image = mAuth.getCurrentUser().getPhotoUrl().toString();
        vaiParaPerfil.putExtra("user_id", uid);
        if(image!=null || image!=""){
            vaiParaPerfil.putExtra("profile_picture",image);
        }

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
//        Bar usuario = new Bar(user.getUid(), usernameFromEmail(user.getEmail()), user.getEmail(), null, null, null, null, null);
        User usuario = new User(user.getUid(), usernameFromEmail(user.getEmail()),user.getEmail(), null);

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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    private void verificaUsuarioLogado() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    // User is signed in
                    Intent intent = new Intent(getApplicationContext(), MapaBarActivity.class);
//                    String uid = mAuth.getCurrentUser().getUid();
//                    String image = mAuth.getCurrentUser().getPhotoUrl().toString();
//                    intent.putExtra("user_id", uid);
//                    if(image!=null || image!=""){
//                        intent.putExtra("profile_picture",image);
//                    }
                    startActivity(intent);
                    finish();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };
    }


    private void inicializaLoginComGoogle() {
        //login com Google
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        loginButtonGoogle = (SignInButton)findViewById(R.id.login_button_google);
        loginButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });
        //
    }


    private void inicializaLoginComFacebook() {
        //login com FaceBook
        LoginManager.getInstance().logOut();
        mCallbackManager = CallbackManager.Factory.create();
        loginButtonFacebook = (LoginButton) findViewById(R.id.login_button_facebook);
        loginButtonFacebook.setReadPermissions("email", "public_profile");
        loginButtonFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
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
        //
    }


    private void inicializaLoginComEmailSenha() {
        cadastroFirebase = (LinearLayout) findViewById(R.id.login_cadastro_firebase);

//        cadastroCheckBox = (CheckBox) findViewById(R.id.login_cadastro_check);
//        cadastroCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    cadastroFirebase.setVisibility(View.VISIBLE);
//                } else {
//                    cadastroFirebase.setVisibility(View.GONE);
//                }
//            }
//        });

        campoEmail = (EditText) findViewById(R.id.login_email);
        campoSenha = (EditText) findViewById(R.id.login_senha);
        campoStatus = (TextView) findViewById(R.id.login_status);

        Button entrar = (Button) findViewById(R.id.login_entrar);
        Button cadastrar = (Button) findViewById(R.id.login_cadastrar);

        //click
        entrar.setOnClickListener(this);
        cadastrar.setOnClickListener(this);
    }
}
