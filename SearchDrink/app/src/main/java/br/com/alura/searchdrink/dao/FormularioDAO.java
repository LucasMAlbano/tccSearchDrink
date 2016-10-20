package br.com.alura.searchdrink.dao;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.alura.searchdrink.FormularioHelper;
import br.com.alura.searchdrink.activity.FormularioActivity;

/**
 * Created by Birbara on 10/10/2016.
 */

public class FormularioDAO {

    private final Context context;
    private final String uId;

    private FormularioHelper helper;

    DatabaseReference dbFiltrosBar = FirebaseDatabase.getInstance().getReference().child("filtros").child("tipoBar");

    public FormularioDAO(Context context, String uId){
        this.context = context;
        this.uId = uId;
    }

    public FormularioHelper getHelper(){
        return this.helper;
    }

    public void inicializaFormularioHelper(final Context context, final BarDAO barDAO){

//        final FormularioHelper[] helper = new FormularioHelper[1];

        final List<String> tiposBares = new ArrayList<>();

        dbFiltrosBar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//                synchronized (tiposBares) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                        String tipo = String.valueOf(map.get("nome"));
                        tiposBares.add(tipo);
                    }
                    tiposBares.add(0, "selecione uma opção");

//                    tiposBares.notifyAll();

//                }
                FormularioDAO.this.helper = new FormularioHelper((FormularioActivity) context, tiposBares, uId);

                barDAO.pegaBarEPreencheFormulario(FormularioDAO.this.helper);
//                helper[0].preencheFormulario(bar);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("AsyncTask", "loadPost:onCancelled", databaseError.toException());
            }
        });


//        synchronized (tiposBares){
//            try {
//                Log.i("AsyncTask", "doInbackground 6: " + Thread.currentThread().getName());
//                tiposBares.wait();
//            } catch (InterruptedException e) {e.printStackTrace();}
//
//            Log.i("AsyncTask", "doInbackground 7: " + Thread.currentThread().getName());
//        }

//        return helper[0];
    }

//    private void preencheFormulario(final FormularioHelper helper) {
//
//        return new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Map <String, String> mapBar = (Map)dataSnapshot.getValue();
//                String nome = mapBar.get("nome");
//                String endereco = mapBar.get("endereco");
//                String telefone = mapBar.get("telefone");
//                String site = mapBar.get("site");
//                String email = mapBar.get("email");
//                String tipoBar = mapBar.get("tipoBar");
//
//
//                Bar bar = new Bar(nome, endereco, telefone, site, email, tipoBar);
//
//                helper.preencheFormulario(bar);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//    }
}
