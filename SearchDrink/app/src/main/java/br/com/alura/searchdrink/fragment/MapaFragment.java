package br.com.alura.searchdrink.fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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

    private final DatabaseReference database;
//    private final String uId;

    public static GoogleMap mapa;

    private List<Bar> bares;

    public MapaFragment(/*List<Bar> bares, */DatabaseReference database/*, String uId*/){

//        this.bares = bares;
        this.database = database;
//        this.uId = uId;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

//        bares = new ArrayList<>();
//        pegaBares(bares);

        getMapAsync(this);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        TarefaDownloadLocalizacaoBares download = new TarefaDownloadLocalizacaoBares(getContext(), database);
//        Log.i("AsyncTask", "AsyncTask senado chamado Thread: " + Thread.currentThread().getName());
//        download.execute();
//
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.mapa = googleMap;

//        TarefaDownloadLocalizacaoBares download = new TarefaDownloadLocalizacaoBares(getContext()/*, database*/);
//        Log.i("AsyncTask", "AsyncTask senado chamado Thread: " + Thread.currentThread().getName());
//        download.execute(database);

//        LatLng posicaoDaEscola = pegaCoordenadaDoEndereco("Rua Vergueiro 3185, Vila Mariana, Sao Paulo");
//
//        if (posicaoDaEscola != null){
//            centralizaEm(posicaoDaEscola);
//        }


//        Toast.makeText(getContext(), bares.get(1).getEndereco(), Toast.LENGTH_LONG).show();

//        Toast.makeText(getContext(), "antes do for tamanho bares" + bares.size(), Toast.LENGTH_LONG).show();

        try {

            Log.i("Resolvendo problema com professor:", " antes método pegaBares() Thread: " + Thread.currentThread().getName());

            bares = new ArrayList<>();

            synchronized (bares) {
                pegaBares();
            }


            Log.i("Resolvendo problema com professor:", " depois método pegaBares() e antes synchronized bares.wait Thread: " + Thread.currentThread().getName());

//            synchronized (bares) {
//                bares.wait();
//            }

            Log.i("Resolvendo problema com professor:", " depois synchronized bares.wait Thread: " + Thread.currentThread().getName());

            for(Bar bar : bares){

                Log.i("Resolvendo problema com professor:", " dentro for Thread: " + Thread.currentThread().getName());

                Toast.makeText(getContext(), "dentro do for", Toast.LENGTH_LONG).show();
                Toast.makeText(getContext(), bar.getNome() + " - " + bar.getEndereco(), Toast.LENGTH_LONG).show();
                LatLng coordenada = pegaCoordenadaDoEndereco(bar.getEndereco());
                if(coordenada != null) {
                    MarkerOptions marcador = new MarkerOptions();
                    marcador.position(coordenada);
                    marcador.title(bar.getNome());
                    marcador.snippet(String.valueOf(bar.getTelefone()));
                    marcador.icon(BitmapDescriptorFactory.fromResource(R.drawable.copocheio));
                    MapaFragment.mapa.addMarker(marcador);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        new Localizador(getContext(), MapaFragment.this);
    }



    private LatLng pegaCoordenadaDoEndereco(String endereco) {

        try{
            Geocoder geocoder = new Geocoder(getContext());
            List<Address> resultados = geocoder.getFromLocationName(endereco, 1);

            if(!resultados.isEmpty()){
                LatLng posicao = new LatLng(resultados.get(0).getLatitude(),
                        resultados.get(0).getLongitude());
                return posicao;
            }

        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
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

    private void pegaBares() {

        Log.i("Resolvendo problema com professor:", " Inicio método pegaBares Thread: " + Thread.currentThread().getName());

//        bares = new ArrayList<>();


        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {

                        Log.i("Resolvendo problema com professor:", " Inicio método onDatChange Thread: " + Thread.currentThread().getName());

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                            Log.i("Resolvendo problema com professor:", " Dentro do for do método onDataChange Thread: " + Thread.currentThread().getName());
                            Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                            String nome = String.valueOf(map.get("nome"));
                            String endereco = String.valueOf(map.get("endereco"));
                            String telefone = String.valueOf(map.get("telefone"));
                            String site = String.valueOf(map.get("site"));
                            String email = String.valueOf(map.get("email"));

                            if (endereco != null) {
                                Bar bar = new Bar(nome, endereco, telefone, site, email);

                                bares.add(bar);


                            }
                            Toast.makeText(getContext(), String.valueOf(nome + " - " + endereco), Toast.LENGTH_LONG).show();
                        }
                        Log.i("Resolvendo problema com professor:", " final método onDataChanged Thread: " + Thread.currentThread().getName());
//                    }
//                }).start();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.i("Resolvendo problema com professor:", " antes synchronized bares.notify Thread: " + Thread.currentThread().getName());

//        synchronized (bares) {
//            bares.notify();
//        }


        Log.i("Resolvendo problema com professor:", " depois synchronized bares.notify Thread: " + Thread.currentThread().getName());

    }


}
