package com.example.odyssiaproject.entidad;

public class Actividad {

    public Actividad(String nombre) {
        this.nombre = nombre;
    }

    private String nombre, horario;
    private String precio;
    private Direccion direccion;
    private Ciudad ciudad;

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

    @Override
    public String toString() {
        return "Actividad{" +
                "nombre='" + nombre + '\'' +
                ", horario='" + horario + '\'' +
                ", precio=" + precio +
                ", direccion=" + direccion +
                ", ciudad=" + ciudad +
                '}';
    }
}
