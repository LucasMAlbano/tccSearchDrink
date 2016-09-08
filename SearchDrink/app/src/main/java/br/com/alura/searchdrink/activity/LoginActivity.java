package br.com.alura.searchdrink.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.dao.BarDAO;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usuario = (EditText) findViewById(R.id.login_usuario);
        final EditText senha = (EditText) findViewById(R.id.login_senha);

        Button entrar = (Button) findViewById(R.id.login_entrar);
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BarDAO dao = new BarDAO(LoginActivity.this);
                if (dao.ehCadastrado(usuario.toString(), senha.toString())){
                    Intent vaiParaPerfil = new Intent(LoginActivity.this, PerfilActivity.class);
                    startActivity(vaiParaPerfil);
                }
                else{
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(LoginActivity.this).
                                    setTitle("Erro").
                                    setMessage("Email ou Senha inválido!").
                                    setPositiveButton("OK", null);
                    builder.create().show();
                }
            }
        });

        Button cadastrar = (Button) findViewById(R.id.login_cadastrar);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent vaiParaCadastro = new Intent(LoginActivity.this, CadastroActivity.class);
                startActivity(vaiParaCadastro);
            }
        });

    }
}
