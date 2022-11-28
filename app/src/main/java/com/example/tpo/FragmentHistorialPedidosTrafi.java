package com.example.tpo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;


public class FragmentHistorialPedidosTrafi extends Fragment {
    RecyclerView recycleview;
    PedidosAdapterTrafi adapter;


    public FragmentHistorialPedidosTrafi() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial_pedidos_trafi,container,false);

        recycleview = (RecyclerView) view.findViewById(R.id.recyclerhistorialtrafi);
        recycleview.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<SolicitudComida> options = new FirebaseRecyclerOptions.Builder<SolicitudComida>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("pedidos")
                        .orderByChild("identificador").equalTo(0),SolicitudComida.class)
                .build();

        adapter = new PedidosAdapterTrafi(options);
        recycleview.setAdapter(adapter);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}