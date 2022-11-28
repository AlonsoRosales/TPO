package com.example.tpo;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Set;

public class ComidasAdapterEmpresario  extends FirebaseRecyclerAdapter<Comida,ComidasAdapterEmpresario.myViewHolder> {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    public ComidasAdapterEmpresario(@NonNull FirebaseRecyclerOptions<Comida> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ComidasAdapterEmpresario.myViewHolder holder, int position, @NonNull Comida comida) {
        int orden = position;

        holder.nombreComida.setText(String.valueOf(comida.getNombre()));
        holder.stockComida.setText(String.valueOf(comida.getStock()));

        if(comida.getStock() == 0){
            holder.fondo_comida.setBackgroundColor(Color.parseColor("#A89F9F"));
        }

        holder.precioComida.setText(comida.getPrecio());
        holder.tiendaComida.setText("Tienda "+comida.getIdTienda());

        switch (comida.getEstado()){
            case "0":
                holder.estadoComida.setText("No disponible");
                holder.estadoComida.setTextColor(Color.parseColor("#EA1313"));
                break;
            case "1":
                holder.estadoComida.setText("Disponible");
                holder.estadoComida.setTextColor(Color.parseColor("#1335EA"));
                break;
            default:
                break;
        }

        //------------------IMAGEN------------------------
        //Seteo de Imagen
        if(comida.getImagenes() != null){
            Set<String> images = comida.getImagenes().keySet();
            String[] img = images.toArray(new String[images.size()]);

            //Obtenemos una imagen aleatoria
            int n = 0;
            if(img.length != 1){
                n = (int) (Math.random() * (img.length - 1)) + 1;
            }
            //int n = (int) (Math.random() * (img.length - 1)) + 1;

            String url = String.valueOf(comida.getImagenes().get(img[n]));
            String ruta = "";
            for(int j=8;j < (url.length()-1);j++){
                ruta = ruta + url.charAt(j);
            }

            Glide.with(holder.imagenComida.getContext()).load(ruta).override(100,125).into(holder.imagenComida);
        }
        //----------------------------------------
        /*Set<String> images = comida.getImagenes().keySet();
        String[] img = images.toArray(new String[images.size()]);

        //Obtenemos una imagen aleatoria
        int n = 0;
        if(img.length != 1){
            n = (int) (Math.random() * (img.length - 1)) + 1;
        }
        //int n = (int) (Math.random() * (img.length - 1)) + 1;

        String url = String.valueOf(comida.getImagenes().get(img[n]));
        String ruta = "";
        for(int j=8;j < (url.length()-1);j++){
            ruta = ruta + url.charAt(j);
        }

        Glide.with(holder.imagenComida.getContext()).load(ruta).override(100,125).into(holder.imagenComida);
        */
        //----------------------------------------


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String keyComida = getRef(orden).getKey();
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_empresario,
                        new FragmentDetalleComidaEmpresario(keyComida)).addToBackStack(null).commit();
            }
        });

    }



    @NonNull
    @Override
    public ComidasAdapterEmpresario.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_comidas,parent,false);
        return new myViewHolder(view);
    }


    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView nombreComida;
        TextView stockComida;
        TextView precioComida;
        TextView estadoComida;
        TextView tiendaComida;
        ImageView imagenComida;
        LinearLayout fondo_comida;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            fondo_comida = itemView.findViewById(R.id.fondo_comida_empresario);
            imagenComida = itemView.findViewById(R.id.imagenComidaEmpresario);
            nombreComida = itemView.findViewById(R.id.nombreComidaEmpresario);
            precioComida = itemView.findViewById(R.id.precioComidaEmpresario);
            stockComida = itemView.findViewById(R.id.stockComidaEmpresario);
            estadoComida = itemView.findViewById(R.id.estadoComidaEmpresario);
            tiendaComida = itemView.findViewById(R.id.tiendaComidaEmpresario);


        }
    }
}
