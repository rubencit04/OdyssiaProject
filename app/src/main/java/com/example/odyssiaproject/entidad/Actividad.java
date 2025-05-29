package com.example.odyssiaproject.entidad;

public class Actividad {
    private String nombre, horario;
    private String precio;
    private Direccion direccion;
    private Ciudad ciudad;

    private String link;
    private String imagen;

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

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "Actividad{" +
                "nombre='" + nombre + '\'' +
                ", horario='" + horario + '\'' +
                ", precio='" + precio + '\'' +
                ", direccion=" + direccion +
                ", ciudad=" + ciudad +
                ", link='" + link + '\'' +
                ", imagen='" + imagen + '\'' +
                '}';
    }
}
