package br.com.alura.searchdrink.dao;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.VisibleForTesting;
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

    DatabaseReference dbFiltrosBar = FirebaseDatabase.getInstance().getReference().child("filtros").child("tipoBar");

    @VisibleForTesting
    public ProgressDialog mProgressDialog;



    public FormularioDAO(Context context, String uId){
        this.context = context;
        this.uId = uId;
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
//                Bar barClicado = new Bar(nome, endereco, telefone, site, email, tipoBar);
//
//                helper.preencheFormulario(barClicado);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Carregando...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
