package br.com.alura.searchdrink.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import br.com.alura.searchdrink.Localizador;
import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.task.DownloadLocalizacaoBaresTask;

/**
 * Created by Birbara on 31/08/2016.
 */
public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    public static List<Bar> estabelecimentos;

    public static Bar barClicado;

    private DatabaseReference database;

    public static GoogleMap mapa;

    private final ImageButton centralizar;

//    private List<Bar> bares;

    private DownloadLocalizacaoBaresTask tarefaDownload;

    public MapaFragment(/*DatabaseReference database, */ImageButton centralizar){
//        this.database = database;
        this.centralizar = centralizar;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        database = FirebaseDatabase.getInstance().getReference().child("bares");

        centralizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                centralizar.startAnimation(animation);
                new Localizador(getContext(), MapaFragment.this);

            }
        });


        tarefaDownload = new DownloadLocalizacaoBaresTask(getContext()/*, bares*/);
        Log.i("AsyncTask", "AsyncTask senado chamado Thread: " + Thread.currentThread().getName());
        tarefaDownload.execute(database);
        Log.i("AsyncTask", "AsyncTask senado chamado Thread: " + Thread.currentThread().getName());

        getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();


//        bares = tarefaDownload.getEstabelecimentos();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.mapa = googleMap;


        new Localizador(getContext(), MapaFragment.this);
    }


    public void centralizaEm(LatLng coordenada){
        if(mapa != null){
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(coordenada, 17);
            mapa.moveCamera(update);

//            Marker eu = mapa.addMarker(new MarkerOptions()
//                    .position(coordenada)
//                    .anchor(0.5f, 0.5f)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.copovazio)));

            MarkerOptions marcador = new MarkerOptions();
            marcador.position(coordenada);
            marcador.icon(BitmapDescriptorFactory.fromResource(R.drawable.copovazio));
            mapa.addMarker(marcador);
        }
    }


    //    public List<Bar> getEstabelecimentos(){
//        return this.bares;
//    }
}
