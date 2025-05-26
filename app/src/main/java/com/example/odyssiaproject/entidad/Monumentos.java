package com.example.odyssiaproject.entidad;

public class Monumentos {

    private String nombre;
    private String horario;
    private String precio;
    private String ciudad;
    private String imagen;
    private String link;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "Monumentos{" +
                "nombre='" + nombre + '\'' +
                ", horario='" + horario + '\'' +
                ", precio='" + precio + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", imagen='" + imagen + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
