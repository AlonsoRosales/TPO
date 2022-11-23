package com.example.tpo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InicioEmpresarioActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    /*FirebaseRecyclerOptions<Comida> options;
    RecyclerView recycleview;
    ComidasAdapter adapter;*/
    private List<Comida> comidas = new ArrayList<Comida>();
    private Context mContext;

    public InicioEmpresarioActivity(Context mContext) {
        this.mContext = mContext;
    }

    public InicioEmpresarioActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_empresario);

        String uidEmpresario = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("usuarios/"+uidEmpresario).child("tienda").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String idTienda = snapshot.getValue(String.class);
                databaseReference.child("tiendas").child(idTienda).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Tienda tienda = snapshot.getValue(Tienda.class);

                        getSupportActionBar().setTitle("Lista de Comidas - "+ tienda.getNombre());

                        databaseReference.child("comidas").orderByChild("estado").equalTo("1").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot children: snapshot.getChildren()){
                                    Comida comida1 = children.getValue(Comida.class);
                                    comidas.add(comida1);
                                }
                                ComidasAdapter comidasAdapter = new ComidasAdapter();
                                comidasAdapter.setListaComidas(comidas);
                                comidasAdapter.setContext(InicioEmpresarioActivity.this);
                                RecyclerView recyclerView = findViewById(R.id.recyclercomidas);
                                recyclerView.setAdapter(comidasAdapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(InicioEmpresarioActivity.this));

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                //error message
                            }
                        });


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //error message
                    }

                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error message
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_empresario, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.cerrarSesion_empresario:
                cerrarSesion();
                break;
            case R.id.agregar_empresario:
                agregarComidaEmpresario();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void cerrarSesion(){
        firebaseAuth.signOut();
        finish();
        Intent i = new Intent(InicioEmpresarioActivity.this,MainActivity.class);
        startActivity(i);
    }

    public void agregarComidaEmpresario(){
        Intent i = new Intent(InicioEmpresarioActivity.this,AgregarComidaEmpresarioActivity.class);
        startActivity(i);
    }

}