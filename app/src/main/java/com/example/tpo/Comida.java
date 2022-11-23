package com.example.tpo;

import java.io.Serializable;
import java.util.HashMap;

public class Comida implements Serializable {
    private String nombre;
    private String precio;
    private String descripcion;
    private int stock;
    private String idTienda;
    private HashMap<String,Object> imagenes;
    private String estado;

    public Comida(String nombre, String precio, String descripcion, int stock,HashMap<String,Object> imagenes,String estado,String idTienda) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.stock = stock;
        this.imagenes = imagenes;
        this.estado = estado;
        this.idTienda = idTienda;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public HashMap<String, Object> getImagenes() {
        return imagenes;
    }

    public void setImagenes(HashMap<String, Object> imagenes) {
        this.imagenes = imagenes;
    }

    public String getIdTienda() {
        return idTienda;
    }

    public void setIdTienda(String idTienda) {
        this.idTienda = idTienda;
    }

    public Comida() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
