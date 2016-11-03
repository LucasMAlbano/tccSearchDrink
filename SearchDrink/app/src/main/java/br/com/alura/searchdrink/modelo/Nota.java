package br.com.alura.searchdrink.modelo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Birbara on 05/09/2016.
 */
@IgnoreExtraProperties
public class Nota {

//    private long uId;
    private double valorNota;
    private String uId;

    public Nota(double valorNota, String uId){
        this.valorNota = valorNota;
        this.uId = uId;
    }

    public double getValorNota() {
        return valorNota;
    }

    public void setValorNota(double valorNota) {
        this.valorNota = valorNota;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> resultado = new HashMap<>();
        resultado.put("valorNota", valorNota);
        resultado.put("uId", uId);

        return resultado;
    }

    @Override
    public String toString() {
//        return getuId() + " - " + getNome();
        return String.valueOf(getValorNota());
    }
}
