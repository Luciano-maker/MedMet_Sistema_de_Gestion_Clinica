package medmet.modelo;

import java.time.LocalDate;

public class Recepcionista extends Persona{
    private int idRecepcionista;
    private LocalDate fechaIngreso;
    private Boolean activo;

    // ----------- Constructor -------------------
    public Recepcionista(int idRecepcionista, String dni, String nombre, String apellido, String telefono, String email,
                         String direccion, LocalDate fechaIngreso) {

        super(dni, nombre, apellido, telefono, email, direccion);
        this.idRecepcionista = idRecepcionista;
        this.fechaIngreso = fechaIngreso;
        this.activo = true;
    }

    // ------------------- Metodos GETTER -------------------
    public int getIdRecepcionista() {
        return idRecepcionista;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public Boolean isActivo() {
        return activo;
    }

    // ------------------- Metodos SETTER -------------------
    public void setIdRecepcionista(int idRecepcionista) {
        this.idRecepcionista = idRecepcionista;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}

