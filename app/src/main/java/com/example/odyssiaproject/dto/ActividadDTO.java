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
    private double precioNumerico;
    public ActividadDTO() {}

    public ActividadDTO(Long id, String nombre, String horario, String precio, String descripcion,
                        String imagen, String link, String nombreCiudad, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.horario = horario;
        this.setPrecio(precio);
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
    public void setPrecio(String precio) {
        this.precio = precio;
        // Calcula el valor numérico cada vez que se establece el precio en texto
        this.precioNumerico = parsePrecioToDouble(precio);
    }

    public double getPrecioNumerico() {
        return precioNumerico;
    }
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
    public void calcularPrecioNumerico() {
        this.precioNumerico = parsePrecioToDouble(this.precio);
    }
    /**
     * Convierte un string de precio (ej. "GRATIS", "Desde 8€") a un valor numérico.
     * Devuelve 0.0 para "GRATIS", el número extraído para "Desde X€" o "X€",
     * y -1.0 para "N/A" o formatos no reconocidos.
     *
     * @param precioStr El string del precio a parsear.
     * @return El valor numérico del precio, o -1.0 si no se puede parsear.
     */
    private double parsePrecioToDouble(String precioStr) {
        if (precioStr == null || precioStr.trim().isEmpty()) {
            return -1.0; // Valor para "no disponible" o nulo
        }

        String cleanedPrecio = precioStr.toUpperCase().trim();

        if (cleanedPrecio.contains("GRATIS")) {
            return 0.0;
        } else if (cleanedPrecio.startsWith("DESDE ")) {
            try {
                // Elimina "DESDE " y luego todos los caracteres que no sean dígitos, puntos o comas
                String numStr = cleanedPrecio.substring("DESDE ".length()).replaceAll("[^\\d.,]", "").replace(",", ".");
                if (!numStr.isEmpty()) {
                    return Double.parseDouble(numStr);
                }
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear precio 'Desde': " + precioStr + " - " + e.getMessage());
            }
        } else {
            try {
                String numStr = cleanedPrecio.replaceAll("[^\\d.,]", "").replace(",", ".");
                if (!numStr.isEmpty()) {
                    return Double.parseDouble(numStr);
                }
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear precio directo: " + precioStr + " - " + e.getMessage());
            }
        }
        return -1.0;
    }
}