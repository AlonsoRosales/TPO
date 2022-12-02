package com.example.tpo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;


public class FragmentPedidoComidasUsuario extends Fragment {
    RecyclerView recycleview;
    SolicitudesComidaAdapter adapter;


    public FragmentPedidoComidasUsuario() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedido_comidas_usuario,container,false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("                  Historial de Pedidos");
        recycleview = (RecyclerView) view.findViewById(R.id.recyclerPedidosUsuario);
        recycleview.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<SolicitudComida> options = new FirebaseRecyclerOptions.Builder<SolicitudComida>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("pedidos"),SolicitudComida.class)
                .build();

        adapter = new SolicitudesComidaAdapter(options);
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