package br.com.alura.searchdrink.dao;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import br.com.alura.searchdrink.modelo.Bar;

/**
 * Created by Birbara on 20/07/2016.
 */
public class BarDAO {



    @NonNull
    private ContentValues pegaDadosDoAluno(Bar bar) {
        ContentValues dados = new ContentValues();
        dados.put("nome", bar.getNome());
        dados.put("endereco", bar.getEndereco());
        dados.put("telefone", bar.getTelefone());
        dados.put("site", bar.getSite());
//        dados.put("caminhoFoto", bar.getCaminhoFoto());
        dados.put("email", bar.getEmail());
        dados.put("senha", bar.getSenha());
        return dados;
    }
}
