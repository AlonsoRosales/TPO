package com.example.tpo;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Set;

public class ComidasAdapterUsuario extends FirebaseRecyclerAdapter<Comida,ComidasAdapterUsuario.myViewHolder> {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    public ComidasAdapterUsuario(@NonNull FirebaseRecyclerOptions<Comida> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Comida comida) {
        int orden = position;

        holder.nombre.setText(String.valueOf(comida.getNombre()));
        holder.stock.setText(String.valueOf(comida.getStock()));

        if(comida.getStock() == 0){
            holder.fondo_comida.setBackgroundColor(Color.parseColor("#A89F9F"));
        }

        holder.precio.setText(String.valueOf(comida.getPrecio()));

        holder.tienda.setText("Tienda "+String.valueOf(comida.getIdTienda()));

        //------------------IMAGEN------------------------
        //Seteo de Imagen
        Set<String> images = comida.getImagenes().keySet();
        String[] img = images.toArray(new String[images.size()]);

        //Obtenemos una imagen aleatoria
        int n = (int) (Math.random() * (img.length - 1)) + 1;

        String url = String.valueOf(comida.getImagenes().get(img[n]));
        String ruta = "";
        for(int j=8;j < (url.length()-1);j++){
            ruta = ruta + url.charAt(j);
        }

        Glide.with(holder.imagenComida.getContext()).load(ruta).override(100,125).into(holder.imagenComida);
        //----------------------------------------


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comida.getStock() != 0){
                    String keyComida = getRef(orden).getKey();

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_user,
                            new FragmentDetalleComidaUsuario(keyComida)).addToBackStack(null).commit();

                }else{
                    //error message -> sin stock
                }
            }
        });


    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_comidas_usuario,parent,false);
        return new myViewHolder(view);
    }


    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView stock;
        TextView nombre;
        TextView tienda;
        TextView precio;
        ImageView imagenComida;
        LinearLayout fondo_comida;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            fondo_comida = itemView.findViewById(R.id.fondo_comida);
            nombre = itemView.findViewById(R.id.nombreComidaUsuario);
            stock = itemView.findViewById(R.id.stockComidaUsuario);
            tienda = itemView.findViewById(R.id.tiendaComidaUsuario);
            precio = itemView.findViewById(R.id.precioComidaUsuario);
            imagenComida = itemView.findViewById(R.id.iconImageViewComidaUsuario);
        }
    }


}
