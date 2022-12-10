package com.example.tpo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class InicioUsuarioActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    InicioFragmentUsuario inicioFragmentUsuario = new InicioFragmentUsuario();
    FragmentPedidoComidasUsuario solicitudesFragmentUsuario = new FragmentPedidoComidasUsuario();
    FragmentNotificacionesUsuario notificacionesFragmentUsuario = new FragmentNotificacionesUsuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_usuario);

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation_user);

        loadFragment(inicioFragmentUsuario);

        navigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.house_user:
                    loadFragment(inicioFragmentUsuario);
                    return true;
                case R.id.solicitud_user:
                    loadFragment(solicitudesFragmentUsuario);
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
        Intent i = new Intent(InicioUsuarioActivity.this,MainActivity.class);
        startActivity(i);
    }

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container_user,fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
    }

}