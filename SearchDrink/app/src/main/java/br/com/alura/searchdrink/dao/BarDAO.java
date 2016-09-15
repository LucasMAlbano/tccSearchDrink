package br.com.alura.searchdrink.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import br.com.alura.searchdrink.activity.CadastroActivity;
import br.com.alura.searchdrink.activity.ListaAlunosActivity;
import br.com.alura.searchdrink.modelo.Bar;

/**
 * Created by Birbara on 20/07/2016.
 */
public class BarDAO {


    public BarDAO(Context context) {
        super();
    }

    public void insere(Bar bar) {

//        ContentValues dados = pegaDadosDoAluno(bar);


        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("Bar/1/");

//        db.("Bares", null, dados);
    }

    public List<Bar> buscaAlunos() {

//        SQLiteDatabase db = getReadableDatabase();

//        Cursor c = db.rawQuery("SELECT * FROM Bares;", null);
//
        List<Bar> bares = new ArrayList<Bar>();
//
//        while (c.moveToNext()) {
//            Bar bar = new Bar();
//            bar.setId(c.getLong(c.getColumnIndex("id")));
//            bar.setNome(c.getString(c.getColumnIndex("nome")));
//            bar.setEndereco(c.getString(c.getColumnIndex("endereco")));
//            bar.setTelefone(c.getString(c.getColumnIndex("telefone")));
//            bar.setSite(c.getString(c.getColumnIndex("site")));
//            bar.setCaminhoFoto(c.getString(c.getColumnIndex("caminhoFoto")));
//            bar.setEmail(c.getString(c.getColumnIndex("email")));
//            bar.setSenha(c.getString(c.getColumnIndex("senha")));
//            bares.add(bar);
//        }
//        c.close();

        return bares;
    }

    public void deleta(Bar bar) {
//        SQLiteDatabase db = getWritableDatabase();
//        String[] params = {String.valueOf(bar.getId())};
//        db.delete("Bares", "id = ?", params);
    }

    public void altera(Bar bar) {
//        SQLiteDatabase db = getWritableDatabase();
//        String[] params = {String.valueOf(bar.getId())};
//        db.update("Bares", pegaDadosDoAluno(bar), "id = ?", params);
    }

    public boolean ehBar(String telefone){
//        SQLiteDatabase db = getReadableDatabase();
//
//        Cursor cursor = db.rawQuery("SELECT * FROM BARES WHERE telefone = ?", new String[]{telefone});

//        int count = cursor.getCount();

//        cursor.close();

//        return count > 0;
        return false;
    }

    public boolean ehCadastrado(String usuario, String senha){
//        SQLiteDatabase db = getReadableDatabase();
//
//        Cursor cursor = db.rawQuery("SELECT * FROM BARES WHERE email = ? AND senha = ?", new String[]{usuario, senha});
//
//        int count = cursor.getCount();
//
//        cursor.close();
//
//        return count > 0;
        return false;
    }

    public void testeFirebase(){

        // Criando instancia para firebase
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Points to the root reference
        StorageReference storageRef = storage.getReferenceFromUrl("gs://<your-bucket-name>");

        // Points to "images"
        StorageReference imagesRef = storageRef.child("images");

        // Points to "images/space.jpg"
        // Note that you can use variables to create child values
        String fileName = "space.jpg";
        StorageReference spaceRef = imagesRef.child(fileName);

        // File path is "images/space.jpg"
        String path = spaceRef.getPath();

        // File name is "space.jpg"
        String name = spaceRef.getName();

        // Points to "images"
        imagesRef = spaceRef.getParent();
    }


    @NonNull
    private ContentValues pegaDadosDoAluno(Bar bar) {
        ContentValues dados = new ContentValues();
        dados.put("nome", bar.getNome());
        dados.put("endereco", bar.getEndereco());
        dados.put("telefone", bar.getTelefone());
        dados.put("site", bar.getSite());
        dados.put("caminhoFoto", bar.getCaminhoFoto());
        dados.put("email", bar.getEmail());
        dados.put("senha", bar.getSenha());
        return dados;
    }
}
