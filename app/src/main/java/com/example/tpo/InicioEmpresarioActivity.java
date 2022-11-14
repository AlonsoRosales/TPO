package com.example.tpo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InicioEmpresarioActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();


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

                        //Lógica para el recycler view

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