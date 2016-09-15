package br.com.alura.searchdrink.modelo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Birbara on 20/07/2016.
 */
public class Bar implements Serializable{

    private long id;
    private String nome;
    private String endereco;
    private String telefone;
    private String site;
    private String caminhoFoto;
    private String email;
    private String senha;

    private List<Bebida> bebidas;

//    public Bar(String nome, String endereco, String telefone, String site, double nota) {
//        this.nome = nome;
//        this.endereco = endereco;
//        this.telefone = telefone;
//        this.site = site;
//        this.nota = nota;
//    }

//    public Bar (){}


    public List<Bebida> getBebidas() {
        return bebidas;
    }

    public void setBebidas(List<Bebida> bebidas) {
        this.bebidas = bebidas;
    }

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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }


    @Override
    public String toString() {
        return getId() + " - " + getNome();
    }
}
