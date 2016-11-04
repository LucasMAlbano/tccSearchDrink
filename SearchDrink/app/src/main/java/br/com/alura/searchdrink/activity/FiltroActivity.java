package br.com.alura.searchdrink.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.adapter.BaresAdapter;
import br.com.alura.searchdrink.modelo.Bar;

/**
 * Created by danilo on 28/10/2016.
 */

public class FiltroActivity extends BaseActivity{

    private String TAG = "FiltroActivity";

    private TextView campoBebidaSelecianada;
    private Button buttonRemoverBebidaSelecionada;
    private Button buttonOk;

    private LinearLayout linearBebidas;
    private LinearLayout linearBares;

    private DatabaseReference dbFiltrosBar;
    private DatabaseReference dbFiltroBebidas;

    private List<String> listaTiposBares = new ArrayList<>();
    private HashMap<String, List<String>> mapaTiposBebidas = new HashMap<>();

    private HashMap<String, Spinner> filtroBebidas = new HashMap<>();

    private List<CheckBox> checksBares = new ArrayList<>();
    private HashMap<String, Boolean> mapaBaresChecados = new HashMap<>();
    private List<Bar> estabelecimentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_filtro);

        dbFiltrosBar = FirebaseDatabase.getInstance().getReference().child("filtros").child("tipoBar");
        dbFiltroBebidas = FirebaseDatabase.getInstance().getReference().child("filtros").child("tipoBebida");

//        estabelecimentos = (List<Bar>) getIntent().getSerializableExtra("estabelecimentos");

        campoBebidaSelecianada = (TextView)findViewById(R.id.filtro_bebidaSelecionada);

        linearBebidas = (LinearLayout) findViewById(R.id.filtro_bebidas);
        linearBares = (LinearLayout) findViewById(R.id.filtro_bares);

        carregaTipoBebidas();

        carregaTipoBares();

        buttonRemoverBebidaSelecionada = (Button) findViewById(R.id.filtro_button_removerBebidaSelecionada);
        buttonRemoverBebidaSelecionada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonRemoverBebidaSelecionada.getVisibility() == View.VISIBLE){
                    campoBebidaSelecianada.setVisibility(View.GONE);
                    campoBebidaSelecianada.setText(null);
                    buttonRemoverBebidaSelecionada.setVisibility(View.GONE);

                    for (Map.Entry<String, Spinner> p : filtroBebidas.entrySet()) {
                        p.getValue().setEnabled(true);
                        p.getValue().setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        buttonOk = (Button) findViewById(R.id.filtro_button_ok);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!campoBebidaSelecianada.getText().equals(null) &&
                        !campoBebidaSelecianada.getText().equals("null") && !campoBebidaSelecianada.getText().equals("")){
//                    Intent vaiParaListaBares = new Intent(FiltroActivity.this, ListaBaresActivity.class);
//                    vaiParaListaBares.putExtra("bebida", campoBebidaSelecianada.getText());
//                    vaiParaListaBares.putExtra("estabelecimentos", mapaBaresChecados);
//                    vaiParaListaBares.putExtra("estabelecimentos", (Serializable) estabelecimentos);
//                    startActivity(vaiParaListaBares);
                    ListaBaresActivity.bebidaSelecionada = campoBebidaSelecianada.getText().toString();
                    ListaBaresActivity.mapaBaresSelecionados = mapaBaresChecados;
                    finish();
                }
                else
                    Toast.makeText(FiltroActivity.this, "Você deve selecionar uma bebida", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void adicionaViewsBares(LinearLayout linearBares) {

        for(String tipoBar : listaTiposBares){
            CheckBox cb = new CheckBox(this);
            cb.setText(tipoBar);
            cb.setChecked(true);
            cb.setGravity(Gravity.CENTER_HORIZONTAL);
            mapaBaresChecados.put(String.valueOf(cb.getText()), cb.isChecked());
            linearBares.addView(cb);
            checksBares.add(cb);
        }

        carregaBaresChecados();
    }

    private void adicionaViewsBebidas(LinearLayout linearBebidas) {

        for(Map.Entry<String,List<String>> pair : mapaTiposBebidas.entrySet()){

            pair.getValue().add(0, pair.getKey());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 30, 0, 20);

            Spinner s = new Spinner(this);
            s.setLayoutParams(params);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, pair.getValue());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            s.setAdapter(adapter);

            linearBebidas.addView(s);
            filtroBebidas.put(pair.getKey() ,s);
        }

        carregaBebidaSelecionada();
    }

    private void carregaBaresChecados() {

        for(final CheckBox cb : checksBares){
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    mapaBaresChecados.put(String.valueOf(cb.getText()), isChecked);
                }
            });
        }
    }

    private void carregaBebidaSelecionada() {

        for(final Map.Entry<String, Spinner> pair : filtroBebidas.entrySet()){

            pair.getValue().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (!String.valueOf(pair.getValue().getSelectedItem()).equals(pair.getKey())) {
//                        Toast.makeText(FiltroActivity.this, "sim aqui", Toast.LENGTH_LONG).show();
                        campoBebidaSelecianada.setText(String.valueOf(pair.getValue().getSelectedItem()));
                        campoBebidaSelecianada.setVisibility(View.VISIBLE);
                        buttonRemoverBebidaSelecionada.setVisibility(View.VISIBLE);

                        for (Map.Entry<String, Spinner> p : filtroBebidas.entrySet()) {
                            p.getValue().setEnabled(false);
                            p.getValue().setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    private void carregaTipoBebidas() {

        showProgressDialog();

        dbFiltroBebidas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    List<String> tiposBebidas = new ArrayList<>();
                    String nomeTitulo = snapshot.getKey();

                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                        if (snapshot.getKey().equals("bebidas geladas") || snapshot.getKey().equals("bebidas quentes")) {
                            for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                                Map<String, Object> map = (HashMap<String, Object>) snapshot2.getValue();
                                String nome = String.valueOf(snapshot1.getKey() + ": " + map.get("nome"));

                                tiposBebidas.add(nome);
                            }

                        } else {
                            Map<String, Object> map = (HashMap<String, Object>) snapshot1.getValue();
                            String nome = String.valueOf(snapshot.getKey() + ": " + map.get("nome"));

                            tiposBebidas.add(nome);
                        }
                    }

                    mapaTiposBebidas.put(nomeTitulo, tiposBebidas);
                }


                adicionaViewsBebidas(linearBebidas);

                hideProgressDialog();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    private void carregaTipoBares() {

        showProgressDialog();

        dbFiltrosBar.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                    String tipo = String.valueOf(map.get("nome"));
                    listaTiposBares.add(tipo);
                }

                adicionaViewsBares(linearBares);

                hideProgressDialog();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}


