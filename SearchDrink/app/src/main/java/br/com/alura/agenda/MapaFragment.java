package br.com.alura.agenda;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.modelo.Bar;

/**
 * Created by Birbara on 31/08/2016.
 */
public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap mapa;

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

        //adiciona local do aluno
        AlunoDAO dao = new AlunoDAO(getContext());
        for(Bar bar : dao.buscaAlunos()){
            LatLng coordenada = pegaCoordenadaDoEndereco(bar.getEndereco());
            if(coordenada != null) {
                MarkerOptions marcador = new MarkerOptions();
                marcador.position(coordenada);
                marcador.title(bar.getNome());
                marcador.snippet(String.valueOf(bar.getNota()));
                mapa.addMarker(marcador);
            }
        }
        dao.close();

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
        }
    }
}
