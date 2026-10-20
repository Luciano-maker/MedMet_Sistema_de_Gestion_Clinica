package medmet.gestor;

import medmet.excepciones.MedMetException;
import medmet.excepciones.TurnoInvalidoException;
import medmet.modelo.Especialidad;
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

    private final BancoDatos bancoDatos;

    // Constructor
    public GestorTurnos() {
        this.bancoDatos = BancoDatos.getInstancia();
    }

    // Agenda un turno validando restricciones de calendario y disponibilidad
    public Turno agendarTurno(int idPaciente, int idMedico, LocalDate fecha, LocalTime hora)
            throws MedMetException, TurnoInvalidoException {

        if (fecha == null || fecha.isBefore(LocalDate.now())) {     // Valida si la fecha ingresada es anterior a la actual
            throw new TurnoInvalidoException("No se puede agendar turnos con fechas pasadas");
        }

        if (hora == null) {
            throw new TurnoInvalidoException("Seleccione un horario valido");
        }

        // Busca al paciente en el repositorio
        Paciente paciente = null;

        for (Paciente p : bancoDatos.getPacientes()) {
            if (p.getIdPaciente() == idPaciente && p.isActivo()) {
                paciente = p;
                break;
            }
        }

        if (paciente == null) {
            throw new MedMetException("Paciente seleccionado no existe");
        }

        // Busca al medico en el repositorio
        Medico medico = null;

        for (Medico m : bancoDatos.getMedicos()) {
            if (m.getIdMedico() == idMedico && m.isActivo()) {
                medico = m;
                break;
            }
        }

        if (medico == null) {
            throw new MedMetException("Medico seleccionado no existe");
        }

        if (!medico.verificarDisponibilidad(fecha, hora)) {     // Valida la agenda del medico seleccionado
            throw new TurnoInvalidoException("El horario seleccionado está ocupado");
        }

        int nuevoId = bancoDatos.generarIdTurno();
        Turno nuevoTurno = new Turno(nuevoId, paciente, medico, fecha, hora);

        medico.agregarTurno(nuevoTurno);        // Registra el turno en la agenda
        bancoDatos.getTurnos().add(nuevoTurno);

        return nuevoTurno;
    }

    // Consulta turnos aplicando filtros especificos
    public List<Turno> buscarTurnos(LocalDate fecha, Integer idMedico, Integer idEspecialidad, String criterioPaciente) {

        List<Turno> resultado = new ArrayList<>();

        String criterioPacLimpio = "";
        if (criterioPaciente != null && !criterioPaciente.trim().isEmpty()) {
            criterioPacLimpio = criterioPaciente.trim().toLowerCase();
        }

        for (Turno t : bancoDatos.getTurnos()) {

            if (fecha != null && !t.getFecha().equals(fecha)) {     // Filtro por fecha
                continue;
            }


            if (idMedico != null) {     // Filtro por médico
                if (t.getMedico() == null || t.getMedico().getIdMedico() != idMedico) {
                    continue;
                }
            }


            if (idEspecialidad != null) {       // Filtro por especialidad
                Medico med = t.getMedico();
                if (med == null || med.getEspecialidad() == null ||
                        med.getEspecialidad().getIdEspecialidad() != idEspecialidad) {
                    continue;
                }
            }

            if (!criterioPacLimpio.isEmpty()) {     // Filtro por DNI o Nombre del paciente
                Paciente pac = t.getPaciente();
                if (pac == null) {
                    continue;
                }

                boolean coincideDni = pac.getDni() != null && pac.getDni().toLowerCase().contains(criterioPacLimpio);
                boolean coincideNombre = pac.getNombreCompleto() != null && pac.getNombreCompleto().toLowerCase().contains(criterioPacLimpio);

                if (!coincideDni && !coincideNombre) {
                    continue;
                }
            }

            resultado.add(t);
        }

        return resultado;
    }


    // Ordena los turnos cronologicamente
    public List<Turno> ordenarTurnosxHorario(LocalDate fecha) {

        List<Turno> filtrados = new ArrayList<>();

        for (Turno t :  bancoDatos.getTurnos()) {

            if (fecha == null || t.getFecha().isEqual(fecha)) {
                filtrados.add(t);
            }
        }

        Collections.sort(filtrados, new Comparator<Turno>() {
            @Override
            public int compare(Turno t1, Turno t2) {
                return t1.getHora().compareTo(t2.getHora());
            }
        });

        return filtrados;
    }

    public Turno buscarTurnoxId(int idTurno) {      // Localiza un turno por su ID
        for (Turno t : bancoDatos.getTurnos()) {
            if (t.getIdTurno() == idTurno) {
                return t;
            }
        }

        return null;
    }


    // Reprograma un turno modificando la fecha y horario ya registrado
    public void reprogramarTurno(int idTurno, LocalDate nuevaFecha, LocalTime nuevaHora)
            throws MedMetException, TurnoInvalidoException {

        Turno turno = buscarTurnoxId(idTurno);

        if (turno == null) {
            throw new MedMetException("Turno seleccionado no existe");
        }

        if (nuevaFecha == null || nuevaFecha.isBefore(LocalDate.now())) {
            throw new TurnoInvalidoException("La nueva fecha no puede ser anterior a la fecha de hoy");
        }

        if (!turno.isCanlable()) {
            throw new TurnoInvalidoException("No es posible reprogramar un turno finalizado o en curso");
        }

        Medico medico = turno.getMedico();

        if (!medico.verificarDisponibilidad(nuevaFecha, nuevaHora)) {
            throw new TurnoInvalidoException("Turno no disponible");
        }

        turno.reprogranarTurno(nuevaFecha, nuevaHora);
    }

    // Cancela un turno programado y libera el espacio
    public void cancelarTurno(int idTurno) throws MedMetException, TurnoInvalidoException {

        Turno turno = buscarTurnoxId(idTurno);

        if (turno == null) {
            throw new MedMetException("Turno seleccionado no existe");
        }

        String estado = turno.getEstado();

        if ("En curso".equalsIgnoreCase(estado) || "Finalizado".equalsIgnoreCase(estado)) {
            throw new TurnoInvalidoException("No es posible cancelar un turno finalizado o en curso");
        }

        turno.cancelarTurno();
    }

    public void confirmarLlegada(int idTurno) throws MedMetException {      // Registra la llegada del paciente a
                                                                            // la sala de espera
        Turno turno = buscarTurnoxId(idTurno);

        if (turno == null) {
            throw new MedMetException("Turno seleccionado no existe");
        }

        turno.confirmarTurno();
    }

    public List<Turno> listaCompletaTurnos() {      // Retorna todos los turnos agendados
        return new  ArrayList<>(bancoDatos.getTurnos());
    }
}
