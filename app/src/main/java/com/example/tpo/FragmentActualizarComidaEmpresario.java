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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;


public class FragmentActualizarComidaEmpresario extends Fragment {
    String uidEmpresario;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
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

        /*avisoTxt = view.findViewById(R.id.avisoTextEmpresario);

        stockTxt = view.findViewById(R.id.stockEmpresario);
        stock = Integer.parseInt(stockTxt.getText().toString());

        ImageButton botonAgregarImagenes = view.findViewById(R.id.variasImagenesEmpresario);


        ImageButton botonDisminuir = view.findViewById(R.id.disminuirStockEmpresario);
        botonDisminuir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EditText stockTxt1 = view.findViewById(R.id.stockEmpresario);
                //stock = Integer.parseInt(stockTxt1.getText().toString());
                stock = stock - 1;
                stockTxt.setText(String.valueOf(stock));

            }
        });

        ImageButton botonAumentar = view.findViewById(R.id.aumentarStockEmpresario);
        botonAumentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EditText stockTxt2 = view.findViewById(R.id.stockEmpresario);
                //stock = Integer.parseInt(stockTxt2.getText().toString());
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

        EditText nombreTxt = view.findViewById(R.id.nombreComidaEmpresario2);
        EditText precioTxt = view.findViewById(R.id.precioEmpresario);
        EditText idTiendaTxt = view.findViewById(R.id.tiendaEmpresario);
        EditText descripcionTxt = view.findViewById(R.id.descripcionEmpresario);

        Button botonAgregarComida = view.findViewById(R.id.botonAgregarEmpresario);
        botonAgregarComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean guardar = true;

                try{
                    //EditText nombreTxt = view.findViewById(R.id.nombreComidaEmpresario2);
                    System.out.println("NOMBRE: "+nombreTxt.getText().toString());

                    //EditText precioTxt = view.findViewById(R.id.precioEmpresario);
                    System.out.println("PRECIO: "+precioTxt.getText().toString());

                    //EditText idTiendaTxt = view.findViewById(R.id.tiendaEmpresario);
                    System.out.println("TIENDA: "+ idTiendaTxt.getText().toString());

                    //EditText descripcionTxt = view.findViewById(R.id.descripcionEmpresario);
                    System.out.println("DESCRIPCION: "+ descripcionTxt.getText().toString());

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

                    //stock = Integer.parseInt(stockTxt.getText().toString());
                    if(stock <= 0){
                        stockTxt.setError("Ingrese un stock positivo");
                        guardar = false;
                    }

                    try {
                        idTienda = idTiendaTxt.getText().toString();
                        int tienda = Integer.parseInt(idTienda);
                        if(tienda <= 0){
                            idTiendaTxt.setError("Ingrese un id valido");
                            guardar = false;
                        }

                    }catch (NumberFormatException e){
                        idTiendaTxt.setError("Ingrese un id valido");
                        guardar = false;
                    }

                    if(ImageList.size() == 0){
                        avisoTxt.setText("Ingrese múltiples imágenes!");
                        avisoTxt.setTextColor(Color.parseColor("#FF0000"));
                        guardar = false;
                    }


                    if(guardar){
                        Comida comida = new Comida(nombre,precio,descripcion,stock,null,"1",idTienda);
                        //uidEmpresario = firebaseAuth.getCurrentUser().getUid();

                        //databaseReference.child("comidas").push().setValue(comida);

                        String ID = randomID();
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
                                            //comida.setImagenes(valorcito);

                                            if(unavez[0] == 1){
                                                databaseReference.child("comidas").child(ID).setValue(comida);
                                                databaseReference.child("comidas").child(ID).child("imagenes").push().setValue(valorcito);

                                            }else{
                                                databaseReference.child("comidas/"+ID).child("imagenes").push().setValue(valorcito);
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
        });*/


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