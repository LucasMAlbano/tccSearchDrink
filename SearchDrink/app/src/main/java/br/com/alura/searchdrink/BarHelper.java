package br.com.alura.searchdrink;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Birbara on 21/10/2016.
 */

public class BarHelper {

    public String constroiEndereco(String rua, String numero, String bairro, String cidade, String estado){
        return rua + "," + "," + numero + "," + bairro + "," + cidade + "," + estado;
    }

    public Map<String, String> devolveEndereco(String endereco) {

        Map<String, String> mapaEndereco = new HashMap<>();

        String[] enderecoCompleto = endereco.split(",");

        mapaEndereco.put("rua", enderecoCompleto[0]);
        mapaEndereco.put("numero", enderecoCompleto[1]);
        mapaEndereco.put("bairro", enderecoCompleto[2]);
        mapaEndereco.put("cidade", enderecoCompleto[3]);
        mapaEndereco.put("estado", enderecoCompleto[4]);

        return mapaEndereco;
    }
}
