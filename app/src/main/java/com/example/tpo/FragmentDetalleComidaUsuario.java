package com.example.tpo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Set;


public class FragmentDetalleComidaUsuario extends Fragment {
    private String keyComida;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    MessageFragmentPedirComidaUsuario messageFragment;
    ArrayList<String> urls;
    ArrayList<SlideModel> imageList;

    public FragmentDetalleComidaUsuario(String keyComida){
        this.keyComida = keyComida;
    }


    public FragmentDetalleComidaUsuario() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detalle_comida_usuario, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("                      Detalle Comida");

        TextView nameComida = view.findViewById(R.id.nombredetallecomida);
        TextView stockComida = view.findViewById(R.id.valorstockdetallecomida);
        TextView precioComida = view.findViewById(R.id.preciodetallecomida);
        TextView tiendaComida = view.findViewById(R.id.valortiendadetallecomida);
        TextView descripcionComida = view.findViewById(R.id.valordescripciondetallecomida);
        ImageSlider imageSlider = view.findViewById(R.id.sliderdetallecomida);

        Button botonReservar = view.findViewById(R.id.botonRealizarPedido);
        botonReservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botonReservarComida(view);
            }
        });

        ImageButton retroceder = view.findViewById(R.id.retroceder);
        retroceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container_user,new InicioFragmentUsuario());
                transaction.commit();
            }
        });


        databaseReference.child("comidas/"+keyComida).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    Comida comida = snapshot.getValue(Comida.class);

                    if(comida.getEstado().equals("0") || comida.getStock() == 0){
                        //error message
                        //Toast.makeText(getContext(), "Ya no esta disponible la comida!", Toast.LENGTH_SHORT).show();
                        AppCompatActivity activity = (AppCompatActivity) getContext();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_user, new InicioFragmentUsuario()).addToBackStack(null).commit();
                    }else{
                        imageList = new ArrayList<>();
                        imageList.clear();
                        urls = new ArrayList<>();

                        nameComida.setText(String.valueOf(comida.getNombre()));
                        precioComida.setText(String.valueOf(comida.getPrecio()));
                        stockComida.setText(String.valueOf(comida.getStock()));
                        tiendaComida.setText("Tienda "+ String.valueOf(comida.getIdTienda()));
                        descripcionComida.setText(String.valueOf(comida.getDescripcion()));

                        //------------------IMAGEN------------------------
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

                        imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP);
                        //----------------------------------------

                    }

                }else{
                    //error message
                    Toast.makeText(getContext(), "La comida ya no existe!", Toast.LENGTH_SHORT).show();
                    AppCompatActivity activity = (AppCompatActivity) getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_user, new InicioFragmentUsuario()).addToBackStack(null).commit();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error message
                //Toast.makeText(getContext(), "An error has ocurred!", Toast.LENGTH_SHORT).show();
                AppCompatActivity activity = (AppCompatActivity) getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_user, new InicioFragmentUsuario()).addToBackStack(null).commit();
            }
        });

        return  view;

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void botonReservarComida(View view){
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        messageFragment = new MessageFragmentPedirComidaUsuario(keyComida);

        messageFragment.show(activity.getSupportFragmentManager(),"Mi Fragment Pedir Comida");

    }

}