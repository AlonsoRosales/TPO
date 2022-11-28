package com.example.tpo;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

public class SolicitudesComidaAdapter extends FirebaseRecyclerAdapter<SolicitudComida,SolicitudesComidaAdapter.myViewHolder> {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    Comida comida;
    String keyComida;

    public SolicitudesComidaAdapter(@NonNull FirebaseRecyclerOptions<SolicitudComida> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull SolicitudComida solicitudComida) {
        keyComida = solicitudComida.getIdComida();
        int orden = position;

        databaseReference.child("comidas/"+keyComida).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    comida = snapshot.getValue(Comida.class);

                    holder.cantidadPedido.setText(String.valueOf(solicitudComida.getCantidad()));
                    holder.precioPedido.setText("S/"+String.valueOf(solicitudComida.getPrecioTotal()));
                    holder.nombrePedido.setText(String.valueOf(comida.getNombre()));

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
                        case "Recibido":
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

                        Glide.with(holder.imagenComidaPedido.getContext()).load(ruta).override(100,125).into(holder.imagenComidaPedido);
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
                    Glide.with(holder.imagenComidaPedido.getContext()).load(ruta).override(100,125).into(holder.imagenComidaPedido);
                    //----------------------------------------
                    */


                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String keyPedido = getRef(orden).getKey();

                            AppCompatActivity activity = (AppCompatActivity) view.getContext();
                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_user,
                                    new FragmentDetallePedidoComidaUsuario(keyPedido)).addToBackStack(null).commit();

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
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_pedidos,parent,false);
        return new myViewHolder(view);
    }


    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView nombrePedido;
        TextView cantidadPedido;
        TextView precioPedido;
        TextView estadoPedido;
        ImageView imagenComidaPedido;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            nombrePedido = itemView.findViewById(R.id.nombreComidaPedido);
            cantidadPedido = itemView.findViewById(R.id.stockComidaPedido);
            precioPedido = itemView.findViewById(R.id.precioComidaPedido);
            estadoPedido = itemView.findViewById(R.id.estadoComidaPedido);
            imagenComidaPedido = itemView.findViewById(R.id.imageComidita);

        }
    }
}
