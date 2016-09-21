package br.com.alura.searchdrink.modelo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Birbara on 05/09/2016.
 */
public class Bebida {

//    private long uId;
    private String nome;
    private double preco;
    private String caminhoFoto;
    private long id;
    private String idFirebase;

    public Bebida(String nome, double preco, String idFirebase){
        this.nome = nome;
        this.preco = preco;
        this.idFirebase = idFirebase;
    }

    public Bebida() {}


    public String getIdFirebase() {
        return idFirebase;
    }

    public void setIdFirebase(String idFirebase) {
        this.idFirebase = idFirebase;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

//    public long getuId() {
//
//        return uId;
//    }

//    public void setuId(long uId) {
//        this.uId = uId;
//    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> resultado = new HashMap<>();
        resultado.put("nome", nome);
        resultado.put("preco", preco);
        resultado.put("idFirebase", idFirebase);

        return resultado;
    }

    @Override
    public String toString() {
//        return getuId() + " - " + getNome();
        return getNome();
    }
}
