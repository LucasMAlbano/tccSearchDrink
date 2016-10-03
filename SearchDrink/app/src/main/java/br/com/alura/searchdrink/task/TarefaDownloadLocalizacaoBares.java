package br.com.alura.searchdrink.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.fragment.MapaFragment;
import br.com.alura.searchdrink.modelo.Bar;

/**
 * Created by Birbara on 30/09/2016.
 */

public class TarefaDownloadLocalizacaoBares extends AsyncTask<DatabaseReference, Void, List<Bar>> {

    private final Context context;

    private final List<Bar> estabelecimentos;

    private ProgressDialog progresso;

    public TarefaDownloadLocalizacaoBares(Context context, List<Bar> bares){
        this.estabelecimentos = bares;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        Log.i("AsyncTask", "Exibindo ProgressDialog na tela Thread: " + Thread.currentThread().getName());
        progresso = ProgressDialog.show(context, "Por favor Aguarde ...",
                "Baixando Bares...");
    }

    @Override
    protected List<Bar> doInBackground(DatabaseReference... dbs) {
        Log.i("AsyncTask", "Exibindo ProgressDialog na tela Thread 1: " + Thread.currentThread().getName());

            dbs[0].addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("AsyncTask", "Exibindo ProgressDialog na tela Thread 2: " + Thread.currentThread().getName());

                    synchronized (estabelecimentos) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.i("AsyncTask", "Exibindo ProgressDialog na tela Thread 3: " + Thread.currentThread().getName());

                            Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                            String nome = String.valueOf(map.get("nome"));
                            String endereco = String.valueOf(map.get("endereco"));
                            String telefone = String.valueOf(map.get("telefone"));
                            String site = String.valueOf(map.get("site"));
                            String email = String.valueOf(map.get("email"));

                            if (endereco != null) {
                                Bar bar = new Bar(nome, endereco, telefone, site, email);
                                estabelecimentos.add(bar);
                            }
                        }

                    estabelecimentos.notifyAll();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        synchronized (estabelecimentos){
            try {
                estabelecimentos.wait();
            } catch (InterruptedException e) {e.printStackTrace();}
        }

        return estabelecimentos;
    }

    @Override
    protected void onPostExecute(List<Bar> bares) {

        progresso.dismiss();

        Toast.makeText(context, "tamanho da lista: " + bares.size(), Toast.LENGTH_LONG).show();

        //adiciona local do bar
        for(Bar bar : bares){
//            Toast.makeText(context, "dentro do for", Toast.LENGTH_LONG).show();
//            Toast.makeText(context, bar.getNome() + " - " + bar.getEndereco(), Toast.LENGTH_LONG).show();
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

    }

    private LatLng pegaCoordenadaDoEndereco(String endereco) {

        try{
            Geocoder geocoder = new Geocoder(context);
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
}
