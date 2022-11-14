package com.example.tpo;

public class Usuario {
    private String contrasena;
    private String correo;
    private String rol;
    private String tienda;

    public Usuario() {
    }

    public Usuario(String contrasena,String correo, String rol) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public Usuario(String contrasena, String correo, String rol, String tienda) {
        this.contrasena = contrasena;
        this.correo = correo;
        this.rol = rol;
        this.tienda = tienda;
    }

    public String getTienda() {
        return tienda;
    }

    public void setTienda(String tienda) {
        this.tienda = tienda;
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
