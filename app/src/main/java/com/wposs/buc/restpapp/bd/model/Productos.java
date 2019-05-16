package com.wposs.buc.restpapp.bd.model;

public class Productos {

    private String id;
    private String nombre;
    private int valor;
    private String categoria;
    private String descripcion;

    public Productos() {
    }



    public Productos(String id, String nombre, String valor, String categoria, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.valor = Integer.parseInt(valor);
        this.categoria = categoria;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
