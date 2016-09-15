package br.com.alura.searchdrink.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.com.alura.searchdrink.EnviaDadosServidor;
import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.adapter.BarAdapter;
import br.com.alura.searchdrink.dao.BarDAO;
import br.com.alura.searchdrink.modelo.Bar;

public class PerfilActivity extends AppCompatActivity {

    private static final int CODIGO_LIGACAO = 123;
    private static final int CODIGO_SMS = 124;

    private ListView listaAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        //verifica permissão recebimento de SMS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[] { Manifest.permission.RECEIVE_SMS } , CODIGO_SMS);
            }
        }

        listaAlunos = (ListView) findViewById(R.id.lista_alunos);

        Button novoAluno = (Button) findViewById(R.id.novo_aluno);
        novoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilActivity.this, CadastroActivity.class);
                startActivity(intent);
            }
        });

        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) { //AdapterView<?> adapterView = lista, View view = item, int i = posicao, long l = id

                Bar bar = (Bar) lista.getItemAtPosition(position);

                Intent intent = new Intent(PerfilActivity.this, CadastroActivity.class);
                intent.putExtra("bar", bar);
                startActivity(intent);
            }
        });

        //precisamos fazer com que o Android fique ciente da existência do menu de contexto
        registerForContextMenu(listaAlunos);
    }

    @Override
    protected void onResume() {
        super.onResume();

        carregaLista();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        final Bar bar = (Bar) listaAlunos.getItemAtPosition(info.position);

        //item site
        MenuItem itemSite = menu.add("Visitar site do bar");
        Intent intentSite = new Intent(Intent.ACTION_VIEW);
        String site = bar.getSite();
        if (!site.startsWith("http://")){
            site = "http://" + site;
        }
        intentSite.setData(Uri.parse(site));
        itemSite.setIntent(intentSite);

        //item ligação
        MenuItem itemLigar = menu.add("Ligar para bar");
        itemLigar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (ActivityCompat.checkSelfPermission(PerfilActivity.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PerfilActivity.this, new String []{Manifest.permission.CALL_PHONE}, CODIGO_LIGACAO);
                } else{
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel:" + bar.getTelefone()));
                    startActivity(intentLigar);
                }
                return false;
            }
        });

        //item sms
        MenuItem itemSMS = menu.add("Enviar SMS");
        Intent intentSMS = new Intent(Intent.ACTION_VIEW);
        intentSMS.setData(Uri.parse(("sms:" + bar.getTelefone())));
        itemSMS.setIntent(intentSMS);

        //item maps
        MenuItem itemMapa = menu.add("Visualizar no mapa");
        Intent intentMapa = new Intent(Intent.ACTION_VIEW);
        intentMapa.setData(Uri.parse("geo:0,0?z=14&q=" + bar.getEndereco()));
        itemMapa.setIntent(intentMapa);

        //item deletar
        MenuItem itemDeletar = menu.add("Deletar");
        itemDeletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                BarDAO dao = new BarDAO(PerfilActivity.this);
                dao.deleta(bar);
//                dao.close();

                Toast.makeText(PerfilActivity.this, bar.getNome() + "removido(a)", Toast.LENGTH_SHORT).show();

                carregaLista();

                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.menu_lista_alunos, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_enviar_notas:
                new EnviaDadosServidor(this).execute();
                break;

            case R.id.menu_baixar_provas:
                Intent vaiParaProvas = new Intent(this, ProvasActivity.class);
                startActivity(vaiParaProvas);
                break;

            case R.id.menu_mapa:
                Intent vaiParaMapa = new Intent(this, MapaBarActivity.class);
                startActivity(vaiParaMapa);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void carregaLista() {
        //        String[] bars = {"Daniel", "Ronaldo", "Jeferson", "Felipe"};
        BarDAO dao = new BarDAO(this);
        List<Bar> bares = dao.buscaAlunos();
//        dao.close();

        BarAdapter adapter = new BarAdapter(this, bares);

        listaAlunos.setAdapter(adapter);
    }

}
