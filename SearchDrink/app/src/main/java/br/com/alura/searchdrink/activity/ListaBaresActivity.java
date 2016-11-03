package br.com.alura.searchdrink.activity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.alura.searchdrink.Localizador;
import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.adapter.BaresAdapter;
import br.com.alura.searchdrink.fragment.MapaFragment;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.modelo.Bebida;
import br.com.alura.searchdrink.task.CalculaDistanciaTask;
import br.com.alura.searchdrink.task.TarefaDownloadLocalizacaoBares;

public class ListaBaresActivity extends BaseActivity implements CalculaDistanciaTask.Geo {

    public static String ApiKey = "AIzaSyAnE8Q44pkOA_ek3gCaS4tATj99LMOuhOM";

    private String TAG = "ListaBaresActivity";

    public static List<Bar> estabalecimentosFiltrados = new ArrayList<>();
    public static Bar bar;

    private List<Bar> estabalecimentos;

    public static String bebidaSelecionada = null;
    public static HashMap<String, Boolean> mapaBaresSelecionados = null;

    private ListView listaEstabelecimentos;
    private Button buscaLista;
    private Button buscaFiltros;
    private LinearLayout linearOpcoes;
    private LinearLayout linearFiltros;

    private SeekBar seekBarValor = null;
    private SeekBar seekBarDistancia = null;
    private TextView campoMostraSeekBarValor = null;
    private TextView campoMostraSeekBarDistancia = null;
    private Button botaoSelecionarFiltros;
    private TextView campoStatusFiltro;
    private Button botaoRemoverFiltros;

    private BaresAdapter baresAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_bares_);

        Intent veioMapaBar = getIntent();
//        estabalecimentos = (List<Bar>) veioMapaBar.getSerializableExtra("estabelecimentos");
        estabalecimentos = TarefaDownloadLocalizacaoBares.estabelecimentos;

        listaEstabelecimentos = (ListView) findViewById(R.id.lista_bares_lista);
        baresAdapter = new BaresAdapter(this, estabalecimentos);
        listaEstabelecimentos.setAdapter(baresAdapter);

        linearOpcoes = (LinearLayout) findViewById(R.id.lista_bares_opcoes);

        linearFiltros = (LinearLayout) findViewById(R.id.lista_bares_filtros);

        buscaLista = (Button) findViewById(R.id.lista_bares_botao_lista);
        buscaFiltros = (Button) findViewById(R.id.lista_bares_botao_filtros);

        buscaLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscaLista.setTextColor(Color.parseColor("#F1C332"));
                buscaFiltros.setTextColor(Color.parseColor("#c4c4c4"));
                listaEstabelecimentos.setVisibility(View.VISIBLE);
                linearFiltros.setVisibility(View.GONE);
            }
        });
        buscaFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscaFiltros.setTextColor(Color.parseColor("#F1C332"));
                buscaLista.setTextColor(Color.parseColor("#c4c4c4"));
                linearFiltros.setVisibility(View.VISIBLE);
                listaEstabelecimentos.setVisibility(View.GONE);
            }
        });

//        Toast.makeText(this, estabalecimentos.get(0).getEndereco() + " / " + estabalecimentos.get(1).getEndereco(),Toast.LENGTH_SHORT).show();
//        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + "ruadrmarianoj.m.ferraz,190,centro,Osasco,SP" + "&destinations=" + "barueri,sp" + "&mode=driving&language=fr-FR&avoid=tolls&key=" + ApiKey;
//        new CalculaDistanciaTask(this).execute(url);

        listaEstabelecimentos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) { //AdapterView<?> adapterView = tiposBebidas, View view = item, int i = posicao, long l = id
                bar = (Bar) lista.getItemAtPosition(position);

                Intent intent = new Intent(ListaBaresActivity.this, VisualPerfilBarActivity.class);
//                intent.putExtra("bar", (Serializable) bar);
                startActivity(intent);
            }
        });


        filtroDanilo();

        botaoSelecionarFiltros = (Button) findViewById(R.id.lista_bares_button_busca);
        botaoSelecionarFiltros.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent(ListaBaresActivity.this, FiltroActivity.class);
//                it.putExtra("estabelecimentos", (Serializable) estabalecimentos);
                startActivity(it);
            }
        });

        registerForContextMenu(listaEstabelecimentos);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (bebidaSelecionada != null){

            List<String> listaBaresSelecionados = new ArrayList<>();
            for (Map.Entry<String, Boolean> r : mapaBaresSelecionados.entrySet()){
                if (r.getValue().equals(true))
                    listaBaresSelecionados.add(r.getKey());
            }

            inicializaEPegaFiltros(listaBaresSelecionados);

            aplicaFiltros(listaBaresSelecionados);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        final Bar bar = (Bar) listaEstabelecimentos.getItemAtPosition(info.position);

        //item site
        MenuItem itemPerfil = menu.add("Ver perfil");
        itemPerfil.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Intent intent = new Intent(ListaBaresActivity.this, VisualPerfilBarActivity.class);
                intent.putExtra("bar", bar);
                startActivity(intent);

                return false;
            }
        });

    }

    private void inicializaEPegaFiltros(List<String> listaBaresSelecionados) {
        campoStatusFiltro = (TextView) findViewById(R.id.lista_bares_statusFiltro);
        botaoRemoverFiltros = (Button) findViewById(R.id.lista_bares_botao_removerFiltros);
        campoStatusFiltro.setVisibility(View.VISIBLE);
        botaoRemoverFiltros.setVisibility(View.VISIBLE);
        botaoSelecionarFiltros.setVisibility(View.GONE);
        linearOpcoes.setVisibility(View.VISIBLE);

        campoStatusFiltro.setText("Bebida: " + bebidaSelecionada + "\nBares: " + listaBaresSelecionados);
        botaoRemoverFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bebidaSelecionada = null;
                mapaBaresSelecionados = null;
                campoStatusFiltro.setVisibility(View.GONE);
                botaoRemoverFiltros.setVisibility(View.GONE);
                botaoSelecionarFiltros.setVisibility(View.VISIBLE);
                linearOpcoes.setVisibility(View.GONE);
            }
        });
    }

    private void aplicaFiltros(final List<String> listaBaresSelecionados) {

        Button botaoAplicarFiltros = (Button) findViewById(R.id.lista_bares_aplicarFiltros);
        botaoAplicarFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showProgressDialog();

                if(estabalecimentosFiltrados.size() != 0)
                    estabalecimentosFiltrados.removeAll(estabalecimentosFiltrados);

                for (Bar bar : estabalecimentos) {
                    if (listaBaresSelecionados.contains(bar.getTipoBar())) {

                        Log.i("entrou aqui 1", bar.getNome());

                        if (seekBarValor.getProgress() == 0 && seekBarDistancia.getProgress() != 0) {
                            calculaDistanciaDaCoordenada(Localizador.coordenada, bar);
                            Log.i("entrou aqui 2", bar.getNome() + " - v: " + String.valueOf(seekBarValor.getProgress()) + " - d: " + String.valueOf(seekBarDistancia.getProgress()));
                        }

                        else if (seekBarDistancia.getProgress() == 0 && seekBarValor.getProgress() != 0) {
                            Log.i("entrou aqui 3", bar.getNome() + " - v: " + String.valueOf(seekBarValor.getProgress()) + " - d: " + String.valueOf(seekBarDistancia.getProgress()));
                            for (Bebida bebida : bar.getBebidas()) {
                                if (bebida.getNome().equals(bebidaSelecionada) && bebida.getPreco() <= seekBarValor.getProgress()) {
                                    Log.i("entrou aqui 4", bar.getNome() + " - v: " + String.valueOf(seekBarValor.getProgress()) + " - d: " + String.valueOf(seekBarDistancia.getProgress()));
                                    estabalecimentosFiltrados.add(bar);
                                }
                            }
                        }

                        else if (seekBarValor.getProgress() == 0 && seekBarDistancia.getProgress() == 0) {
                            Log.i("entrou aqui 5", bar.getNome() + " - v: " + String.valueOf(seekBarValor.getProgress()) + " - d: " + String.valueOf(seekBarDistancia.getProgress()));
                            for (Bebida bebida : bar.getBebidas()) {
                                if (bebida.getNome().equals(bebidaSelecionada)) {
                                    Log.i("entrou aqui 5.1", bar.getNome() + " - v: " + String.valueOf(seekBarValor.getProgress()) + " - d: " + String.valueOf(seekBarDistancia.getProgress()));
                                    estabalecimentosFiltrados.add(bar);
                                }
                            }
                        }

                        else {
                            Log.i("entrou aqui 6", bar.getNome() + " - v: " + String.valueOf(seekBarValor.getProgress()) + " - d: " + String.valueOf(seekBarDistancia.getProgress()));
                            for (Bebida bebida : bar.getBebidas()) {
                                if (bebida.getNome().equals(bebidaSelecionada) && bebida.getPreco() <= seekBarValor.getProgress()) {
                                    Log.i("entrou aqui 7", bar.getNome() + " - v: " + String.valueOf(seekBarValor.getProgress()) + " - d: " + String.valueOf(seekBarDistancia.getProgress()));
                                    estabalecimentosFiltrados.add(bar);
                                    if (!estabalecimentosFiltrados.contains(bar)) {
                                        Log.i("entrou aqui 7.1", bar.getNome() + " - v: " + String.valueOf(seekBarValor.getProgress()) + " - d: " + String.valueOf(seekBarDistancia.getProgress()));
                                        calculaDistanciaDaCoordenada(Localizador.coordenada, bar);
                                    }
                                }
                            }
                        }
                    }
                }

                if (estabalecimentosFiltrados.size() != 0) {
                    Log.i("entrou aqui 8", String.valueOf(estabalecimentosFiltrados.size()));
                    listaEstabelecimentos.setAdapter(null);
                    baresAdapter.notifyDataSetChanged();

                    baresAdapter = new BaresAdapter(ListaBaresActivity.this, estabalecimentosFiltrados);
                    listaEstabelecimentos.setAdapter(baresAdapter);

                }
                else{
                    Toast.makeText(ListaBaresActivity.this, "Nenhum bar encontrado", Toast.LENGTH_LONG).show();
                }

                listaEstabelecimentos.setVisibility(View.VISIBLE);
                linearFiltros.setVisibility(View.GONE);
                buscaLista.setTextColor(Color.parseColor("#F1C332"));
                buscaFiltros.setTextColor(Color.parseColor("#c4c4c4"));

                hideProgressDialog();
            }
        });
    }

    @Override
    public void setDouble(String result, Bar bar) {
        String res[]=result.split(",");
//        Double min=Double.parseDouble(res[0])/60;
        int dist=Integer.parseInt(res[1])/1000;

        if (dist < seekBarDistancia.getProgress()){
            estabalecimentosFiltrados.add(bar);
        }
//        Toast.makeText(this, "Duration= " + (int) (min / 60) + " hr " + (int) (min % 60) + " mins", Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Distance= " + dist + " kilometers", Toast.LENGTH_LONG).show();

    }


    private void filtroDanilo() {

        seekBarValor = (SeekBar) findViewById(R.id.lista_bares_seekBar_valor);
        seekBarDistancia = (SeekBar) findViewById(R.id.lista_bares_seekBar_distancia);
        campoMostraSeekBarValor = (TextView) findViewById(R.id.lista_bares_mostraValor);
        campoMostraSeekBarDistancia = (TextView) findViewById(R.id.lista_bares_mostraDistancia);

        seekBarValor.setProgress(0);
        seekBarValor.setMax(100);
        seekBarValor.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                progress = progress / 10;
                progress = progress * 10;
                campoMostraSeekBarValor.setText("R$ " + String.valueOf(progress));
                seekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(ListaBaresActivity.this, "Valor: R$ " + seekBar.getProgress(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        seekBarDistancia.setProgress(0);
        seekBarDistancia.setMax(50);
        seekBarDistancia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 10;
                progress = progress * 10;
                campoMostraSeekBarDistancia.setText(String.valueOf(progress) + " km");
                seekBar.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(ListaBaresActivity.this, "Distância:" + seekBar.getProgress()+ " km",
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void calculaDistanciaDaCoordenada(LatLng coordenada, Bar bar) {

        if (coordenada != null ) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(coordenada.latitude, coordenada.longitude, 1);

                if (!addresses.isEmpty()) {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
//                    String country = addresses.get(0).getCountryName();
//                    String postalCode = addresses.get(0).getPostalCode();
//                    String knownName = addresses.get(0).getFeatureName();

                    String meuEndereco = address.split("-")[0] + "," +address.split("-")[1] + "," + city + "," + state;
                    meuEndereco = meuEndereco.replace(" ", "");
//                    ruadrmarianoj.m.ferraz,190,centro,Osasco,SP

                    String destino = bar.getEndereco().replace(" ", "");

                    String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + meuEndereco + "&destinations=" + destino + "&mode=driving&language=fr-FR&avoid=tolls&key=" + ApiKey;
                    new CalculaDistanciaTask(ListaBaresActivity.this, bar).execute(url);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
