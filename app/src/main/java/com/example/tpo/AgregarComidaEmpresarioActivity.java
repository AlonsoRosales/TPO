package com.example.tpo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AgregarComidaEmpresarioActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    String nombre;
    int stock;
    String precio;
    String descripcion;
    TextView stockTxt = findViewById(R.id.stock);
    String uidEmpresario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_comida_empresario);

        uidEmpresario = firebaseAuth.getCurrentUser().getUid();

        databaseReference.child("usuarios/"+uidEmpresario).child("tienda").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String idTienda = snapshot.getValue(String.class);
                databaseReference.child("tiendas").child(idTienda).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Tienda tienda = snapshot.getValue(Tienda.class);
                        getSupportActionBar().setTitle("Lista de Comidas - "+ tienda.getNombre());

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

        stock = Integer.parseInt(stockTxt.getText().toString());


        ImageButton botonDisminuir = findViewById(R.id.disminuirStock);
        botonDisminuir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stock = stock - 1;
                stockTxt.setText(stock);

            }
        });

        ImageButton botonAumentar = findViewById(R.id.aumentarStock);
        botonAumentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stock = stock + 1;
                stockTxt.setText(stock);
            }
        });


        Button botonAgregar = findViewById(R.id.botonAgregar);
        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean guardar = true;

                try{
                    TextView nombreTxt = findViewById(R.id.nombreComida);
                    TextView precioTxt = findViewById(R.id.precio);
                    TextView descripcionTxt = findViewById(R.id.descripcion);

                    nombre = nombreTxt.getText().toString();
                    if (nombre.equalsIgnoreCase("") || nombre == null || nombre.isEmpty()) {
                        nombreTxt.setError("Ingrese un nombre");
                        guardar = false;
                    }

                    precio = precioTxt.getText().toString();
                    if (precio.equalsIgnoreCase("") || precio == null || precio.isEmpty()) {
                        precioTxt.setError("Ingrese un precio");
                        guardar = false;
                    }

                    descripcion = descripcionTxt.getText().toString();
                    if (descripcion.equalsIgnoreCase("") || descripcion == null || descripcion.isEmpty()) {
                        descripcionTxt.setError("Ingrese una descripción");
                        guardar = false;
                    }

                    stock = Integer.parseInt(stockTxt.getText().toString());
                    if(stock <= 0){
                        stockTxt.setError("Ingrese un stock positivo");
                        guardar = false;
                    }


                    //Falta las fotos



                }catch(Exception e){
                    Toast.makeText(AgregarComidaEmpresarioActivity.this,"Campo(s) incorrecto(s)!",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }



}