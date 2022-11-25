package com.example.tpo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
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

import java.io.ByteArrayOutputStream;
import java.sql.SQLOutput;
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


                                    String idFoto = getRandomString();

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


                                    uidUsuario = mAuth.getCurrentUser().getUid();

                                    SolicitudComida solicitudComida =
                                            new SolicitudComida(uidUsuario,keyComida,idFoto,null,"En espera",descripcion,stock,precioTotal);

                                    databaseReference.child("pedidos").push().setValue(solicitudComida);

                                    AppCompatActivity activity = (AppCompatActivity) getContext();
                                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_user,new InicioFragmentUsuario()).commit();
                                    //success message
                                    dismiss();

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

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 1){
            Bundle extras = data.getExtras();
            imgBitMap = (Bitmap) extras.get("data");

            fondoDNI.setImageBitmap(imgBitMap);
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