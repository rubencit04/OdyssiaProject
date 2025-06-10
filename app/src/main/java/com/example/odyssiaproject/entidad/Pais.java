package com.example.odyssiaproject.entidad;

import java.util.ArrayList;
import java.util.List;

public class Pais {
    private String id;
    private List<Ciudad> listaCiudades;
    private String nombre;
    private String imagen;

    private List<Promociones> listaPromociones;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Ciudad> getListaCiudades() {
        return listaCiudades;
    }

    public void setListaCiudades(List<Ciudad> listaCiudades) {
        this.listaCiudades = listaCiudades;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Pais(String imagen, String nombre) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.listaCiudades = new ArrayList<>();
    }

    public Pais() {
    }

    public List<Promociones
            > getListaPromociones() {
        return listaPromociones;
    }

    public void setListaPromociones(List<Promociones> listaPromociones) {
        this.listaPromociones = listaPromociones;
    }

    @Override
    public String toString() {
        return "Pais{" +
                "id='" + id + '\'' +
                ", listaCiudades=" + listaCiudades +
                ", nombre='" + nombre + '\'' +
                ", imagen='" + imagen + '\'' +
                ", listaPromociones=" + listaPromociones +
                '}';
    }
}