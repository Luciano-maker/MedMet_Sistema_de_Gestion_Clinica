package medmet.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HistoriaClinica {

    private int idHistoriaClinica;
    private Paciente paciente;
    private LocalDate fechaApertura;
    private String observacionesIniciales;

    private List<EvolucionMedica> evoluciones;

    public HistoriaClinica(int idHistoriaClinica, Paciente paciente, String observacionesIniciales) {

        this.idHistoriaClinica = idHistoriaClinica;
        this.paciente = paciente;
        this.fechaApertura = LocalDate.now();
        this.observacionesIniciales = observacionesIniciales;
        this.evoluciones = new ArrayList<>();
    }

    // ----------- Metodo Logico -------------------
    public void agregarEvolucion(EvolucionMedica evolucion) {
        if (evolucion != null) {
            this.evoluciones.add(evolucion);
        }
    }

    // ----------- Metodos GETTER -------------------
    public int getIdHistoriaClinica() {
        return idHistoriaClinica;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public LocalDate getFechaApertura() {
        return fechaApertura;
    }

    public String getObservaciones() {
        return observacionesIniciales;
    }

    public List<EvolucionMedica> getEvoluciones() {
        return evoluciones;
    }

    // ----------- Metodos SETTER -------------------
    public void setIdHistoriaClinica(int idHistoriaClinica) {
        this.idHistoriaClinica = idHistoriaClinica;
    }

    public void setFechaApertura(LocalDate fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public void setObservaciones(String observaciones) {
        this.observacionesIniciales = observaciones;
    }
}
