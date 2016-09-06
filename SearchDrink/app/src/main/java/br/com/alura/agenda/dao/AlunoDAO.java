package br.com.alura.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import br.com.alura.agenda.modelo.Bar;

/**
 * Created by Birbara on 20/07/2016.
 */
public class AlunoDAO extends SQLiteOpenHelper {


    public AlunoDAO(Context context) {
        super(context, "Agenda", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE Alunos (id INTEGER PRIMARY KEY, " +
                "nome TEXT NOT NULL, " +
                "endereco TEXT, " +
                "telefone TEXT, " +
                "site TEXT, " +
                "nota REAL," +
                "caminhoFoto TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        switch(oldVersion){
            case 1:
                String sql = "ALTER TABLE Alunos ADD COLLUNM caminhoFoto TEXT;";
                db.execSQL(sql);
        }

    }

    public void insere(Bar bar) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues dados = pegaDadosDoAluno(bar);

        db.insert("Alunos", null, dados);
    }

    public List<Bar> buscaAlunos() {

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM Alunos;", null);

        List<Bar> bars = new ArrayList<Bar>();

        while (c.moveToNext()) {
            Bar bar = new Bar();
            bar.setId(c.getLong(c.getColumnIndex("id")));
            bar.setNome(c.getString(c.getColumnIndex("nome")));
            bar.setEndereco(c.getString(c.getColumnIndex("endereco")));
            bar.setTelefone(c.getString(c.getColumnIndex("telefone")));
            bar.setSite(c.getString(c.getColumnIndex("site")));
            bar.setNota(c.getDouble(c.getColumnIndex("nota")));
            bar.setCaminhoFoto(c.getString(c.getColumnIndex("caminhoFoto")));
            bars.add(bar);
        }
        c.close();

        return bars;
    }

    public void deleta(Bar bar) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(bar.getId())};
        db.delete("Alunos", "id = ?", params);
    }

    public void altera(Bar bar) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(bar.getId())};
        db.update("Alunos", pegaDadosDoAluno(bar), "id = ?", params);
    }

    public boolean ehAluno(String telefone){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM ALUNOS WHERE telefone = ?", new String[]{telefone});

        int count = cursor.getCount();

        cursor.close();

        return count > 0;
    }

    @NonNull
    private ContentValues pegaDadosDoAluno(Bar bar) {
        ContentValues dados = new ContentValues();
        dados.put("nome", bar.getNome());
        dados.put("endereco", bar.getEndereco());
        dados.put("telefone", bar.getTelefone());
        dados.put("site", bar.getSite());
        dados.put("nota", bar.getNota());
        dados.put("caminhoFoto", bar.getCaminhoFoto());
        return dados;
    }
}
