package com.example.odyssiaproject.entidad;

public class Promociones {
    private String nombre;
    private String link;
    private String imagen;
    private String pais;


    public Promociones() {
    }

    public Promociones(String nombre, String link, String imagen, String pais) {
        this.nombre = nombre;
        this.link = link;
        this.imagen = imagen;
        this.pais = pais;
    }

    public String getNombre() { return nombre; }
    public String getLink() { return link; }
    public String getImagen() { return imagen; }
    public String getPais() { return pais; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setLink(String link) { this.link = link; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public void setPais(String pais) { this.pais = pais; }



    @Override
    public String toString() {
        return "Promociones{" +
                "nombre='" + nombre + '\'' +
                ", link='" + link + '\'' +
                ", imagen='" + imagen + '\'' +
                ", pais='" + pais + '\'' +
                '}';
    }
}