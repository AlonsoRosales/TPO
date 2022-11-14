package com.example.tpo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class InicioEmpresarioActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_empresario);
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cerrar_sesion, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){
            case R.id.cerrarSesion:
                cerrarSesion();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void cerrarSesion(){
        firebaseAuth.signOut();
        finish();
        Intent i = new Intent(InicioEmpresarioActivity.this,MainActivity.class);
        startActivity(i);
    }
}