package br.com.alura.searchdrink.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

import br.com.alura.searchdrink.Localizador;
import br.com.alura.searchdrink.fragment.MapaFragment;
import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.task.TarefaDownloadLocalizacaoBares;

//import com.google.android.gms.maps.SupportMapFragment;


public class MapaBarActivity extends BaseActivity {

    private static final int REQUEST_PERMISSOES = 1;
    private MapaFragment mapaFragment;

    DatabaseReference database;

//    private List<Bar> bares;

    private Button botaoMenu;

//    private String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mapa_bar);

        database = FirebaseDatabase.getInstance().getReference().child("bares");

//        if(getUid() != null){
//            uId = getUid();
//        }





        botaoMenu = (Button) findViewById(R.id.botao_mapa);
        botaoMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent vaiParaLogin = new Intent(MapaBarActivity.this, LoginActivity.class);
                startActivity(vaiParaLogin);
            }
        });

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tx = manager.beginTransaction();

        mapaFragment = new MapaFragment(/*bares,*/ database/*, uId*/);

        tx.replace(R.id.frame_mapa, mapaFragment);
        tx.commit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] permissoes = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(permissoes, REQUEST_PERMISSOES);
            }
        }


        final View actionB = findViewById(R.id.action_b);

        FloatingActionButton actionC = new FloatingActionButton(getBaseContext());
        actionC.setTitle("Hide/Show Action above");
        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });

        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        menuMultipleActions.addButton(actionC);

        final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionA.setTitle("Action A clicked");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

//        bares = pegaBares();
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


//    private List<Bar> pegaBares() {
//
//        final List<Bar> estabelecimentos = new ArrayList<>();
//
//            database.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                        Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
//
//                        String nome = String.valueOf(map.get("nome"));
//                        String endereco = String.valueOf(map.get("endereco"));
//                        String telefone = String.valueOf(map.get("telefone"));
//                        String site = String.valueOf(map.get("site"));
//                        String email = String.valueOf(map.get("email"));
//
//                        if (endereco != null) {
//                            Bar bar = new Bar(nome, endereco, telefone, site, email);
//
//                            estabelecimentos.add(bar);
//
//
//                        }
////                    Toast.makeText(getContext(), String.valueOf(nome + " - " + endereco), Toast.LENGTH_LONG).show();
//                    }
////                Toast.makeText(getContext(), bares.get(1).getEndereco(), Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//
//
////        Toast.makeText(MapaBarActivity.this, "Estabelecimento ultimo " + estabelecimentos.get(estabelecimentos.size()-1).getEndereco(), Toast.LENGTH_LONG).show();
//
//        return estabelecimentos;
//    }

//    private void pegaBares(){
//
//        TarefaDownloadLocalizacaoBares download = new TarefaDownloadLocalizacaoBares(this, database);
//        Log.i("AsyncTask", "AsyncTask senado chamado Thread: " + Thread.currentThread().getName());
//        download.execute();
//    }

}
