package com.example.tpo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class InicioTrafiActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    InicioFragmentTrafi inicioFragmentTrafi = new InicioFragmentTrafi();
    FragmentHistorialPedidosTrafi solicitudesFragmentTrafi = new FragmentHistorialPedidosTrafi();
    FragmentNotificacionesTrafi notificacionesFragmentTrafi = new FragmentNotificacionesTrafi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_trafi);

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation_trafi);

        loadFragment(inicioFragmentTrafi);

        navigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.house_user:
                    loadFragment(inicioFragmentTrafi);
                    return true;
                case R.id.solicitud_user:
                    loadFragment(solicitudesFragmentTrafi);
                    return true;
                case R.id.notificacion_user:
                    loadFragment(notificacionesFragmentTrafi);
                    return true;
            }
            return false;
        });


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
        Intent i = new Intent(InicioTrafiActivity.this,MainActivity.class);
        startActivity(i);
    }

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container_trafi,fragment);
        transaction.commit();
    }

    /*@Override
    public void onBackPressed() {
    }*/
}