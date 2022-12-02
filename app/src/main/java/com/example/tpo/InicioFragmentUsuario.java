package com.example.tpo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;


public class InicioFragmentUsuario extends Fragment {
    RecyclerView recycleview;
    ComidasAdapterUsuario adapter;


    public InicioFragmentUsuario() {

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
        View view = inflater.inflate(R.layout.fragment_inicio_usuario,container,false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("                           Comidas");


        recycleview = (RecyclerView) view.findViewById(R.id.recyclercomidasUsuario);
        recycleview.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<Comida> options = new FirebaseRecyclerOptions.Builder<Comida>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("comidas")
                        .orderByChild("estado").equalTo("1"),Comida.class)
                .build();

        adapter = new ComidasAdapterUsuario(options);
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