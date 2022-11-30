package com.example.tpo;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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


public class FragmentActualizarComidaEmpresario extends Fragment {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    String keyComida;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference imageRef = firebaseStorage.getReference();

    String nombre;
    int stock;
    String precio;
    String descripcion;
    String idTienda;
    EditText stockTxt;
    TextView avisoTxt;

    private int PICK_IMAGE = 1;
    private int upload_count;
    ArrayList<Uri> ImageList = new ArrayList<>();
    private Uri ImageUri;

    public FragmentActualizarComidaEmpresario() {

    }

    public FragmentActualizarComidaEmpresario(String keyComida) {
        this.keyComida = keyComida;
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
        View view = inflater.inflate(R.layout.fragment_actualizar_comida_empresario,container,false);


        TextView nombreComida = view.findViewById(R.id.nombreComida2updateempresario);
        TextView precioComida = view.findViewById(R.id.precio2updateempresario);

        avisoTxt = view.findViewById(R.id.avisoText2updateempresario);

        stockTxt = view.findViewById(R.id.stock2updateempresario);


        ImageButton botonAgregarImagenes = view.findViewById(R.id.variasImagenes2updateempresario);


        botonAgregarImagenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        EditText descripcionTxt = view.findViewById(R.id.descripcion2updateempresario);


        databaseReference.child("comidas/"+keyComida).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    Comida comida = snapshot.getValue(Comida.class);
                    nombreComida.setText(comida.getNombre());
                    precioComida.setText(comida.getPrecio());
                    descripcionTxt.setText(comida.getDescripcion());


                    stockTxt.setText(String.valueOf(comida.getStock()));
                    stock = comida.getStock();

                    ImageButton botonDisminuir = view.findViewById(R.id.disminuirStock2updateempresario);
                    botonDisminuir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            stock = stock - 1;
                            stockTxt.setText(String.valueOf(stock));

                        }
                    });

                    ImageButton botonAumentar = view.findViewById(R.id.aumentarStock2updateempresario);
                    botonAumentar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            stock = stock + 1;
                            stockTxt.setText(String.valueOf(stock));
                        }
                    });


                    Button botonAgregarComida = view.findViewById(R.id.botonAgregar2updateempresario);
                    botonAgregarComida.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            boolean guardar = true;

                            try{

                                descripcion = descripcionTxt.getText().toString();
                                if (descripcion.equalsIgnoreCase("") || descripcion == null || descripcion.isEmpty()) {
                                    descripcionTxt.setError("Ingrese una descripción");
                                    guardar = false;
                                }

                                //stock = Integer.parseInt(stockTxt.getText().toString());
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
                                    nombre = comida.getNombre();
                                    precio = comida.getPrecio();
                                    idTienda = comida.getIdTienda();

                                    Comida comida = new Comida(nombre,precio,descripcion,stock,null,"1",idTienda);

                                    final int[] unavez = {1};
                                    for(upload_count = 0;upload_count < ImageList.size(); upload_count++){
                                        Uri individualImage = ImageList.get(upload_count);
                                        String randomName = randomString();
                                        StorageReference imageName = imageRef.child(randomName+individualImage.getLastPathSegment());

                                        imageName.putFile(individualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String url = String.valueOf(uri);

                                                        HashMap<String, Object> valorcito = new HashMap<>();

                                                        valorcito.put("imagen",url);

                                                        if(unavez[0] == 1){
                                                            databaseReference.child("comidas/"+keyComida).setValue(comida);
                                                            databaseReference.child("comidas").child(keyComida).child("imagenes").push().setValue(valorcito);

                                                        }else{
                                                            databaseReference.child("comidas/"+keyComida).child("imagenes").push().setValue(valorcito);
                                                        }

                                                        unavez[0]++;
                                                    }
                                                });
                                            }
                                        });

                                    }

                                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_empresario,
                                            new InicioFragmentEmpresario()).addToBackStack(null).commit();
                                    //success message

                                }else{
                                    //error message
                                    System.out.println("entro al else");
                                }

                            }catch(Exception e){
                                //error message
                                System.out.println(e);
                                System.out.println("entro al catch");
                            }

                        }
                    });

                }else{
                    //error message
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error message
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE){

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