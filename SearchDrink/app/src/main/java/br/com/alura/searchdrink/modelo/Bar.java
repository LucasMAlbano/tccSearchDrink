package br.com.alura.searchdrink.modelo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Birbara on 20/07/2016.
 */
public class Bar implements Serializable{

//    private long uId;
    private String nome;
    private String endereco;
    private String telefone;
    private String site;
    private String caminhoFoto;
    private String email;
    private String senha;

    private List<Bebida> bebidas;

    public Bar(String nome, String email){
        this.nome = nome;
        this.email = email;
    }


    public Bar(String nome, String endereco, String telefone, String site) {
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.site = site;
    }

    public Bar (){}


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

//    public long getuId() {
//        return uId;
//    }

//    public void setuId(long uId) {
//        this.uId = uId;
//    }

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


    public Map<String, Object> toMap(){
        HashMap<String, Object> resultado = new HashMap<>();
        resultado.put("nome", nome);
        resultado.put("endereco", endereco);
        resultado.put("telefone", telefone);
        resultado.put("site", site);

        return resultado;
    }

    @Override
    public String toString() {
//        return getuId() + " - " + getNome();
        return getNome();
    }
}
