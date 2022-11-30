package com.example.tpo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Random;


public class MessageFragmentCRUDEmpresario extends DialogFragment {
    String keyComida;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    public MessageFragmentCRUDEmpresario(String keyComida) {
        this.keyComida = keyComida;
    }

    @Override
    public void onStart() {
        super.onStart();

        getDialog().getWindow().setLayout(1000, 500);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_message_c_r_u_d_empresario, null);

        Button botonEditar = view.findViewById(R.id.botoneditarcomida);
        Button botonEliminar = view.findViewById(R.id.botoneliminarcomida);

        botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_empresario,
                        new FragmentActualizarComidaEmpresario(keyComida)).addToBackStack(null).commit();
                dismiss();
            }
        });

        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference.child("pedidos").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot == null){
                            //error message
                        }else{
                            boolean existePedido = false;
                            for(DataSnapshot sp : snapshot.getChildren()){
                                SolicitudComida pedido = sp.getValue(SolicitudComida.class);

                                if(pedido.getIdComida().equals(keyComida)){
                                    if(pedido.getIdentificador() == 1){
                                        existePedido = true;

                                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                                        MessageFragmentEliminarEmpresario messageEliminar = new MessageFragmentEliminarEmpresario();
                                        messageEliminar.show(activity.getSupportFragmentManager(),"Eliminar Fragment");

                                        dismiss();
                                        break;
                                    }
                                }

                            }

                            if(!existePedido){
                                databaseReference.child("comidas/"+keyComida).child("estado").setValue("0");
                                dismiss();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //error message
                    }
                });

            }
        });


        builder.setView(view);

        return builder.create();
    }



}