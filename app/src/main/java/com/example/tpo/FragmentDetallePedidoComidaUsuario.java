package com.example.tpo;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Set;


public class FragmentDetallePedidoComidaUsuario extends Fragment {
    private String keyPedido;
    private String keyComida;
    private Comida comida;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference imageRef = firebaseStorage.getReference();
    ArrayList<String> urls = new ArrayList<>();
    ArrayList<SlideModel> imageList = new ArrayList<>();
    SolicitudComida solicitudComida;
    FirebaseAuth mAuth;
    String uidUsuario;

    public FragmentDetallePedidoComidaUsuario(String keyPedido){
        this.keyPedido = keyPedido;
    }


    public FragmentDetallePedidoComidaUsuario(){
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detalle_comida_usuario, container, false);

        TextView nombrePedidoTXT = view.findViewById(R.id.nombrePedidoComidaDetalle);
        TextView estadoPedidoTXT = view.findViewById(R.id.estadoPedidoComidaDetalle);
        TextView precioPedidoTXT = view.findViewById(R.id.precioTotalComidaDetalle);
        TextView cantidadPedidoTXT = view.findViewById(R.id.cantidadPedidoComidaDetalle);
        TextView nombreTiendaPedidoTXT = view.findViewById(R.id.tiendaPedidoComidaDetalle);
        TextView descripcionPedidoTXT = view.findViewById(R.id.descripcionPedidoComidaDetalle);
        ImageSlider sliderImagenes = view.findViewById(R.id.sliderPedidoComidaDetalle);
        ImageView imagenUbicacion = view.findViewById(R.id.imagenUbicacionPedidoDetalleUsuario);

        databaseReference.child("pedidos/"+keyPedido).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                solicitudComida = snapshot.getValue(SolicitudComida.class);
                keyComida = solicitudComida.getIdComida();

                databaseReference.child("comidas/"+keyComida).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue() != null){
                            comida = snapshot.getValue(Comida.class);

                            nombrePedidoTXT.setText(String.valueOf(comida.getNombre()));
                            precioPedidoTXT.setText(String.valueOf(solicitudComida.getPrecioTotal()));
                            cantidadPedidoTXT.setText(String.valueOf(solicitudComida.getCantidad()));
                            nombreTiendaPedidoTXT.setText("Tienda "+String.valueOf(comida.getIdTienda()));
                            descripcionPedidoTXT.setText(String.valueOf(solicitudComida.getDescripcion()));

                            switch (solicitudComida.getEstado()){
                                case "En espera":
                                    estadoPedidoTXT.setTextColor(Color.parseColor("#A9A319"));
                                    break;
                                case "En proceso":
                                    estadoPedidoTXT.setTextColor(Color.parseColor("#219C52"));
                                    break;
                                case "Cancelado":
                                    estadoPedidoTXT.setTextColor(Color.parseColor("#EA1313"));
                                    break;
                                case "Recibido":
                                    estadoPedidoTXT.setTextColor(Color.parseColor("#1335EA"));
                                    break;
                                default:
                                    break;
                            }

                            estadoPedidoTXT.setText(String.valueOf(solicitudComida.getEstado()));

                            Button botonCancelar = view.findViewById(R.id.botonCancelarPedidoDetalleUsuario);

                            if(!solicitudComida.getEstado().equals("En espera")){
                                botonCancelar.setBackgroundColor(Color.parseColor("#A89F9F"));
                            }else{
                                botonCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        uidUsuario = mAuth.getCurrentUser().getUid();

                                        String idFoto = solicitudComida.getFotoUbicacion();
                                        String descp = solicitudComida.getDescripcion();
                                        int cant = solicitudComida.getCantidad();
                                        double total = solicitudComida.getPrecioTotal();

                                        SolicitudComida solicitudComida =
                                                new SolicitudComida(uidUsuario,keyComida,idFoto,null,"Cancelado",descp,cant,total);

                                        databaseReference.child("pedidos/"+keyPedido).setValue(solicitudComida);

                                        AppCompatActivity activity = (AppCompatActivity) getContext();
                                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_user,new FragmentPedidoComidasUsuario()).commit();
                                        //success message

                                    }
                                });
                            }

                            //------------------IMAGEN SLIDER------------------------
                            //Seteo de Imagen
                            Set<String> images = comida.getImagenes().keySet();
                            String[] img = images.toArray(new String[images.size()]);

                            for(int i=0;i<img.length;i++){
                                String urlNoModificada = String.valueOf(comida.getImagenes().get(img[i]));
                                String ruta = "";
                                for(int j=8;j < (urlNoModificada.length()-1);j++){
                                    ruta = ruta + urlNoModificada.charAt(j);
                                }

                                urls.add(ruta);

                            }

                            for(int i=0;i<urls.size();i++){
                                imageList.add(new SlideModel(urls.get(i),null));
                            }

                            sliderImagenes.setImageList(imageList, ScaleTypes.CENTER_CROP);
                            //----------------------------------------


                            //------------------IMAGEN UBICACION------------------------
                            imageRef.child(solicitudComida.getFotoUbicacion()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(getContext()).load(uri).override(300,250).into(imagenUbicacion);
                                }
                            });

                            //----------------------------------------


                        }else{
                            //error message
                        }

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

        return  view;

    }


}