package com.example.tpo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String correo;
    String contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button botonLogeo = findViewById(R.id.button);
        botonLogeo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean seguirLogeo = true;

                try{
                    EditText correoTxt = findViewById(R.id.textView4);
                    EditText contrasenaTxt = findViewById(R.id.textView6);

                    correo = correoTxt.getText().toString();
                    contrasena = contrasenaTxt.getText().toString();

                    if(correo.isEmpty() || correo == null || correo.equalsIgnoreCase("")){
                        correoTxt.setError("Ingrese un correo valido!");
                        seguirLogeo = false;
                    }

                    if(contrasena.isEmpty() || contrasena == null || contrasena.equalsIgnoreCase("")){
                        contrasenaTxt.setError("Ingrese una contraseña valida!");
                        seguirLogeo = false;
                    }

                    if(seguirLogeo){
                        firebaseAuth.signInWithEmailAndPassword(correo,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    String uid = firebaseAuth.getCurrentUser().getUid();

                                    databaseReference.child("usuarios/"+uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Usuario user = snapshot.getValue(Usuario.class);
                                            String rolDB = user.getRol();
                                            if(rolDB.equals("1")){
                                                Intent i = new Intent(MainActivity.this,InicioUsuarioActivity.class);
                                                startActivity(i);
                                                Toast.makeText(MainActivity.this,"Eres Cliente!",Toast.LENGTH_SHORT).show();
                                            }

                                            if(rolDB.equals("2")){
                                                Intent i = new Intent(MainActivity.this,InicioTrafiActivity.class);
                                                startActivity(i);
                                                Toast.makeText(MainActivity.this,"Eres Trafi!",Toast.LENGTH_SHORT).show();
                                            }

                                            if(rolDB.equals("3")){
                                                Intent i = new Intent(MainActivity.this,InicioEmpresarioActivity.class);
                                                startActivity(i);
                                                Toast.makeText(MainActivity.this,"Eres Empresario!",Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(MainActivity.this,"An error has ocurred!",Toast.LENGTH_SHORT).show();
                                        }

                                    });

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Error al iniciar sesión!",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else{
                        Toast.makeText(MainActivity.this,"Campo(s) incorrecto(s)!",Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    Toast.makeText(MainActivity.this,"Campo(s) incorrecto(s)!",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void textoCrearCuenta(View view){
        Intent i = new Intent(this,RegistroActivity.class);
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*
        Falta setearles la tienda
        //Creo al trafi1
        firebaseAuth.createUserWithEmailAndPassword("trafi1@tpo.oficial.com","123456").addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String uid = firebaseAuth.getUid();
                databaseReference.child("usuarios").child(uid).setValue(new Usuario("trafi1@tpo.oficial.com",sha256("123456"),"2"));
            }
        });


        //Creo al empresario1
        firebaseAuth.createUserWithEmailAndPassword("empresario1@tpo.oficial.com","123456").addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String uid1 = firebaseAuth.getUid();
                databaseReference.child("usuarios").child(uid1).setValue(new Usuario("empresario1@tpo.oficial.com",sha256("123456"),"3"));
            }
        });*/

       /*FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            Intent i = new Intent(MainActivity.this,InicioUsuarioActivity.class);
            finish();
            startActivity(i);
        }*/
    }

    public String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){

            return base;
        }
    }
}