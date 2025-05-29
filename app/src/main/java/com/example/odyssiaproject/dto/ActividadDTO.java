package com.example.odyssiaproject.dto;

public class ActividadDTO {

    private Long id;
    private String nombre;
    private String horario;
    private String precio;
    private String descripcion;
    private String imagen;
    private String link;

    private String nombreCiudad;  
    private String direccion;      // Puedes concatenar calle + código postal

    public ActividadDTO() {}

    public ActividadDTO(Long id, String nombre, String horario, String precio, String descripcion,
                        String imagen, String link, String nombreCiudad, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.horario = horario;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.link = link;
        this.nombreCiudad = nombreCiudad;
        this.direccion = direccion;
    }

    // Getters y setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getPrecio() { return precio; }
    public void setPrecio(String precio) { this.precio = precio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getNombreCiudad() { return nombreCiudad; }
    public void setNombreCiudad(String nombreCiudad) { this.nombreCiudad = nombreCiudad; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
