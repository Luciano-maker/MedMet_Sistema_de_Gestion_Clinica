package medmet.modelo;

import java.time.LocalDate;
import java.time.Period;

// Subclase Paciente
public class Paciente extends Persona{

    private int idPaciente;
    private LocalDate fechaNacimiento;
    private String obraSocial;
    private Boolean activo = true;

    // ----------- Constructor -------------------
    public Paciente(int idPaciente, String dni, String nombre, String apellido, String telefono, String email,
                    String direccion, LocalDate fechaNacimiento, String obraSocial) {

        //Instancia de Herencia
        super(dni, nombre, apellido, telefono, email, direccion);
        this.idPaciente = idPaciente;
        this.fechaNacimiento = fechaNacimiento;
        this.obraSocial = obraSocial;
        this.activo = true;
    }

    // ------------------- Calcular edad Paciente -------------------
    public int calcularEdad() {
        if (fechaNacimiento == null) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    // ------------------- Metodos GETTER -------------------
    public int getIdPaciente() {
        return idPaciente;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getObraSocial() {
        return obraSocial;
    }

    public Boolean isActivo() {
        return activo;
    }

    // ------------------- Metodos SETTER -------------------
    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public void setObraSocial(String obraSocial) {
        this.obraSocial = obraSocial;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " (DNI: " + getDni() + ")";
    }
}
