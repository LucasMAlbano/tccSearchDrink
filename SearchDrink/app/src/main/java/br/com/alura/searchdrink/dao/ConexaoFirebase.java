package br.com.alura.searchdrink.dao;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Birbara on 14/09/2016.
 */
public class ConexaoFirebase {

    FirebaseDatabase db;

    public ConexaoFirebase(FirebaseDatabase db){
        this.db = db;
    }

//    public FirebaseDatabase pegaConexao(){
//
//    }
}
