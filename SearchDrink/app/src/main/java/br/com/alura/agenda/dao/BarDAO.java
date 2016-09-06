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
public class BarDAO extends SQLiteOpenHelper {


    public BarDAO(Context context) {
        super(context, "SearchDrink", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE Bares (id INTEGER PRIMARY KEY, " +
                "nome TEXT NOT NULL, " +
                "endereco TEXT, " +
                "telefone TEXT, " +
                "site TEXT, " +
                "nota REAL," +
                "caminhoFoto TEXT," +
                "email TEXT NOT NULL," +
                "senha TEXT NOT NULL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        switch(oldVersion){
            case 1:
                String sql = "ALTER TABLE Alunos ADD COLLUNM caminhoFoto TEXT;";
                db.execSQL(sql);
                break;
            case 2:
                String sqlAlteraNome = "ALTER TABLE Alunos RENAME Bares";
                String sqlColunaU = "ALTER TABLE Alunos ADD COLLUNM email TEXT NOT NULL;";
                String sqlColunaS = "ALTER TABLE Alunos ADD COLLUNM senha TEXT NOT NULL;";
                String sqlRenameBd = "RENAME DATABASE Agenda TO SearchDrink";
                db.execSQL(sqlRenameBd);
                db.execSQL(sqlAlteraNome);
                db.execSQL(sqlColunaU);
                db.execSQL(sqlColunaS);
        }

    }

    public void insere(Bar bar) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues dados = pegaDadosDoAluno(bar);

        db.insert("Bares", null, dados);
    }

    public List<Bar> buscaAlunos() {

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM Bares;", null);

        List<Bar> bares = new ArrayList<Bar>();

        while (c.moveToNext()) {
            Bar bar = new Bar();
            bar.setId(c.getLong(c.getColumnIndex("id")));
            bar.setNome(c.getString(c.getColumnIndex("nome")));
            bar.setEndereco(c.getString(c.getColumnIndex("endereco")));
            bar.setTelefone(c.getString(c.getColumnIndex("telefone")));
            bar.setSite(c.getString(c.getColumnIndex("site")));
            bar.setNota(c.getDouble(c.getColumnIndex("nota")));
            bar.setCaminhoFoto(c.getString(c.getColumnIndex("caminhoFoto")));
            bar.setEmail(c.getString(c.getColumnIndex("email")));
            bar.setSenha(c.getString(c.getColumnIndex("senha")));
            bares.add(bar);
        }
        c.close();

        return bares;
    }

    public void deleta(Bar bar) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(bar.getId())};
        db.delete("Bares", "id = ?", params);
    }

    public void altera(Bar bar) {
        SQLiteDatabase db = getWritableDatabase();
        String[] params = {String.valueOf(bar.getId())};
        db.update("Bares", pegaDadosDoAluno(bar), "id = ?", params);
    }

    public boolean ehBar(String telefone){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM BARES WHERE telefone = ?", new String[]{telefone});

        int count = cursor.getCount();

        cursor.close();

        return count > 0;
    }

    public boolean ehCadastrado(String usuario, String senha){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM BARES WHERE email = ? AND senha = ?", new String[]{usuario, senha});

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
        dados.put("email", bar.getEmail());
        dados.put("senha", bar.getSenha());
        return dados;
    }
}
