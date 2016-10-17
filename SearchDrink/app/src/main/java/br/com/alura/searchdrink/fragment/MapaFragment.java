package br.com.alura.searchdrink.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.alura.searchdrink.Localizador;
import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.activity.MapaBarActivity;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.task.TarefaDownloadLocalizacaoBares;

/**
 * Created by Birbara on 31/08/2016.
 */
public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    private DatabaseReference database;

    public static GoogleMap mapa;

    private final ImageButton centralizar;

    private List<Bar> bares;

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
                new Localizador(getContext(), MapaFragment.this);
            }
        });

        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.mapa = googleMap;

        TarefaDownloadLocalizacaoBares tarefaDownload = new TarefaDownloadLocalizacaoBares(getContext()/*, bares*/);
        Log.i("AsyncTask", "AsyncTask senado chamado Thread: " + Thread.currentThread().getName());
        tarefaDownload.execute(database);
        Log.i("AsyncTask", "AsyncTask senado chamado Thread: " + Thread.currentThread().getName());


        new Localizador(getContext(), MapaFragment.this);

        bares = tarefaDownload.getEstabelecimentos();
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

    public List<Bar> getEstabelecimentos(){
        return this.bares;
    }
}
