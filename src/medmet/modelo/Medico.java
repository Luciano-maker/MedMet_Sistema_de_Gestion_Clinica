package medmet.modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Subclase Medico
public class Medico extends Persona{

    private int idMedico;
    private String matriculaProfesional;
    private Especialidad especialidad;
    private Boolean activo;

    private List<Turno> turnos;

    // ----------- Constructor -------------------
    public Medico(int idMedico, String dni, String nombre, String apellido, String telefono, String email,
                  String direccion, String matrculaProfesional, Especialidad especialidad) {

        // Instancia de Herencia
        super(dni, nombre, apellido, telefono, email, direccion);
        this.idMedico = idMedico;
        this.matriculaProfesional = matrculaProfesional;
        this.especialidad = especialidad;
        this.activo = true;
        this.turnos = new ArrayList<>();

    }

    // ----------- Metodos Logicos -------------------
    public boolean verificarDisponibilidad(LocalDate fecha, LocalTime hora) {   // Verifica si el calendario del medico, indicando true como disponible y false como ocupado
        if (fecha == null ||  hora == null) {
            return false;
        }
        for (Turno turno : turnos) {
            if (turno.getFecha().equals(fecha) && turno.getHora().equals(hora)) {   // Si coiciden la fecha y hora y si no se canceló el turno, entonces el medico está ocupado
                if (!"cancelado".equals(turno.getEstado())) {
                    return false;
                }

            }
        }
        return true;
    }

    public void agregarTurno(Turno turno) {     // Asocia un turno al medico
        if (turno != null) {
            this.turnos.add(turno);
        }
    }

    // ------------------- Metodos GETTER -------------------
    public int getIdMedico() {
        return idMedico;
    }

    public String getMatriculaProfesional() {
        return matriculaProfesional;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public boolean isActivo() {
        return activo;
    }

    public List<Turno> getTurnos() {
        return turnos;
    }

    // ------------------- Metodos SETTER -------------------
    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public void setMatriculaProfesional(String matriculaProfesional) {
        this.matriculaProfesional = matriculaProfesional;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
