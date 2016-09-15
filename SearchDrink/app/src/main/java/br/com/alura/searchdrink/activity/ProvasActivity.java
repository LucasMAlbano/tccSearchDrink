package br.com.alura.searchdrink.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.alura.searchdrink.fragment.DetalhesProvaFragment;
import br.com.alura.searchdrink.fragment.ListaProvasFragment;
import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.modelo.Prova;

public class ProvasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provas);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();

        tx.replace(R.id.frame_principal, new ListaProvasFragment());
        if (estaNoModoPaisagem()){
            tx.replace(R.id.frame_secundario, new DetalhesProvaFragment());
        }
        tx.commit();
    }

    private boolean estaNoModoPaisagem(){
        return getResources().getBoolean(R.bool.modoPaisagem);
    }

    public void selecionaProva(Prova prova) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        if(!estaNoModoPaisagem()){
            FragmentTransaction tx = fragmentManager.beginTransaction();

            DetalhesProvaFragment detalhesFragment = new DetalhesProvaFragment();
            Bundle paramentros = new Bundle();
            paramentros.putSerializable("prova", prova);
            detalhesFragment.setArguments(paramentros);

            tx.replace(R.id.frame_principal, detalhesFragment);
            tx.addToBackStack(null);  // adicionamos a transação na pilha

            tx.commit();

        }else{
            DetalhesProvaFragment detalhesFragment =
                    (DetalhesProvaFragment) fragmentManager.findFragmentById(R.id.frame_secundario);
            detalhesFragment.populaCamposCom(prova);
        }
    }
}
