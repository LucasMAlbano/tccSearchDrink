package br.com.alura.searchdrink.modelo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Birbara on 20/07/2016.
 */
@IgnoreExtraProperties
public class User implements Serializable{

    private String uId;
    private String nome;
    private String email;
    private String uriFoto;

    public User(String uId, String nome, String email, String uriFoto){
        this.uId = uId;
        this.nome = nome;
        this.email = email;
        this.uriFoto = uriFoto;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUriFoto() {
        return uriFoto;
    }

    public void setUriFoto(String uriFoto) {
        this.uriFoto = uriFoto;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> resultado = new HashMap<>();
        resultado.put("nome", nome);
        resultado.put("email", email);
        resultado.put("uId", uId);
        resultado.put("uriFoto", uriFoto);

        return resultado;
    }

    @Override
    public String toString() {
//        return getuId() + " - " + getNome();
        return getNome();
    }
}
