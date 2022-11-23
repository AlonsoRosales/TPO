package com.example.tpo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import java.util.HashMap;

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
    EditText stockTxt;
    String uidEmpresario;
    TextView avisoTxt;
    private int PICK_IMAGE = 1;
    private int upload_count;
    ArrayList<Uri> ImageList = new ArrayList<>();
    private Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_comida_empresario);

        avisoTxt = findViewById(R.id.avisoText);

        stockTxt = findViewById(R.id.stock);
        stock = Integer.parseInt(stockTxt.getText().toString());

        ImageButton botonAgregarImagenes = findViewById(R.id.variasImagenes);

        //App Bar
        ImageButton imageButton = findViewById(R.id.botonatrasagregar);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AgregarComidaEmpresarioActivity.this,InicioEmpresarioActivity.class);
                startActivity(i);
            }
        });

        ImageButton botonDisminuir = findViewById(R.id.disminuirStock);
        botonDisminuir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stockTxt = findViewById(R.id.stock);
                stock = Integer.parseInt(stockTxt.getText().toString());
                stock = stock - 1;
                stockTxt.setText(String.valueOf(stock));

            }
        });

        ImageButton botonAumentar = findViewById(R.id.aumentarStock);
        botonAumentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stockTxt = findViewById(R.id.stock);
                stock = Integer.parseInt(stockTxt.getText().toString());
                stock = stock + 1;
                stockTxt.setText(String.valueOf(stock));
            }
        });

        botonAgregarImagenes.setOnClickListener(new View.OnClickListener() {
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
                        Comida comida = new Comida(nombre,precio,descripcion,stock,null,"1",null);
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

                        for(upload_count = 0;upload_count < ImageList.size(); upload_count++){
                            Uri individualImage = ImageList.get(upload_count);

                            StorageReference imageName = imageRef.child("Image"+individualImage.getLastPathSegment());

                            HashMap<String,String> valorcito = new HashMap<>();

                            imageName.putFile(individualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String url = String.valueOf(uri);
                                            valorcito.put("imagen",url);


                                            databaseReference.child("comidas").orderByChild("nombre").equalTo(String.valueOf(comida.getNombre())).addListenerForSingleValueEvent(new ValueEventListener() {
                                                String keyComida;
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    for(DataSnapshot child : snapshot.getChildren()){
                                                        keyComida = child.getKey();
                                                        break;
                                                    }
                                                    System.out.println("LA LLAVE ES : "+keyComida);
                                                    databaseReference.child("comidas").child(keyComida).child("imagenes").push().setValue(valorcito);
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    //error message
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                        }
                        Toast.makeText(AgregarComidaEmpresarioActivity.this,"Comida Agregada!",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(AgregarComidaEmpresarioActivity.this,InicioEmpresarioActivity.class);
                        startActivity(i);

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



















