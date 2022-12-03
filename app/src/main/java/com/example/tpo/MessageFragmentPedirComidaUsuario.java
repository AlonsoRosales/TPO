package com.example.tpo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Random;


public class MessageFragmentPedirComidaUsuario extends DialogFragment {
    private String keyComida;
    private int stock;
    private String descripcion;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference imageRef = firebaseStorage.getReference();
    ImageView fondoDNI;
    Bitmap imgBitMap;
    String uidUsuario;
    FirebaseAuth mAuth;
    TextView descripcionTXT;
    TextView stockTxt;
    Double precioTotal;
    String idFoto;
    String coordenadas;
    private FusedLocationProviderClient client;


    public MessageFragmentPedirComidaUsuario(String keyComida) {
        this.keyComida = keyComida;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().getWindow().setLayout(1050, 1490);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_message_pedir_comida_usuario, null);

        descripcionTXT = view.findViewById(R.id.descripcionComidaUsuario);
        stockTxt = view.findViewById(R.id.stockTituloPedirComida);
        stock = Integer.parseInt(stockTxt.getText().toString());

        fondoDNI = view.findViewById(R.id.imagenUbicacionUsuario);

        ImageButton botonCamara = view.findViewById(R.id.botonCamara);
        botonCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1);
            }
        });


        databaseReference.child("comidas").child(keyComida).addListenerForSingleValueEvent(new ValueEventListener() {
            Comida comida;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() != null){
                    comida = snapshot.getValue(Comida.class);
                    TextView stockTitulo = view.findViewById(R.id.stockValorComidaUsuario);
                    stockTitulo.setText(String.valueOf(comida.getStock()));
                }else{
                    //error message
                    dismiss();

                }


                TextView precio = view.findViewById(R.id.stockValorComidaUsuario);
                precio.setText(String.valueOf(comida.getPrecio()));


                ImageButton botonDisminuir = view.findViewById(R.id.disminuirStockPediComidaUsuario);
                botonDisminuir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stock = Integer.parseInt(stockTxt.getText().toString());
                        stock = stock - 1;
                        stockTxt.setText(String.valueOf(stock));


                        String precioMenos = comida.getPrecio();
                        String precioFormalMenos = "";
                        for(int k=2;k<precioMenos.length();k++){
                            precioFormalMenos = precioFormalMenos + precioMenos.charAt(k);
                        }

                        Double precioTotalMenos = Double.parseDouble(precioFormalMenos)*stock;
                        precio.setText("S/"+String.valueOf(precioTotalMenos));

                    }
                });


                ImageButton botonAumentar = view.findViewById(R.id.aumentarStockPediComidaUsuario);
                botonAumentar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stock = Integer.parseInt(stockTxt.getText().toString());
                        stock = stock + 1;
                        stockTxt.setText(String.valueOf(stock));

                        String precioMas = comida.getPrecio();
                        String precioFormalMas = "";
                        for(int k=2;k<precioMas.length();k++){
                            precioFormalMas = precioFormalMas + precioMas.charAt(k);
                        }

                        Double precioTotalMas = Double.parseDouble(precioFormalMas)*stock;
                        precio.setText("S/"+String.valueOf(precioTotalMas));


                    }
                });

                Button botonRealizarPedido = view.findViewById(R.id.botonRealizarPedido);
                botonRealizarPedido.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(comida.getEstado().equals("0") || comida.getStock() == 0){
                            //error message -> no esta disponible
                            dismiss();

                        }else{
                            boolean guardar = true;

                            try {
                                descripcion = descripcionTXT.getText().toString();
                                if(descripcion.equalsIgnoreCase("") || descripcion == null || descripcion.isEmpty()){
                                    descripcionTXT.setError("Ingrese una descripcion");
                                    guardar = false;
                                }

                                if((stock <= 0) || (stock > comida.getStock())){
                                    stockTxt.setError("Ingrese una cantidad positiva");
                                    guardar = false;
                                }

                                if(imgBitMap.equals("") || imgBitMap == null){
                                    guardar = false;
                                }

                                if(guardar){
                                    int stockRestante = comida.getStock() - stock;

                                    String precioStr = comida.getPrecio();
                                    String precioFormal = "";
                                    for(int k=2;k<precioStr.length();k++){
                                        precioFormal = precioFormal + precioStr.charAt(k);
                                        System.out.println(precioFormal);
                                    }

                                    precioTotal = Double.parseDouble(precioFormal)*stock;

                                    if(stockRestante > 0){
                                        databaseReference.child("comidas/"+keyComida).child("stock").setValue(stockRestante);
                                    }else{
                                        databaseReference.child("comidas/"+keyComida).child("stock").setValue(0);
                                        databaseReference.child("comidas/"+keyComida).child("estado").setValue("0");
                                    }


                                    idFoto = getRandomString();

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    imgBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] data = baos.toByteArray();


                                    UploadTask uploadTask = imageRef.child(idFoto+".jpg").putBytes(data);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            //error message
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            //success message
                                        }
                                    });


                                    //Sacamos las coordenadas
                                    client = LocationServices.getFusedLocationProviderClient(getActivity());
                                    if(ContextCompat.checkSelfPermission(getActivity()
                                            ,Manifest.permission.ACCESS_FINE_LOCATION)
                                            == PackageManager.PERMISSION_GRANTED &&
                                            ContextCompat.checkSelfPermission(getActivity()
                                                    ,Manifest.permission.ACCESS_COARSE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED){

                                        getCurrentLocation();

                                    }else{
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                                        ,Manifest.permission.ACCESS_COARSE_LOCATION},100);

                                    }


                                    /*uidUsuario = mAuth.getCurrentUser().getUid();

                                    SolicitudComida solicitudComida =
                                            new SolicitudComida(uidUsuario,keyComida,idFoto,null,"En espera",descripcion,stock,precioTotal,1);

                                    databaseReference.child("pedidos").push().setValue(solicitudComida);

                                    AppCompatActivity activity = (AppCompatActivity) getContext();
                                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_user,new InicioFragmentUsuario()).commit();
                                    //success message
                                    dismiss();*/

                                }else{
                                    //message error -> campos incorrectos
                                    System.out.println("ERROR AL GUARDAR");
                                    dismiss();

                                }

                            }catch (Exception e){
                                //error message
                                System.out.println("ENTRU AL CATCH");
                                System.out.println(e);
                                dismiss();
                            }
                        }

                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error message
                dismiss();
            }
        });


        builder.setView(view);

        return builder.create();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        if(requestCode == 100 && (grantResults.length > 0) &&
                (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){

            System.out.println("ENTRO AL OVERRIDE ON REQUEST");
            getCurrentLocation();

        }else{
            System.out.println("ERROR MESSAGE");
            //denied permissions - message error
        }

    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation(){
        LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                String latitud;
                String longitud;

                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();

                    if(location != null){
                        latitud = String.valueOf(location.getLatitude());
                        longitud = String.valueOf(location.getLongitude());

                    }else{
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                latitud = String.valueOf(location1.getLatitude());
                                longitud = String.valueOf(location1.getLongitude());

                            }
                        };

                        client.requestLocationUpdates(locationRequest
                                ,locationCallback, Looper.myLooper());
                    }

                    System.out.println("LATITUD: "+latitud);
                    System.out.println("LONGITUD: "+longitud);

                    coordenadas = latitud + "/" + longitud;

                    //codigo
                    uidUsuario = mAuth.getCurrentUser().getUid();

                    SolicitudComida solicitudComida =
                            new SolicitudComida(uidUsuario,keyComida,idFoto,coordenadas,"En espera",descripcion,stock,precioTotal,1);

                    databaseReference.child("pedidos").push().setValue(solicitudComida);

                    AppCompatActivity activity = (AppCompatActivity) getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_user,new InicioFragmentUsuario()).commit();

                    //success message
                    dismiss();

                }
            });

        }else{

            //error message - activar permisos
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 1){
            Bundle extras = data.getExtras();
            imgBitMap = (Bitmap) extras.get("data");

            fondoDNI.setImageBitmap(imgBitMap);

            String imgName = System.currentTimeMillis() + ".jpg";
            Uri imageCollection = null;
            ContentResolver resolver = getActivity().getContentResolver();

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            }else{
                imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,imgName);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
            Uri imageUri = resolver.insert(imageCollection,contentValues);
            try {
                OutputStream outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                imgBitMap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                Objects.requireNonNull(outputStream);


            }catch (Exception e){
                System.out.println("ERROR");
            }


        }
    }

    public String getRandomString() {
        String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";
        int sizeOfRandomString = 15;
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public void disminuir(View view){
        EditText stockTxt = view.findViewById(R.id.stockTituloPedirComida);
        stock = Integer.parseInt(stockTxt.getText().toString());
        stock = stock - 1;
        stockTxt.setText(String.valueOf(stock));
        System.out.println("NUEVO STOCK: "+stock);
    }

}