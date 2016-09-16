package br.com.alura.searchdrink.modelo;

/**
 * Created by Birbara on 05/09/2016.
 */
public class Bebida {

    private long uId;
    private String nome;
    private double preco;

    private String caminhoFoto;

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

    public long getuId() {

        return uId;
    }

    public void setuId(long uId) {
        this.uId = uId;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

}
