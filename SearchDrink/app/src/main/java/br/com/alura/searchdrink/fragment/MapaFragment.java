package br.com.alura.searchdrink.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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

    private List<Bar> bares = new ArrayList<>();;

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

        TarefaDownloadLocalizacaoBares download = new TarefaDownloadLocalizacaoBares(getContext(), bares);
        Log.i("AsyncTask", "AsyncTask senado chamado Thread: " + Thread.currentThread().getName());
        download.execute(database);
        Log.i("AsyncTask", "AsyncTask senado chamado Thread: " + Thread.currentThread().getName());


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

//    private LatLng pegaCoordenadaDoEndereco(String endereco) {
//
//        try{
//            Geocoder geocoder = new Geocoder(getContext());
//            List<Address> resultados = geocoder.getFromLocationName(endereco, 1);
//
//            if(!resultados.isEmpty()){
//                LatLng posicao = new LatLng(resultados.get(0).getLatitude(),
//                        resultados.get(0).getLongitude());
//                return posicao;
//            }
//
//        }catch(IOException e){
//            e.printStackTrace();
//        }
//        return null;
//    }


//    private void pegaBares() {
//
//        Log.i("Resolvendo problema com professor:", " Inicio método pegaBares Thread: " + Thread.currentThread().getName());
//
//        bares = new ArrayList<>();
//
//
//        database.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(final DataSnapshot dataSnapshot) {
//
////                Thread t = new Thread(new Runnable() {
////                    @Override
////                    public void run() {
//
//                        Log.i("Resolvendo problema com professor:", " Inicio método onDatChange Thread: " + Thread.currentThread().getName());
////                        synchronized (this) {
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                                Log.i("Resolvendo problema com professor:", " Dentro do for do método onDataChange Thread: " + Thread.currentThread().getName());
//                                Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
//
//                                String nome = String.valueOf(map.get("nome"));
//                                String endereco = String.valueOf(map.get("endereco"));
//                                String telefone = String.valueOf(map.get("telefone"));
//                                String site = String.valueOf(map.get("site"));
//                                String email = String.valueOf(map.get("email"));
//
//                                if (endereco != null) {
//                                    Bar bar = new Bar(nome, endereco, telefone, site, email);
//
//                                    bares.add(bar);
//
//
//                                }
//                                Log.i("Resolvendo problema com professor:", nome + " " + endereco);
////                                Toast.makeText(getContext(), String.valueOf(nome + " - " + endereco), Toast.LENGTH_LONG).show();
//                            }
////                            notifyAll();
//                            Log.i("Resolvendo problema com professor:", " final método onDataChanged Thread: " + Thread.currentThread().getName());
//                        }
////                    }
////                });
//
////                t.start();
//
////                synchronized (t){
////                    Log.i("Resolvendo problema com professor:", " antes wait: " + Thread.currentThread().getName());
////                    try {
////                        t.wait();
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////                    Log.i("Resolvendo problema com professor:", " depois wait: " + Thread.currentThread().getName());
//
//                    //adiciona local do bar
////                    for(Bar bar : bares){
////                        Log.i("Resolvendo problema com professor:", " dentro for add: " + Thread.currentThread().getName());
////                        Toast.makeText(getContext(), "dentro do for", Toast.LENGTH_LONG).show();
////                        Toast.makeText(getContext(), bar.getNome() + " - " + bar.getEndereco(), Toast.LENGTH_LONG).show();
////                        LatLng coordenada = pegaCoordenadaDoEndereco(bar.getEndereco());
////                        if(coordenada != null) {
////                            MarkerOptions marcador = new MarkerOptions();
////                            marcador.position(coordenada);
////                            marcador.title(bar.getNome());
////                            marcador.snippet(String.valueOf(bar.getTelefone()));
////                            marcador.icon(BitmapDescriptorFactory.fromResource(R.drawable.copocheio));
////                            MapaFragment.mapa.addMarker(marcador);
////                        }
////                    }
////                }
//
////            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        Log.i("Resolvendo problema com professor:", " antes synchronized bares.notify Thread: " + Thread.currentThread().getName());
//
////        synchronized (bares) {
////            bares.notify();
////        }
//
//
//        Log.i("Resolvendo problema com professor:", " depois synchronized bares.notify Thread: " + Thread.currentThread().getName());
//
//    }


}
