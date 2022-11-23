package com.example.tpo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ComidasAdapter extends RecyclerView.Adapter<ComidasAdapter.myViewHolder> {
    private List<Comida> listaComidas;
    private Context context;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Comida> getListaComidas() {
        return listaComidas;
    }

    public void setListaComidas(List<Comida> listaComidas) {
        this.listaComidas = listaComidas;
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        Comida comida;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_comidas,parent,false);
        return new myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        int posicion = position;

        Comida comidita = listaComidas.get(posicion);
        holder.comida = comidita;

        TextView nombre = holder.itemView.findViewById(R.id.nombreComida);
        TextView stock = holder.itemView.findViewById(R.id.stockComida);
        TextView precio = holder.itemView.findViewById(R.id.precioComida);
        ImageView imagen = holder.itemView.findViewById(R.id.iconImageViewComida);

        nombre.setText(String.valueOf(comidita.getNombre()));
        stock.setText(String.valueOf(comidita.getStock()));
        precio.setText(String.valueOf(comidita.getPrecio()));

        //Seteo de Imagen
        Set<String> images = comidita.getImagenes().keySet();
        String[] img = images.toArray(new String[images.size()]);


        //Obtenemos una imagen aleatoria
        int n = (int) (Math.random() * (img.length - 1)) + 1;

        String url = String.valueOf(comidita.getImagenes().get(img[n]));
        String ruta = "";
        for(int j=8;j < (url.length()-1);j++){
            ruta = ruta + url.charAt(j);
        }

        Glide.with(getContext()).load(ruta).into(imagen);

    }

    @Override
    public int getItemCount() {
        return listaComidas.size();
    }




}
