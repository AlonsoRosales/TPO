package com.example.tpo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;


public class RegistroActivity extends AppCompatActivity {
    String correo;
    String contrasena;
    String confirmarContrasena;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Button botonRegistro = findViewById(R.id.botonRegistro);
        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean guardar = true;

                try {

                    EditText correoText = findViewById(R.id.correoRegistro);
                    String correoHelper = correoText.getText().toString();
                    if (correoHelper.equalsIgnoreCase("") || correoHelper == null || correoHelper.isEmpty()) {
                        correoText.setError("Ingrese un correo válido");
                        guardar = false;

                    } else {
                        if (correoHelper.contains("@")) {
                            String[] partesCorreo = correoHelper.split("@");

                            if (partesCorreo[0].equals("")) {
                                correoText.setError("Ingrese un correo válido");
                                guardar = false;
                            }

                            if (!partesCorreo[1].equals("pucp.edu.pe") && !partesCorreo[1].equals("tpo.oficial.com")) {
                                correo = correoHelper;

                            } else {
                                correoText.setError("No se permite ese tipo de correos");
                                guardar = false;
                            }

                        } else {
                            correoText.setError("Ingrese un correo válido");
                            guardar = false;
                        }
                    }


                    TextView contraText = findViewById(R.id.contraRegistro);
                    contrasena = contraText.getText().toString();
                    if (contrasena.equalsIgnoreCase("") || contrasena == null || contrasena.isEmpty()) {
                        contraText.setError("Ingrese su contraseña");
                        guardar = false;
                    }

                    TextView recontraText = findViewById(R.id.recontraRegistro);
                    confirmarContrasena = recontraText.getText().toString();
                    if (confirmarContrasena.equalsIgnoreCase("") || confirmarContrasena == null || confirmarContrasena.isEmpty()) {
                        recontraText.setError("Ingrese su contraseña");
                        guardar = false;
                    }

                    if (!contrasena.equals(confirmarContrasena)) {
                        guardar = false;
                        recontraText.setError("Las contraseñas no coinciden");
                    }


                    if(guardar){
                        firebaseAuth.createUserWithEmailAndPassword(correo,contrasena).addOnCompleteListener(RegistroActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    String uid = firebaseAuth.getUid();

                                    //volley
                                    RequestQueue requestQueue = Volley.newRequestQueue(RegistroActivity.this);
                                    String url = "https://servicetpo.azurewebsites.net/api/HttpTriggerTPO?";
                                    //String url = "http://192.168.1.36:7071/api/HttpTriggerTPO";
                                    StringRequest stringRequest = new StringRequest(
                                            Request.Method.POST,
                                            url,
                                            response -> {
                                                System.out.println(response);
                                            },
                                            error -> System.out.println(error.getMessage())){
                                        @Nullable
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String,String> params = new HashMap<>();
                                            params.put("UID",uid);
                                            params.put("correo",correo);
                                            params.put("pass",sha256(contrasena));
                                            params.put("rol","1");
                                            return  params;
                                        }
                                    };
                                    requestQueue.add(stringRequest);

                                    //databaseReference.child("usuarios").child(uid).setValue(new Usuario(sha256(contrasena),correo,"1"));
                                    Toast.makeText(RegistroActivity.this,"Cuenta creada exitosamente!",Toast.LENGTH_SHORT).show();

                                }else{
                                    Toast.makeText(RegistroActivity.this,"Correo o contraseña incorrectas!",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }else{
                        Toast.makeText(RegistroActivity.this,"Campo(s) incorrecto(s)!",Toast.LENGTH_SHORT).show();
                    }


                }catch (Exception e){
                    Toast.makeText(RegistroActivity.this,"Campo(s) incorrecto(s)!",Toast.LENGTH_SHORT).show();
                }


            }
        });

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

    public void textoTengoCuentaRetroceder(View view){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }


    public void botonRetrocederRegistro(View view){
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

}