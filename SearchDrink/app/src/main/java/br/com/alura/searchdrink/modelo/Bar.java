package br.com.alura.searchdrink.modelo;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Birbara on 20/07/2016.
 */
@IgnoreExtraProperties
public class Bar implements Serializable{

    private String uId;

    private String nome;
    private String endereco;
    private String telefone;
    private String site;
    private Uri caminhoFoto;
    private String email;
    private String tipoBar;

    private String coordenada;

    private List<Bebida> bebidas;


    public Bar(String nome, String email){
//        this.uId = uId;
        this.nome = nome;
        this.email = email;
    }

    public Bar(String uId, String nome, String email){
        this.uId = uId;
        this.nome = nome;
        this.email = email;
    }

    public Bar(String email){
        this.email = email;
    }


    public Bar(String nome, String endereco, String telefone, String site, String email, String tipoBar) {
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.site = site;
        this.email = email;
        this.tipoBar = tipoBar;
    }

    public Bar (){}

    public Bar(String nome, String email, String endereco, String site, String telefone, Uri uriFotoPerfil) {
        this.nome = nome;
        this.email = email;
        this.endereco = endereco;
        this.site = site;
        this.telefone = telefone;
        this.caminhoFoto = uriFotoPerfil;
    }


    public String getCoordenada() {
        return coordenada;
    }

    public void setCoordenada(String coordenada) {
        this.coordenada = coordenada;
    }


    public String getTipoBar() {
        return tipoBar;
    }

    public void setTipoBar(String tipoBar) {
        this.tipoBar = tipoBar;
    }

    public List<Bebida> getBebidas() {
        return bebidas;
    }

    public void setBebidas(List<Bebida> bebidas) {
        this.bebidas = bebidas;
    }

    public Uri getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(Uri caminhoFoto) {
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


    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> resultado = new HashMap<>();
        resultado.put("nome", nome);
        resultado.put("email", email);
        resultado.put("endereco", endereco);
        resultado.put("telefone", telefone);
        resultado.put("site", site);
        resultado.put("tipoBar", tipoBar);

        return resultado;
    }

    @Override
    public String toString() {
//        return getuId() + " - " + getNome();
        return getNome();
    }
}
