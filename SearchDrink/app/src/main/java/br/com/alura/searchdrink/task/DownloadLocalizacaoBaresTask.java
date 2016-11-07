package br.com.alura.searchdrink.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
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
import br.com.alura.searchdrink.activity.ListaBaresActivity;
import br.com.alura.searchdrink.activity.VisualPerfilBarActivity;
import br.com.alura.searchdrink.fragment.MapaFragment;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.modelo.Bebida;
import br.com.alura.searchdrink.modelo.Nota;

/**
 * Created by Birbara on 30/09/2016.
 */

public class DownloadLocalizacaoBaresTask extends AsyncTask<DatabaseReference, Void, List<Bar>> {

    private final Context context;

//    public static List<Bar> estabelecimentos;

    private ProgressDialog progresso;

    public DownloadLocalizacaoBaresTask(Context context/*, List<Bar> bares*/){
//        this.estabelecimentos = bares;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        Log.i("AsyncTask", "Exibindo ProgressDialog na tela Thread: " + Thread.currentThread().getName());
        progresso = ProgressDialog.show(context, "Por favor aguarde ...",
                "Baixando Bares...");
    }

    @Override
    protected List<Bar> doInBackground(DatabaseReference... dbs) {
        Log.i("AsyncTask", "doInbackground: " + Thread.currentThread().getName());

        MapaFragment.estabelecimentos = new ArrayList<>();

        ValueEventListener postListener = pegaEstabelecimentos();
        dbs[0].addListenerForSingleValueEvent(postListener);

        synchronized (MapaFragment.estabelecimentos){
            try {
                MapaFragment.estabelecimentos.wait();
            } catch (InterruptedException e) {e.printStackTrace();}
        }

        return MapaFragment.estabelecimentos;
    }


    @Override
    protected void onPostExecute(List<Bar> bares) {

//        Toast.makeText(context, "tamanho da lista: " + bares.size(), Toast.LENGTH_LONG).show();

        if (bares.size() != 0) {
            //adiciona local do barClicado
            int i = 0;
            for (final Bar bar : bares) {
//            Toast.makeText(context, "dentro do for", Toast.LENGTH_LONG).show();
//            Toast.makeText(context, barClicado.getNome() + " - " + barClicado.getEndereco(), Toast.LENGTH_LONG).show();
                LatLng coordenada = pegaCoordenadaDoEndereco(bar.getEndereco());
                if (coordenada != null) {

                    MapaFragment.estabelecimentos.get(i).setCoordenada(coordenada);
                    i++;

                    Marker marker = MapaFragment.mapa.addMarker(new MarkerOptions()
                            .position(coordenada)
                            .title(bar.getNome())
                            .snippet(String.valueOf(bar.getTelefone()))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.copocheio_mapa)));
                    marker.setTag(bar);


//                    MarkerOptions marcador = new MarkerOptions();
//                    marcador.position(coordenada);
//                    marcador.title(bar.getNome());
//                    marcador.snippet(String.valueOf(bar.getTelefone()));
//                    marcador.icon(BitmapDescriptorFactory.fromResource(R.drawable.copocheio_mapa));
//                    MapaFragment.mapa.addMarker(marcador);

//                    MapaFragment.mapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                        @Override
//                        public boolean onMarkerClick(Marker marker) {
//                            MapaFragment.barClicado = bar;
//                            Intent intent = new Intent(context, VisualPerfilBarActivity.class);
//                            intent.putExtra("flagOrigem", "Mapa");
//                            context.startActivity(intent);
//
//                            return false;
//                        }
//                    });

                }
            }
        }

        progresso.dismiss();

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

    @NonNull
    private ValueEventListener pegaEstabelecimentos() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("AsyncTask", "doInbackground 2: " + Thread.currentThread().getName());

                synchronized (MapaFragment.estabelecimentos) {

                    //pega os bares
                    for (DataSnapshot snapshotBares : dataSnapshot.getChildren()) {
                        Log.i("AsyncTask", "doInbackground 3: " + Thread.currentThread().getName());

                        Map<String, Object> map = (HashMap<String, Object>) snapshotBares.getValue();

                        String nome = String.valueOf(map.get("nome"));
                        String endereco = String.valueOf(map.get("endereco"));
                        String telefone = String.valueOf(map.get("telefone"));
                        String site = String.valueOf(map.get("site"));
                        String email = String.valueOf(map.get("email"));
                        String uriFotoPerfil = String.valueOf(map.get("uriFoto"));
                        String tipoBar = String.valueOf(map.get("tipoBar"));

                        if (endereco != null) {
                            Bar bar = new Bar(snapshotBares.getKey(), nome, email, uriFotoPerfil, endereco, telefone, site, tipoBar);

                            // pega as bebidas
                            for (DataSnapshot snapshotBebidas : snapshotBares.child("bebidas").getChildren()){

                                Map <String, Object> mapBebidas = (HashMap<String, Object>)snapshotBebidas.getValue();
                                String nomeBebida = String.valueOf(mapBebidas.get("nome"));
                                String quantidade = String.valueOf(mapBebidas.get("quantidade"));
                                double preco = Double.parseDouble(String.valueOf(mapBebidas.get("preco")));
                                String idBebida = snapshotBares.getKey();
                                Bebida bebida = new Bebida(nomeBebida, quantidade, preco, idBebida);

                                bar.getBebidas().add(bebida);
                            }

                            //pega as notas
                            for (DataSnapshot snapshotNotas : snapshotBares.child("notas").getChildren()){

                                Map<String, Object> mapNotas = (HashMap<String, Object>) snapshotNotas.getValue();
                                String idNota = String.valueOf(mapNotas.get("uId"));
                                String valorNota = String.valueOf(mapNotas.get("valorNota"));

                                //adiciona ao mapa de notas do barClicado
                                if (!valorNota.equals(null) && !valorNota.equals("null") && !valorNota.equals("")) {
                                    Nota nota = new Nota(Double.parseDouble(valorNota),idNota);
                                    bar.getNotas().put(idNota, nota);
                                }
                            }

                            //faz a media das notas
                            double media = 0;
                            for (Map.Entry<String, Nota> r : bar.getNotas().entrySet()) {
                                media += r.getValue().getValorNota();
                            }

                            bar.setMediaNotas(media / bar.getNotas().size());



                            MapaFragment.estabelecimentos.add(bar);
                        }
                    }

                    MapaFragment.estabelecimentos.notifyAll();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("AsyncTask", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
    }

//    public List<Bar> getEstabelecimentos(){
//        return this.estabelecimentos;
//    }
}

//        dbs[0].addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Log.i("AsyncTask", "doInbackground 2: " + Thread.currentThread().getName());
//
//                    synchronized (estabelecimentos) {
//
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            Log.i("AsyncTask", "doInbackground 3: " + Thread.currentThread().getName());
//
//                            Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
//
//                            String nome = String.valueOf(map.get("nome"));
//                            String endereco = String.valueOf(map.get("endereco"));
//                            String telefone = String.valueOf(map.get("telefone"));
//                            String site = String.valueOf(map.get("site"));
//                            String email = String.valueOf(map.get("email"));
//
//                            if (endereco != null) {
//                                Bar barClicado = new Bar(nome, endereco, telefone, site, email);
//                                estabelecimentos.add(barClicado);
//                            }
//                        }
//
//                        estabelecimentos.notifyAll();
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                      Log.w("AsyncTask", "loadPost:onCancelled", databaseError.toException());
//                }
//            });