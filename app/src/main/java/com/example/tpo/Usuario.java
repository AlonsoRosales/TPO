package com.example.tpo;

public class Usuario {
    private String contrasena;
    private String correo;
    private String rol; //Acepta Long no String

    public Usuario() {
    }

    public Usuario(String contrasena,String correo, String rol) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
