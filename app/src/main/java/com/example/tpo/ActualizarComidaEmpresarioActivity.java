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
import android.widget.EditText;
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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

public class ActualizarComidaEmpresarioActivity extends AppCompatActivity {
    EditText stockTxt;
    String nombre;
    int stock;
    String precio;
    String descripcion;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference imageRef = firebaseStorage.getReference();
    String uidEmpresario;
    TextView avisoTxt;
    private int PICK_IMAGE = 1;
    private int upload_count;
    ArrayList<Uri> ImageList = new ArrayList<>();
    private Uri ImageUri;
    Comida comida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_comida_empresario);

        //Aviso cantidad de imagenes
        avisoTxt = findViewById(R.id.avisoText2);

        //Recibo del intent la comida
        Intent intent = getIntent();
        comida = (Comida) intent.getSerializableExtra("comida");


        stockTxt = findViewById(R.id.stock2);
        stockTxt.setText(String.valueOf(comida.getStock()));
        stock = Integer.parseInt(String.valueOf(comida.getStock()));

        ImageButton botonDisminuir = findViewById(R.id.disminuirStock2);
        botonDisminuir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stockTxt = findViewById(R.id.stock2);
                stock = Integer.parseInt(stockTxt.getText().toString());
                stock = stock - 1;
                stockTxt.setText(String.valueOf(stock));

            }
        });

        ImageButton botonAumentar = findViewById(R.id.aumentarStock2);
        botonAumentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stockTxt = findViewById(R.id.stock2);
                stock = Integer.parseInt(stockTxt.getText().toString());
                stock = stock + 1;
                stockTxt.setText(String.valueOf(stock));
            }
        });

        TextView titulo = findViewById(R.id.tituloappbar);
        titulo.setText("Actualizar Comida");

        ImageButton botonAgregarImagenes = findViewById(R.id.variasImagenes2);
        botonAgregarImagenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        TextView nombreTxt = findViewById(R.id.nombreComida2);
        TextView precioTxt = findViewById(R.id.precio2);
        TextView descripcionTxt = findViewById(R.id.descripcion2);

        nombreTxt.setText(String.valueOf(comida.getNombre()));
        precioTxt.setText(String.valueOf(comida.getPrecio()));
        descripcionTxt.setText(String.valueOf(comida.getDescripcion()));

        //Retrocerder
        ImageButton imageButton = findViewById(R.id.botonatrasagregar);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActualizarComidaEmpresarioActivity.this,InicioEmpresarioActivity.class);
                startActivity(i);
            }
        });


        Button botonAgregar = findViewById(R.id.botonAgregar2);
        botonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean guardar = true;

                try {

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

                        Comida comidaActualizada = new Comida(nombre,precio,descripcion,stock,null,"1",comida.getIdTienda());

                        databaseReference.child("comidas").orderByChild("nombre").equalTo(String.valueOf(comida.getNombre())).addListenerForSingleValueEvent(new ValueEventListener() {
                            String keyComida;
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot child : snapshot.getChildren()){
                                    keyComida = child.getKey();
                                    break;
                                }

                                databaseReference.child("comidas").child(keyComida).setValue(comidaActualizada);

                                for(upload_count = 0;upload_count < ImageList.size(); upload_count++){
                                    Uri individualImage = ImageList.get(upload_count);
                                    String randomName = randomString();

                                    StorageReference imageName = imageRef.child(randomName+individualImage.getLastPathSegment());

                                    HashMap<String,String> valorcito = new HashMap<>();

                                    imageName.putFile(individualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    String url = String.valueOf(uri);
                                                    valorcito.put("imagen",url);

                                                    System.out.println(">>>URL : "+url);
                                                    databaseReference.child("comidas").child(keyComida).child("imagenes").push().setValue(valorcito);
                                                }
                                            });
                                        }
                                    });

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                //error message
                            }

                        });

                        Toast.makeText(ActualizarComidaEmpresarioActivity.this,"Comida Actualizada!",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ActualizarComidaEmpresarioActivity.this,InicioEmpresarioActivity.class);
                        startActivity(i);

                    }else{
                        Toast.makeText(ActualizarComidaEmpresarioActivity.this,"Campo(s) incorrecto(s)!",Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    Toast.makeText(ActualizarComidaEmpresarioActivity.this,"Campo(s) incorrecto(s)!",Toast.LENGTH_SHORT).show();
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


    public String randomString(){
        int len = 5;
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

}