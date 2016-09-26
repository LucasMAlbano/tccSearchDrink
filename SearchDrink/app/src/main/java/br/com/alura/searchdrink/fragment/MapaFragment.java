package br.com.alura.searchdrink.fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import br.com.alura.searchdrink.modelo.Bar;

/**
 * Created by Birbara on 31/08/2016.
 */
public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    private final DatabaseReference database;
//    private final String uId;
    private GoogleMap mapa;

    public MapaFragment(DatabaseReference database/*, String uId*/){
        this.database = database;
//        this.uId = uId;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.mapa = googleMap;

//        LatLng posicaoDaEscola = pegaCoordenadaDoEndereco("Rua Vergueiro 3185, Vila Mariana, Sao Paulo");
//
//        if (posicaoDaEscola != null){
//            centralizaEm(posicaoDaEscola);
//        }

        final List<Bar> bares = new ArrayList<>();

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
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

//                    Toast.makeText(getContext(), String.valueOf(nome + email), Toast.LENGTH_LONG).show();
                }
//                Toast.makeText(getContext(), bares.get(1).getEndereco(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //adiciona local do bar
//        BarDAO dao = new BarDAO(getContext());
        for(Bar bar : bares){
            LatLng coordenada = pegaCoordenadaDoEndereco(bar.getEndereco());
            if(coordenada != null) {
//                Marker b = mapa.addMarker(new MarkerOptions()
//                        .position(coordenada)
//                        .anchor(0.5f, 0.5f)
//                        .title(bar.getEndereco())
//                        .snippet(String.valueOf(bar.getTelefone()))
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.copocheio)));

                MarkerOptions marcador = new MarkerOptions();
                marcador.position(coordenada);
                marcador.title(bar.getNome());
                marcador.snippet(String.valueOf(bar.getTelefone()));
                marcador.icon(BitmapDescriptorFactory.fromResource(R.drawable.copocheio));
                mapa.addMarker(marcador);
            }
        }
//        dao.close();

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
}
