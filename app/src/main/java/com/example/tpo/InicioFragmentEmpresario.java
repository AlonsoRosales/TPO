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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class InicioFragmentEmpresario extends Fragment {
    RecyclerView recycleview;
    ComidasAdapterEmpresario adapter;
    String uidEmpresario;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    View view;

    public InicioFragmentEmpresario() {

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
        view = inflater.inflate(R.layout.fragment_inicio_empresario,container,false);

        recycleview = (RecyclerView) view.findViewById(R.id.recyclercomidasempresario);
        recycleview.setLayoutManager(new LinearLayoutManager(getContext()));

        //AHORA ES ADMIN Y YA NO EMPRESARIO
        FirebaseRecyclerOptions<Comida> options = new FirebaseRecyclerOptions.Builder<Comida>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("comidas"),Comida.class)
                .build();

        adapter = new ComidasAdapterEmpresario(options);

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