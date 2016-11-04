package br.com.alura.searchdrink.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.alura.searchdrink.Localizador;
import br.com.alura.searchdrink.fragment.MapaFragment;
import br.com.alura.searchdrink.R;

//import com.google.android.gms.maps.SupportMapFragment;


public class MapaBarActivity extends BaseActivity {

    public static boolean verificadorSeUsuarioEhBar = false;

    private static final int REQUEST_PERMISSOES = 1;
    private MapaFragment mapaFragment;

    DatabaseReference database;

    FloatingActionMenu botaoMenu;
    FloatingActionButton floatingLogin, floatingFiltrar, floatingPesquisar;/*, floatingListar*/
    ImageButton centralizar;

    private String uId;
    private String uriFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mapa_bar);


        uId = getUid();

//        DatasHelper dataHelper = new DatasHelper();
//        uId = dataHelper.pegauId(getIntent());
//        uriFoto = dataHelper.pegaUrlFoto(getIntent());


        database = FirebaseDatabase.getInstance().getReference().child("bares");

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
        floatingFiltrar = (FloatingActionButton) findViewById(R.id.floating_filtrar);
        floatingPesquisar = (FloatingActionButton) findViewById(R.id.floating_pesquisar);
//        floatingListar = (FloatingActionButton) findViewById(R.id.floating_listar);

        floatingLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                boolean ehBar = verificaSeEhBar(uId);
//                Toast.makeText(MapaBarActivity.this, String.valueOf(ehBar), Toast.LENGTH_SHORT).show();
                vaiParaPerfil();
            }
        });
        floatingFiltrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent vaiParaLista = new Intent(MapaBarActivity.this, ListaBaresActivity.class);
//                vaiParaLista.putExtra("estabelecimentos", (Serializable) mapaFragment.getEstabelecimentos());
                startActivity(vaiParaLista);
            }
        });
        floatingPesquisar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        verificaSeUsuarioEhBar();
    }

    private void vaiParaPerfil() {

        if (verificadorSeUsuarioEhBar) {
            Intent vaiParaPerfilBar = new Intent(MapaBarActivity.this, PerfilBarActivity.class);
            startActivity(vaiParaPerfilBar);
        } else {
            Intent vaiParaPerfilUsuario = new Intent(MapaBarActivity.this, PerfilUsuarioActivity.class);
            startActivity(vaiParaPerfilUsuario);
        }
    }

    private void verificaSeUsuarioEhBar() {

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals(uId)) {
                        verificadorSeUsuarioEhBar = true;
                        break;
                    }
                }

                if (verificadorSeUsuarioEhBar)
                    floatingLogin.setImageResource(R.mipmap.ic_perfilbar);
                else
                    floatingLogin.setImageResource(R.mipmap.ic_perfiluser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private boolean verificaSeEhBar(String uId) {
//
//        List<Bar> estabelecimentos = null;
//        while (estabelecimentos == null) {
//            estabelecimentos = mapaFragment.getEstabelecimentos();
//        }
//
//        for(Bar barClicado : estabelecimentos){
//            if(barClicado.getuId().equals(uId))
//                return true;
//        }
//
//        return false;
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_PERMISSOES){

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED){

                new Localizador(this, mapaFragment);
            }
        }
    }
}
