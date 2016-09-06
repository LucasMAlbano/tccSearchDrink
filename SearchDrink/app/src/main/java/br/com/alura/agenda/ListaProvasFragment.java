package br.com.alura.agenda;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import br.com.alura.agenda.activity.ProvasActivity;
import br.com.alura.agenda.modelo.Prova;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListaProvasFragment extends Fragment {


    public ListaProvasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista_provas, container, false);

        List<String> topicosPort = Arrays.asList("Redação", "Verbos", "Pronomes");
        Prova provaPortugues = new Prova("Português", "10/09/2016", topicosPort);

        List<String> topicosMat = Arrays.asList("Trigonometria", "Equação 3º grau");
        Prova provaMatematica = new Prova("Matemática", "12/09/2016", topicosMat);

        final List<Prova> provas = Arrays.asList(provaPortugues, provaMatematica);

        ArrayAdapter<Prova> adapter =
                new ArrayAdapter<Prova>(getContext(), android.R.layout.simple_list_item_1, provas);

        ListView lista = (ListView) view.findViewById(R.id.provas_lista);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Prova prova = (Prova) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), "Clicou na prova de " + prova, Toast.LENGTH_SHORT).show();

                ProvasActivity provasActivity = (ProvasActivity) getActivity();
                provasActivity.selecionaProva(prova);
            }
        });

        return view;
    }

}
