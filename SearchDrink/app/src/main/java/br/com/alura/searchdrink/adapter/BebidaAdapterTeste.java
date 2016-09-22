package br.com.alura.searchdrink.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import br.com.alura.searchdrink.R;
import br.com.alura.searchdrink.modelo.Bebida;

public class BebidaAdapterTeste extends RecyclerView.Adapter<BebidaViewHolder> {

    private Context mContext;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private List<String> bebidasIds = new ArrayList<>();
    private List<Bebida> bebidas = new ArrayList<>();

    private static final String TAG = "PostDetailActivity";

    public BebidaAdapterTeste(final Context context, DatabaseReference ref) {
        mContext = context;
        mDatabaseReference = ref;

        // Create child event listener
        // [START child_event_listener_recycler]
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new bebida has been added, add it to the displayed list
                Bebida bebida = dataSnapshot.getValue(Bebida.class);

                // [START_EXCLUDE]
                // Update RecyclerView
                bebidasIds.add(dataSnapshot.getKey());
                bebidas.add(bebida);
                notifyItemInserted(bebidas.size() - 1);
                // [END_EXCLUDE]
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                Bebida newBebida = dataSnapshot.getValue(Bebida.class);
                String bebidaKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int bebidaIndex = bebidasIds.indexOf(bebidaKey);
                if (bebidaIndex > -1) {
                    // Replace with the new data
                    bebidas.set(bebidaIndex, newBebida);

                    // Update the RecyclerView
                    notifyItemChanged(bebidaIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + bebidaKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String bebidaKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int bebidaIndex = bebidasIds.indexOf(bebidaKey);
                if (bebidaIndex > -1) {
                    // Remove data from the list
                    bebidasIds.remove(bebidaIndex);
                    bebidas.remove(bebidaIndex);

                    // Update the RecyclerView
                    notifyItemRemoved(bebidaIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + bebidaKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                Bebida movedBebida = dataSnapshot.getValue(Bebida.class);
                String bebidaKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        ref.addChildEventListener(childEventListener);
        // [END child_event_listener_recycler]

        // Store reference to listener so it can be removed on app stop
        mChildEventListener = childEventListener;
    }

    @Override
    public BebidaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new BebidaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BebidaViewHolder holder, int position) {
        Bebida bebida = bebidas.get(position);
        holder.nomeView.setText(bebida.getNome());
        holder.precoVuew.setText(String.valueOf(bebida.getPreco()));
    }

    @Override
    public int getItemCount() {
        return bebidas.size();
    }

    public void cleanupListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
        }
    }

}