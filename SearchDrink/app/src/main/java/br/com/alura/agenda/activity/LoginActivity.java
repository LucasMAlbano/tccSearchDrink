package br.com.alura.agenda.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.com.alura.agenda.R;
import br.com.alura.agenda.dao.BarDAO;

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
                if (dao.ehCadastrado(usuario.toString(), senha.toString()){
                    Intent vaiParaPerfil = new Intent(LoginActivity.this, PerfilActivity.class);
                    startActivity(vaiParaPerfil);
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
