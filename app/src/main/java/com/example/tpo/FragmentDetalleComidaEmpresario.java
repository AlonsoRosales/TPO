package com.example.tpo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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


public class FragmentDetalleComidaEmpresario extends Fragment {
    private String keyComida;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    MessageFragmentPedirComidaUsuario messageFragment;
    ArrayList<String> urls = new ArrayList<>();
    ArrayList<SlideModel> imageList = new ArrayList<>();

    public FragmentDetalleComidaEmpresario(String keyComida){
        this.keyComida = keyComida;
    }


    public FragmentDetalleComidaEmpresario() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("                    Detalle Comida");

        View view = inflater.inflate(R.layout.fragment_detalle_comida_empresario, container, false);
        TextView nameComida = view.findViewById(R.id.nombredetallecomidaEmpresario);
        TextView stockComida = view.findViewById(R.id.valorstockdetallecomidaEmpresario);
        TextView precioComida = view.findViewById(R.id.preciodetallecomidaEmpresario);
        TextView tiendaComida = view.findViewById(R.id.valortiendadetallecomidaEmpresario);
        TextView descripcionComida = view.findViewById(R.id.valordescripciondetallecomidaEmpresario);
        ImageSlider imageSlider = view.findViewById(R.id.sliderdetallecomidaEmpresario);

        ImageButton retroceder = view.findViewById(R.id.retroceder4);
        retroceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_empresario,new InicioFragmentEmpresario()).commit();
            }
        });

        databaseReference.child("comidas/"+keyComida).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    Comida comida = snapshot.getValue(Comida.class);

                    imageList.clear();

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

                }else{
                    //error message
                    Toast.makeText(getContext(), "La comida ya no existe!", Toast.LENGTH_LONG).show();
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_user,new InicioFragmentEmpresario()).addToBackStack(null).commit();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error message
            }
        });

        return  view;

    }

    @Override
    public void onPause() {
        super.onPause();
    }

}