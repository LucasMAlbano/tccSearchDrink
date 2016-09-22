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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.alura.searchdrink.Localizador;
import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.dao.BarDAO;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.modelo.Bebida;

/**
 * Created by Birbara on 31/08/2016.
 */
public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {

//    private final DatabaseReference database;
//    private final String uId;
    private GoogleMap mapa;

//    public MapaFragment(DatabaseReference database, String uId){
//        this.database = database;
//        this.uId = uId;
//    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.mapa = googleMap;

        LatLng posicaoDaEscola = pegaCoordenadaDoEndereco("Rua Vergueiro 3185, Vila Mariana, Sao Paulo");

        if (posicaoDaEscola != null){
            centralizaEm(posicaoDaEscola);
        }

        final List<Bar> bares = new ArrayList<>();

//        Query queryRef = database.child("bares").equalTo("perfil");
//        Toast.makeText(getContext(), queryRef.getRef().toString(), Toast.LENGTH_LONG).show();
//        queryRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                System.out.println(dataSnapshot.getValue());
//
//                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//
//                String nome = String.valueOf(map.get("nome"));
//                System.out.println(nome);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


//                database.child("bares").child(uId).child("perfil").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        String s = dataSnapshot.getValue().toString();
//
//
//                        Map<String, String> mapBar = (Map) dataSnapshot.getValue();
//                        String nome = mapBar.get("nome");
//
//                        Toast.makeText(getContext(), nome, Toast.LENGTH_LONG).show();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//
//                }


//                    for(DataSnapshot snap : snapshot.getChildren()){
//
//                        String s = snap.child("nome").getValue(String.class);
//
////                        String nome = mapBar.get("nome");
//
//                        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
//                    }
//                    Toast.makeText(getContext(), k, Toast.LENGTH_LONG).show();

//                    String k = snapshot.child("perfil").toString();
//
//                    Map<String, Object> mapBar = (HashMap<String, Object>)snapshot.getValue();
//
//                    String nome = (String) mapBar.get("nome");
//                    String endereco= (String) mapBar.get("endereco");
//                    String site = (String) mapBar.get("site");
//                    String telefone = (String) mapBar.get("telefone");
//                    String email = (String) mapBar.get("email");
////                    String idBebida = dataSnapshot.getKey();
//                    Bar bar = new Bar(nome, endereco, telefone, site, email);
//
//                    Toast.makeText(getContext(), k, Toast.LENGTH_SHORT).show();
//
//                    bares.add(bar);
//            }


//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });


        //adiciona local do bar
//        BarDAO dao = new BarDAO(getContext());
//        for(Bar bar : bares){
//            LatLng coordenada = pegaCoordenadaDoEndereco(bar.getEndereco());
//            if(coordenada != null) {
//                MarkerOptions marcador = new MarkerOptions();
//                marcador.position(coordenada);
//                marcador.title(bar.getNome());
////                marcador.snippet(String.valueOf(aluno.getNota()));
//                mapa.addMarker(marcador);
//            }
//        }
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

            MarkerOptions marcador = new MarkerOptions();
            marcador.position(coordenada);
            marcador.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_localizacao));
            mapa.addMarker(marcador);
        }
    }
}
