package com.example.tpo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class AgregarComidaEmpresarioActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference imageRef = firebaseStorage.getReference();
    String nombre;
    int stock;
    String precio;
    String descripcion;
    TextView stockTxt = findViewById(R.id.stock);
    String uidEmpresario;
    TextView avisoTxt = findViewById(R.id.avisoText);
    private int PICK_IMAGE = 1;
    private int upload_count;
    ArrayList<Uri> ImageList = new ArrayList<>();
    private Uri ImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_comida_empresario);

        stock = Integer.parseInt(stockTxt.getText().toString());

        ImageButton botonAgregarImagenes = findViewById(R.id.variasImagenes);


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

        botonDisminuir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                startActivityForResult(intent,PICK_IMAGE);
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

                    if(ImageList.size() == 0){
                        avisoTxt.setText("Ingrese múltiples imágenes!");
                        avisoTxt.setTextColor(Color.parseColor("#FF0000"));
                        guardar = false;
                    }


                    if(guardar){

                        Comida comida = new Comida(nombre,precio,descripcion,stock);
                        uidEmpresario = firebaseAuth.getCurrentUser().getUid();

                        databaseReference.child("usuarios/"+uidEmpresario).child("tienda").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String idTienda = snapshot.getValue(String.class);
                                comida.setIdTienda(idTienda);

                                databaseReference.child("comidas").push().setValue(comida);

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                //error message
                            }
                        });


                        databaseReference.child("comidas").orderByChild("nombre").equalTo(comida.getNombre()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String keyComida = String.valueOf(snapshot.getKey());

                                for(upload_count = 0;upload_count < ImageList.size(); upload_count++){
                                    Uri individualImage = ImageList.get(upload_count);
                                    StorageReference imageName = imageRef.child("Image"+individualImage.getLastPathSegment());

                                    imageRef.putFile(individualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String url = String.valueOf(uri);
                                                    databaseReference.child("comidas/"+keyComida).child("imagenes").push().setValue(url);

                                                }
                                            });
                                        }
                                    });

                                }

                                Toast.makeText(AgregarComidaEmpresarioActivity.this,"Comida Agregada!",Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                //error message
                            }
                        });


                    }else{
                        Toast.makeText(AgregarComidaEmpresarioActivity.this,"Campo(s) incorrecto(s)!",Toast.LENGTH_SHORT).show();
                    }

                }catch(Exception e){
                    Toast.makeText(AgregarComidaEmpresarioActivity.this,"Campo(s) incorrecto(s)!",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE){
            if (resultCode == RESULT_OK){

                if(data.getClipData() != null){

                    int countClipData = data.getClipData().getItemCount();

                    int currentImageSelect = 0;

                    while(currentImageSelect < countClipData){

                        ImageUri = data.getClipData().getItemAt(currentImageSelect).getUri();
                        ImageList.add(ImageUri);

                        currentImageSelect++;

                    }

                    avisoTxt.setText("Has seleccionado "+ImageList.size() + " fotos.");
                    avisoTxt.setTextColor(Color.BLACK);

                }else{
                    avisoTxt.setText("Ingrese múltiples imágenes!");
                    avisoTxt.setTextColor(Color.parseColor("#FF0000"));
                }

            }
        }

    }
}



















