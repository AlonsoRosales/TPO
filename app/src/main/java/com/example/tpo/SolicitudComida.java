package com.example.tpo;

public class SolicitudComida {
    private String idUsuario;
    private String idComida;
    private String fotoUbicacion;
    private String coordenadas;
    private String estado;
    private String descripcion;
    private int cantidad;
    private double precioTotal;
    private int identificador;

    public SolicitudComida(String idUsuario, String idComida, String fotoUbicacion, String coordenadas, String estado, String descripcion, int cantidad,double precioTotal,int identificador) {
        this.idUsuario = idUsuario;
        this.idComida = idComida;
        this.fotoUbicacion = fotoUbicacion;
        this.coordenadas = coordenadas;
        this.estado = estado;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioTotal = precioTotal;
        this.identificador = identificador;
    }


    public SolicitudComida() {
    }

    public int getIdentificador() {
        return identificador;
    }

    public void setIdentificador(int identificador) {
        this.identificador = identificador;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdComida() {
        return idComida;
    }

    public void setIdComida(String idComida) {
        this.idComida = idComida;
    }

    public String getFotoUbicacion() {
        return fotoUbicacion;
    }

    public void setFotoUbicacion(String fotoUbicacion) {
        this.fotoUbicacion = fotoUbicacion;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
