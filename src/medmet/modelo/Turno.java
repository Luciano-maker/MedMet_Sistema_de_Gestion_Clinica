package medmet.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Turno {
    private int idTurno;
    private Paciente paciente;
    private Medico medico;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;              // Pendiente, En espera, En curso, Finalizado, Cancelado.
    private String estadoPago;          // Pendiente, Pagado.

    // ----------- Constructor -------------------
    public Turno (int idTurno, Paciente paciente, Medico medico, LocalDate fecha, LocalTime hora) {

        this.idTurno = idTurno;
        this.paciente = paciente;
        this.medico = medico;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = "Pendiente";
        this.estadoPago = "Pendiente";
    }

    // ------------------- Metodos Logicos -------------------
    public void confirmarTurno() {      // Confirma la asistencia del paciente en la sala de espera

        if ("Cancelado".equalsIgnoreCase(this.estado)) {
            this.estado = "En espera";
        }
    }

    public boolean isCanlable() {       // Valida si el turno se puede cancelar
        return "Pendiente".equalsIgnoreCase(this.estado) || "En espera".equalsIgnoreCase(this.estado);
    }

    public boolean cancelarTurno() {    // Vancela el turno liberando el espacio
        if (isCanlable()) {
            this.estado = "Cancelado";
            return true;
        }
        return false;
    }

    public boolean reprogranarTurno(LocalDate nuevaFecha, LocalTime nuevaHora) {        // Reprograma el turno con un nuevo dia y horario
        if (isCanlable()) {
            this.fecha = nuevaFecha;
            this.hora = nuevaHora;
            this.estado = "Pendiente";
            return true;
        }
        return false;
    }

    public void registrarPago() {       // Cambia el estado del pago de Pendiente a Pagado
        this.estadoPago = "Pagado";
    }

    public void mostrarDetalleTurno() {
        System.out.println("  Turno # " + this.idTurno + " | Fecha: " + this.fecha + " " + this.hora);
        System.out.println("| Paciente: " + paciente.getNombreCompleto());
        System.out.println("| Médico: Dr./Dra." + medico.getNombreCompleto());
        System.out.println("| Estado Cita: " + this.estado + " | " + this.estadoPago);
    }



    // ------------------- Metodos GETTER -------------------
    public int getIdTurno() {
        return idTurno;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public String getEstado() {
        return estado;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    // ------------------- Metodos SETTER -------------------
    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

}
