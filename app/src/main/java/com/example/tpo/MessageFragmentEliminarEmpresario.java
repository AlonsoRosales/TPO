package com.example.tpo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class MessageFragmentEliminarEmpresario extends DialogFragment {


    public MessageFragmentEliminarEmpresario() {

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

        final View view = inflater.inflate(R.layout.fragment_message_eliminar_empresario, null);

        builder.setView(view);

        return builder.create();
    }
}