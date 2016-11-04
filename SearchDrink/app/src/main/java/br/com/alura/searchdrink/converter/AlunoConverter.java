package br.com.alura.searchdrink.converter;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.List;

import br.com.alura.searchdrink.modelo.Bar;

/**
 * Created by Birbara on 24/08/2016.
 */
public class AlunoConverter {

    public String toJson(List<Bar> bars){

        try{
            JSONStringer jsonStringer = new JSONStringer();
            jsonStringer.object().key("list").array().object().key("aluno").array();

            for(Bar bar : bars){
                jsonStringer.object()
//                        .key("id").value(barClicado.getuId())
                        .key("nome").value(bar.getNome())
                        .key("telefone").value(bar.getTelefone())
                        .key("endereco").value(bar.getEndereco())
                        .key("site").value(bar.getSite())
//                        .key("nota").value(barClicado.getNota())
                        .endObject();
            }

            return jsonStringer.endArray().endObject().endArray().endObject().toString();

        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return "";
    }
}
