package br.com.alura.agenda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usuario = (EditText) findViewById(R.id.login_usuario);
        EditText senha = (EditText) findViewById(R.id.login_senha);

        Button entrar = (Button) findViewById(R.id.login_entrar);
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
