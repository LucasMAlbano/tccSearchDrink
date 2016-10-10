package br.com.alura.searchdrink.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.alura.searchdrink.Localizador;
import br.com.alura.searchdrink.fragment.MapaFragment;
import br.com.alura.searchdrink.R;

//import com.google.android.gms.maps.SupportMapFragment;


public class MapaBarActivity extends BaseActivity {

    private static final int REQUEST_PERMISSOES = 1;
    private MapaFragment mapaFragment;

    DatabaseReference database;


//    private Button botaoMenu;

    FloatingActionMenu botaoMenu;
    FloatingActionButton floatingLogin, floatingPesquisar, floatingListar;
    ImageButton centralizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mapa_bar);

        database = FirebaseDatabase.getInstance().getReference().child("bares");

//        botaoMenu = (Button) findViewById(R.id.botao_mapa);
//        botaoMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent vaiParaLogin = new Intent(MapaBarActivity.this, LoginActivity.class);
//                startActivity(vaiParaLogin);
//            }
//        });

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tx = manager.beginTransaction();

        centralizar = (ImageButton) findViewById(R.id.botao_centralizar);

        mapaFragment = new MapaFragment(/*database, */centralizar);

        tx.replace(R.id.frame_mapa, mapaFragment);
        tx.commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] permissoes = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(permissoes, REQUEST_PERMISSOES);
            }
        }


        botaoMenu = (FloatingActionMenu) findViewById(R.id.button_menu);
        floatingLogin = (FloatingActionButton) findViewById(R.id.floating_login);
        floatingPesquisar = (FloatingActionButton) findViewById(R.id.floating_pesquisar);
        floatingListar = (FloatingActionButton) findViewById(R.id.floating_listar);

        floatingLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent vaiParaLogin = new Intent(MapaBarActivity.this, LoginActivity.class);
                startActivity(vaiParaLogin);

            }
        });
        floatingPesquisar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent vaiParaPesquisa = new Intent(MapaBarActivity.this, SearchActivity.class);
                startActivity(vaiParaPesquisa);
            }
        });
        floatingListar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });


//        final View actionB = findViewById(R.id.action_b);
//
//        FloatingActionButton actionC = new FloatingActionButton(getBaseContext());
//        actionC.setTitle("Hide/Show Action above");
//        actionC.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
//            }
//        });
//
//        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
//        menuMultipleActions.addButton(actionC);
//
//        final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_a);
//        actionA.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                actionA.setTitle("Action A clicked");
//            }
//        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_PERMISSOES){

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED){

                new Localizador(this, mapaFragment);
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater menuInflater = getMenuInflater();
//
//        menuInflater.inflate(R.menu.menu_mapa, menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.menu_login:
//
//                Intent vaiParaLogin = new Intent(this, LoginActivity.class);
//                startActivity(vaiParaLogin);
//
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


}
