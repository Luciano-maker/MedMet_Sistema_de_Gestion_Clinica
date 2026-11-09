package medmet.modelo;

public class Especialidad {
    private int idEspecialidad;
    private String nombre;
    private String descripcion;
    private Boolean activo;

    // ----------- Constructor -------------------
    public Especialidad(int idEspecialidad, String nombre, String descripcion) {
        this.idEspecialidad = idEspecialidad;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = true;
    }

    // ------------------- Metodos GETTER -------------------
    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Boolean isActivo() {
        return activo;
    }

    // ------------------- Metodos SETTER -------------------
    public void setIdEspecialidad(int idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return nombre + " (ID: " + idEspecialidad + ")";
    }
}
