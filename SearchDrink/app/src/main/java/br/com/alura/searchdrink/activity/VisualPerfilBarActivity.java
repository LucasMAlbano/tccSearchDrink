package br.com.alura.searchdrink.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.adapter.BebidasAdapter;
import br.com.alura.searchdrink.fragment.MapaFragment;
import br.com.alura.searchdrink.modelo.Bar;
import br.com.alura.searchdrink.modelo.Nota;
import br.com.alura.searchdrink.task.DownloadLocalizacaoBaresTask;
import br.com.alura.searchdrink.task.ImageLoadTask;

public class VisualPerfilBarActivity extends BaseActivity {

    private static final int CODIGO_LIGACAO = 123;

    private ImageView campoFoto;
    private TextView campoNome;
    private TextView campoEndereco;
    private TextView campoTipoBar;
    private Button botaoLigar;
    private Button botaoSite;
    private Button botaoMapa;
    private Button botaoDarNota;
    private ListView listaBebidas;
    private RatingBar campoNotaBar;

    private DatabaseReference dbBar;
    private DatabaseReference dbUser;

    private String uId;

    private String flagOrigem;
    private Bar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_visual_perfil_bar);


        flagOrigem = (String) getIntent().getSerializableExtra("flagOrigem");

        if (flagOrigem.equals("ListaBares")){
            bar = ListaBaresActivity.barClicado;
        }
        else {
            bar = MapaFragment.barClicado;
        }

//        barClicado = (Bar) getIntent().getSerializableExtra("barClicado");

//        bar = ListaBaresActivity.barClicado;

        uId = getUid();

        dbBar = FirebaseDatabase.getInstance().getReference().child("bares").child(bar.getuId()).child("notas");
        dbUser = FirebaseDatabase.getInstance().getReference().child("users").child(uId).child("notas");

        campoFoto = (ImageView) findViewById(R.id.visual_perfil_foto);
        campoNome = (TextView) findViewById(R.id.visual_perfil_nome);
        campoEndereco = (TextView) findViewById(R.id.visual_perfil_endereco);
        campoTipoBar = (TextView) findViewById(R.id.visual_lista_tipo_bar);

        botaoLigar = (Button) findViewById(R.id.visual_perfil_botao_ligar);
        botaoSite = (Button) findViewById(R.id.visual_perfil_botao_site);
        botaoMapa = (Button) findViewById(R.id.visual_perfil_botao_mapa);
        botaoDarNota = (Button) findViewById(R.id.visual_perfil_botao_nota);

        campoNotaBar = (RatingBar) findViewById(R.id.visual_perfil_nota);

        listaBebidas = (ListView) findViewById(R.id.visual_perfil_lista_bebidas);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Toast.makeText(this, bar.getNome(), Toast.LENGTH_SHORT).show();

        if (!MapaBarActivity.verificadorSeUsuarioEhBar)
            adicionaOuModificaNotaData();
        else{
            botaoDarNota.setText("usuários que possui bar, não pode dar nota");
            botaoDarNota.setTextColor(Color.RED);
            botaoDarNota.setTextSize(13);
            botaoDarNota.setEnabled(false);
        }


        BebidasAdapter bebidasAdapter = new BebidasAdapter(VisualPerfilBarActivity.this, bar.getBebidas());
        listaBebidas.setAdapter(bebidasAdapter);

        if (!bar.getUriFoto().equals(null) && !bar.getUriFoto().equals("null")
                && !bar.getUriFoto().equals(""))
            new ImageLoadTask(bar.getUriFoto(), campoFoto).execute();

        campoNome.setText(bar.getNome());
        campoEndereco.setText(bar.getEndereco());
        campoTipoBar.setText(bar.getTipoBar().toUpperCase());

        campoNotaBar.setRating((float) bar.getMediaNotas());
        campoNotaBar.setEnabled(false);

        botaoLigar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(VisualPerfilBarActivity.this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(VisualPerfilBarActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CODIGO_LIGACAO);
                } else {
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel:" + bar.getTelefone()));
                    startActivity(intentLigar);
                }
            }
        });

        botaoSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSite = new Intent(Intent.ACTION_VIEW);
                String site = bar.getSite();
                if (!site.startsWith("http://")) {
                    site = "http://" + site;
                }
                intentSite.setData(Uri.parse(site));
                startActivity(intentSite);
            }
        });

        botaoMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMapa = new Intent(Intent.ACTION_VIEW);
                intentMapa.setData(Uri.parse("geo:0,0?z=14&q=" + bar.getEndereco()));
                startActivity(intentMapa);
            }
        });

    }


    private void adicionaOuModificaNotaData() {

        showProgressDialog();

        final Map<String, Nota> notasUsuario = new HashMap<>();
        final Map<String, Nota> notasBar= new HashMap<>();

        final boolean[] deuNota = {false};
        final Nota[] notaDada = new Nota[1];

        //comeca varrendo as notas do usuario
        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                    String idNota = String.valueOf(map.get("uId"));
                    String valorNota = String.valueOf(map.get("valorNota"));

                    //adiciona ao mapa de notas do usuario
                    if (!valorNota.equals(null) && !valorNota.equals("null") && !valorNota.equals("")) {
                        Nota n = new Nota(Double.parseDouble(valorNota),idNota);
                        notasUsuario.put(idNota, n);
                    }
                }

                //varre as notas do barClicado
                dbBar.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Map<String, Object> map = (HashMap<String, Object>) snapshot.getValue();

                            String idNota = String.valueOf(map.get("uId"));
                            String valorNota = String.valueOf(map.get("valorNota"));

                            //adiciona ao mapa de notas do barClicado
                            if (!valorNota.equals(null) && !valorNota.equals("null") && !valorNota.equals("")) {
                                Nota n = new Nota(Double.parseDouble(valorNota),idNota);
                                notasBar.put(idNota, n);
                            }
                        }

                        //compara se o usuario ja deu nota para o barClicado
                        if (notasUsuario.size() != 0 && notasBar.size() != 0){
                            for (Map.Entry<String, Nota> r : notasUsuario.entrySet()){
                                if (notasBar.containsKey(r.getKey())){
                                    deuNota[0] = true;
                                    notaDada[0] = new Nota(r.getValue().getValorNota(), r.getValue().getuId());
                                    botaoDarNota.setText("Mudar sua nota");
                                }
                            }
                        }

                        botaoDarNota.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final Dialog dialog = new Dialog(VisualPerfilBarActivity.this);
                                dialog.setContentView(R.layout.dialog_dar_nota);
                                dialog.setTitle(botaoDarNota.getText());

                                final RatingBar dialogNota = (RatingBar) dialog.findViewById(R.id.dialog_dar_nota_nota);
                                final Button dialogSalvar = (Button) dialog.findViewById(R.id.dialog_dar_nota_botao_salvar);
                                final Button dialogCancelar = (Button) dialog.findViewById(R.id.dialog_dar_nota_botao_cancelar);

                                if (deuNota[0] == true)
                                    dialogNota.setRating((float)notaDada[0].getValorNota());


                                dialogSalvar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        //adiciona nova nota inexistente do usuario para o barClicado
                                        if (deuNota[0] == false) {
                                            String idNotaNova = dbBar.push().getKey();
                                            Nota notaNova = new Nota(dialogNota.getRating(), idNotaNova);

                                            Map<String, Object> valoresNota = notaNova.toMap();

                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put(idNotaNova, valoresNota);

                                            dbBar.updateChildren(childUpdates);
                                            dbUser.updateChildren(childUpdates);

                                            //atualiza as notas e media das notas dos estabelemcimentos gerais
                                            final int i = MapaFragment.estabelecimentos.indexOf(bar);
                                            MapaFragment.estabelecimentos.get(i).getNotas().put(idNotaNova, notaNova);
                                            double mediaNotas = 0;
                                            for (Map.Entry<String, Nota> r : MapaFragment.estabelecimentos.get(i).getNotas().entrySet()) {
                                                mediaNotas += r.getValue().getValorNota();
                                            }
                                            mediaNotas /= MapaFragment.estabelecimentos.get(i).getNotas().size();
                                            MapaFragment.estabelecimentos.get(i).setMediaNotas(mediaNotas );
                                            //atualiza as notas e medias das notas dos estabelecimentos filtrados
                                            if (ListaBaresActivity.estabalecimentosFiltrados.size() != 0){
                                                //atualiza as notas e media das notas
                                                final int j = ListaBaresActivity.estabalecimentosFiltrados.indexOf(bar);
                                                ListaBaresActivity.estabalecimentosFiltrados.get(j).getNotas().put(idNotaNova, notaNova);
                                                double mediaNotasFiltrados = 0;
                                                for (Map.Entry<String, Nota> r : ListaBaresActivity.estabalecimentosFiltrados.get(j).getNotas().entrySet()) {
                                                    mediaNotasFiltrados += r.getValue().getValorNota();
                                                }
                                                mediaNotasFiltrados /= ListaBaresActivity.estabalecimentosFiltrados.get(j).getNotas().size();
                                                ListaBaresActivity.estabalecimentosFiltrados.get(j).setMediaNotas(mediaNotasFiltrados);
                                            }

                                            campoNotaBar.setRating((float)mediaNotas);

                                            notaDada[0] = notaNova;
                                            deuNota[0] = true;
                                        }

                                        //edita a nota do usuario para o barClicado
                                        else {
                                            String idNotaModificada = notaDada[0].getuId();
                                            Nota notaModificada = new Nota(Double.valueOf(dialogNota.getRating()), idNotaModificada);

                                            Log.i("perfil", String.valueOf(notaModificada.getValorNota()));
                                            Map<String, Object> valoresNota = notaModificada.toMap();

                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put(idNotaModificada, valoresNota);

                                            dbBar.updateChildren(childUpdates);
                                            dbUser.updateChildren(childUpdates);

                                            //atualiza as notas e media das notas dos estabelemcimentos gerais
                                            final int i = MapaFragment.estabelecimentos.indexOf(bar);
                                            MapaFragment.estabelecimentos.get(i).getNotas().put(idNotaModificada, notaModificada);
                                            double mediaNotas = 0;
                                            for (Map.Entry<String, Nota> r : MapaFragment.estabelecimentos.get(i).getNotas().entrySet()) {
                                                mediaNotas += r.getValue().getValorNota();
                                            }
                                            mediaNotas /= MapaFragment.estabelecimentos.get(i).getNotas().size();
                                            MapaFragment.estabelecimentos.get(i).setMediaNotas(mediaNotas );
                                            //atualiza as notas e medias das notas dos estabelecimentos filtrados
                                            if (ListaBaresActivity.estabalecimentosFiltrados.size() != 0){
                                                //atualiza as notas e media das notas
                                                final int j = ListaBaresActivity.estabalecimentosFiltrados.indexOf(bar);
                                                ListaBaresActivity.estabalecimentosFiltrados.get(j).getNotas().put(idNotaModificada, notaModificada);
                                                double mediaNotasFiltrados = 0;
                                                for (Map.Entry<String, Nota> r : ListaBaresActivity.estabalecimentosFiltrados.get(j).getNotas().entrySet()) {
                                                    mediaNotasFiltrados += r.getValue().getValorNota();
                                                }
                                                mediaNotasFiltrados /= ListaBaresActivity.estabalecimentosFiltrados.get(j).getNotas().size();
                                                ListaBaresActivity.estabalecimentosFiltrados.get(j).setMediaNotas(mediaNotasFiltrados);
                                            }

                                            campoNotaBar.setRating((float)mediaNotas);

                                            notaDada[0] = notaModificada;
                                        }

                                        dialog.dismiss();
                                    }
                                });

                                dialogCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();
                            }
                        });

                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
