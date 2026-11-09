package medmet.gestor;

import medmet.dao.TurnoDAO;
import medmet.excepciones.MedMetException;
import medmet.excepciones.TurnoInvalidoException;
import medmet.modelo.Medico;
import medmet.modelo.Paciente;
import medmet.modelo.Turno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Gestor responsable de la administracion de Turnos
public class GestorTurnos {

    private final TurnoDAO turnoDao;

    // Constructor
    public GestorTurnos() {
        this.turnoDao = new TurnoDAO();
    }

    // Agenda un turno validando restricciones de calendario y disponibilidad
    public boolean agendarTurno(int idPaciente, int idMedico, LocalDate fecha, LocalTime hora) throws MedMetException {

        if (turnoDao.verificarSolapamiento(idMedico, fecha, hora)) {
            throw new MedMetException("El profesional seleccionado ya cuenta con un turno activo en esa fecha y horario.");
        }
        boolean exito = turnoDao.registrarTurno(idPaciente, idMedico, fecha, hora);
        if (!exito) {
            throw new MedMetException("No se pudo registrar el turno");
        }
        return true;
    }

    // Consulta turnos aplicando filtros especificos
    public List<Turno> buscarTurnos(LocalDate fecha, Integer idMedico, Integer idEspecialidad, String criterioPaciente) {

        try {
            return turnoDao.buscarTurnos(fecha, idMedico, idEspecialidad, criterioPaciente);
        } catch (MedMetException e) {
            System.err.println("Aviso al consultar turnos: " + e.getMessage());

            return new ArrayList<>();
        }
    }


    // Ordena los turnos cronologicamente
    public List<Turno> ordenarTurnosPorHorario(LocalDate fecha) {

        try {
            return turnoDao.listarPorFecha(fecha);
        } catch (MedMetException e) {
            System.err.println("Aviso al listar agenda: " + e.getMessage());

            return new ArrayList<>();
        }
    }


    // Reprograma un turno modificando la fecha y horario ya registrado
    public boolean reprogramarTurno(int idTurno, LocalDate nuevaFecha, LocalTime nuevaHora) throws MedMetException {

        Turno turno = turnoDao.buscarPorId(idTurno);

        if (turno == null) {
            throw new MedMetException("El turno seleccionado no existe en el sistema");
        }

        if ("Atendido".equalsIgnoreCase(turno.getEstado()) || "Cancelado".equalsIgnoreCase(turno.getEstado())) {
            throw new MedMetException("No se puede reprogramar un turno en estado '" + turno.getEstado() + "'");
        }

        if (turnoDao.solapamientoReprogramacion(turno.getMedico().getIdMedico(), nuevaFecha, nuevaHora, idTurno)) {
            throw new MedMetException("El médico ya tiene asignado otro turno en la fecha y horario solicitado.");
        }

        boolean exito = turnoDao.reprogramarTurno(idTurno, nuevaFecha, nuevaHora);
        if (!exito) {
            throw new MedMetException("No se pudo actualizar el horario");
        }
        return true;
    }

    // Cancela un turno programado y libera el espacio
    public boolean cancelarTurno(int idTurno) throws MedMetException, TurnoInvalidoException {

        try {
            return turnoDao.actualizarEstado(idTurno, "Cancelado");
        } catch (MedMetException e) {
            System.err.println("Error al cancelar el turno: " + e.getMessage());

            return false;
        }
    }

    public boolean confirmarLlegada(int idTurno) throws MedMetException {      // Registra la llegada del paciente a
                                                                            // la sala de espera
        try {
            return turnoDao.actualizarEstado(idTurno, "En espera");
        } catch (MedMetException e) {
            System.err.println("Error al confirmar llegada: " + e.getMessage());

            return false;
        }
    }

    public List<Turno> listaCompletaTurnos() {      // Retorna todos los turnos agendados
        return buscarTurnos(null, null, null, null);
    }

    public boolean registrarPago(int idTurno) {

        try {
            return turnoDao.actualizarEstadoPago(idTurno, "PAGADO");
        } catch (MedMetException e) {
            System.err.println("Error al registrar el pago del turno: " + e.getMessage());

            return false;
        }
    }
}
