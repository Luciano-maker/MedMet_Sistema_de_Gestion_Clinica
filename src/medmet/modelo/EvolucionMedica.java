package medmet.modelo;

import java.time.LocalDateTime;

public class EvolucionMedica {

    private int idEvolucion;
    private LocalDateTime fechaHora;
    private Medico medico;
    private String motivoConsulta;
    private String diagnostico;
    private String tratamiento;

    public EvolucionMedica( int idEvolucion, Medico medico, String motivoConsulta, String diagnostico, String tratamiento) {

        this.idEvolucion = idEvolucion;
        this.fechaHora = LocalDateTime.now();
        this.medico = medico;
        this.motivoConsulta = motivoConsulta;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
    }

    // ----------- Metodos GETTER -------------------
    public int getIdEvolucion() {
        return idEvolucion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Medico getMedico() {
        return medico;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    // ----------- Metodos SETTER -------------------
    public void setIdEvolucion(int idEvolucion) {
        this.idEvolucion = idEvolucion;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public void setDiagnostico(String diagnostico){
        this.diagnostico = diagnostico;
    }

    public void setTratamiento(String tratamiento){
        this.tratamiento = tratamiento;
    }
}
