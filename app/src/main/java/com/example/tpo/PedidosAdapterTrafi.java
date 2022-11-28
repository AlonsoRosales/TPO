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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

public class PedidosAdapterTrafi extends FirebaseRecyclerAdapter<SolicitudComida,PedidosAdapterTrafi.myViewHolder> {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    String keyComida;
    Comida comida;

    public PedidosAdapterTrafi(@NonNull FirebaseRecyclerOptions<SolicitudComida> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PedidosAdapterTrafi.myViewHolder holder, int position, @NonNull SolicitudComida solicitudComida) {
        int orden = position;
        keyComida = solicitudComida.getIdComida();

        databaseReference.child("comidas/"+keyComida).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    comida = snapshot.getValue(Comida.class);

                    holder.nombre.setText(String.valueOf(comida.getNombre()));
                    holder.cantidad.setText(String.valueOf(solicitudComida.getCantidad()));
                    holder.precio.setText("S/"+String.valueOf(solicitudComida.getPrecioTotal()));

                    switch (solicitudComida.getEstado()){
                        case "En espera":
                            holder.estadoPedido.setTextColor(Color.parseColor("#A9A319"));
                            break;
                        case "En proceso":
                            holder.estadoPedido.setTextColor(Color.parseColor("#219C52"));
                            break;
                        case "Cancelado":
                            holder.estadoPedido.setTextColor(Color.parseColor("#EA1313"));
                            break;
                        case "Entregado":
                            holder.estadoPedido.setTextColor(Color.parseColor("#1335EA"));
                            break;
                        default:
                            break;
                    }
                    holder.estadoPedido.setText(String.valueOf(solicitudComida.getEstado()));


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

                    /*
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
                    */

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String keyPedido = getRef(orden).getKey();
                            AppCompatActivity activity = (AppCompatActivity) view.getContext();
                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_trafi,
                                    new FragmentDetallePedidoComidaTrafi(keyPedido)).addToBackStack(null).commit();
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

    }


    @NonNull
    @Override
    public PedidosAdapterTrafi.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_pedidos_trafi,parent,false);
        return new PedidosAdapterTrafi.myViewHolder(view);
    }


    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView cantidad;
        TextView nombre;
        TextView precio;
        ImageView imagenComida;
        TextView estadoPedido;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nombreComidaPedidoTrafi);
            cantidad = itemView.findViewById(R.id.stockComidaPedidoTrafi);
            precio = itemView.findViewById(R.id.precioComidaPedidoTrafi);
            imagenComida = itemView.findViewById(R.id.imageComiditaTrafi);
            estadoPedido = itemView.findViewById(R.id.estadoComidaPedidoTrafi);
        }
    }


}
