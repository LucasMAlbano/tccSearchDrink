package br.com.alura.searchdrink.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import br.com.alura.searchdrink.Localizador;
import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.activity.VisualPerfilBarActivity;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.task.DownloadLocalizacaoBaresTask;

/**
 * Created by Birbara on 31/08/2016.
 */
public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    public static List<Bar> estabelecimentos;

    public static Bar barClicado;

    private static Marker markerLocalizacao;

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

        baixaEstabelecimentosFirebase();

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

        this.mapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Bar bar = null;
                String localizacao = null;

                try{
                    bar = (Bar) marker.getTag();
                }catch (ClassCastException e){
                    localizacao = (String) marker.getTag();
                }


                if (bar != null) {
                    barClicado = bar;

                    Intent intent = new Intent(getContext(), VisualPerfilBarActivity.class);
                    intent.putExtra("flagOrigem", "Mapa");
                    getContext().startActivity(intent);
                }

                else if (localizacao != null)
                    Toast.makeText(getContext(), "Esse é você", Toast.LENGTH_SHORT).show();

                return false;
            }
        });


        new Localizador(getContext(), MapaFragment.this);
    }


    public static void centralizaEm(LatLng coordenada, String origem){
        if(mapa != null){
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(coordenada, 17);
            mapa.moveCamera(update);

//            Marker eu = mapa.addMarker(new MarkerOptions()
//                    .position(coordenada)
//                    .anchor(0.5f, 0.5f)
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.copovazio)));

            if (!origem.equals("busca")) {

                if (markerLocalizacao == null) {
                    markerLocalizacao = mapa.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_copovazio))
                            .position(coordenada));
                    markerLocalizacao.setTag(origem);
                }

                markerLocalizacao.setPosition(coordenada);

            }
        }
    }


    public void baixaEstabelecimentosFirebase() {

        tarefaDownload = new DownloadLocalizacaoBaresTask(getContext()/*, bares*/);
        Log.i("AsyncTask", "AsyncTask senado chamado Thread: " + Thread.currentThread().getName());
        tarefaDownload.execute(database);
        Log.i("AsyncTask", "AsyncTask senado chamado Thread: " + Thread.currentThread().getName());
    }



    //    public List<Bar> getEstabelecimentos(){
//        return this.bares;
//    }
}
