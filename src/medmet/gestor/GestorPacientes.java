package medmet.gestor;

import medmet.dao.PacienteDAO;
import medmet.dao.TurnoDAO;
import medmet.excepciones.MedMetException;
import medmet.modelo.Paciente;
import medmet.modelo.Turno;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Gestor responsable de la administracion de Pacientes
public class GestorPacientes {

    private final PacienteDAO pacienteDao;
    private final TurnoDAO turnoDao;

    private static final String[] OBRAS_SOCIALES_DISPONIBLES = {
            "Particular", "OSDE", "INSSJP (PAMI)", "Swiss Medical", "Galeno", "Sancor Salud"
    };

    // Constructor
    public GestorPacientes() {
        this.pacienteDao = new PacienteDAO();
        this.turnoDao = new TurnoDAO();
    }

    public String[] getObrasSocialesDisponibles() {
        return OBRAS_SOCIALES_DISPONIBLES;
    }

    // Registra un nuevo paciente al sistema
    public Paciente registroPaciente(String dni, String nombre, String apellido, String telefono, String email, String direccion,
                                    LocalDate fechaNacimiento, String obraSocial) throws MedMetException {

        if (dni == null || dni.trim().isEmpty() || nombre == null || nombre.trim().isEmpty() || apellido == null ||
                apellido.trim().isEmpty() || fechaNacimiento == null) {

            throw new MedMetException("Todos los campos obligatorios deben ser completados");
        }

        String dniLimpio = dni.trim();

        if (pacienteDao.buscarPorDni(dniLimpio) != null) {      // Control de DNI Duplicado
            throw new MedMetException("El DNI existe en el sistema");
        }

        Paciente nuevo = new Paciente(0, dniLimpio, nombre.trim(), apellido.trim(), telefono != null ? telefono.trim() : "",
                email != null ? email.trim() : "", direccion != null ? direccion.trim() : "", fechaNacimiento,
                (obraSocial != null && !obraSocial.trim().isEmpty()) ? obraSocial.trim() : "Particular");

        boolean exito = pacienteDao.registrarPaciente(nuevo);

        if (!exito) {
            throw new MedMetException("No se pudo registrar el paciente en la base de datos");
        }

        return nuevo;
    }

    // Consulta los pacientes aplicando filtros de busqueda
    public List<Paciente> buscarPaciente(String filtro) {

        if (filtro == null || filtro.trim().isEmpty()) {

            return ordenarPacientesAlbeticamente();
        }

        List<Paciente> resultado = new ArrayList<>();
        String criterio = filtro.trim().toLowerCase();

        for (Paciente p : listarPacientesActivos()) {

            boolean coincideDni = p.getDni() != null && p.getDni().toLowerCase().contains(criterio);
            boolean coincideApellido = p.getNombreCompleto() != null && p.getNombreCompleto().toLowerCase().contains(criterio);
            boolean coincideObraSocial = p.getObraSocial() != null && p.getObraSocial().toLowerCase().contains(criterio);

            if (coincideDni || coincideApellido || coincideObraSocial) {
                resultado.add(p);
            }
        }

        return resultado;
    }

    public List<Paciente> ordenarPacientesAlbeticamente() {     // Ordena los pacientes en orden alfabetico

        List<Paciente> lista = listarPacientesActivos();

        lista.sort(Comparator.comparing(p -> p.getNombreCompleto() != null ? p.getNombreCompleto() : "", String.CASE_INSENSITIVE_ORDER));

        return lista;
    }

    // Modifica los datos de un paciente ya registrado
    public void actDatosPaciente(int idPaciente, String nombre, String apellido, String telefono, String email, String direccion,
                                    LocalDate fechaNacimiento, String obraSocial) throws MedMetException {

        if (nombre == null || nombre.trim().isEmpty() || apellido == null || apellido.trim().isEmpty() || fechaNacimiento == null) {
            throw new MedMetException("Formato de datos no válido");
        }

        Paciente paciente = pacienteDao.buscarPorId(idPaciente);

        if (paciente == null) {
            throw new MedMetException("El paciente seleccionado no existe");
        }

        paciente.setNombre(nombre.trim());
        paciente.setApellido(apellido.trim());
        paciente.setTelefono(telefono != null ? telefono.trim() : "");
        paciente.setEmail(email != null ? email.trim() : "");
        paciente.setDireccion(direccion != null ? direccion.trim() : "");
        paciente.setFechaNacimiento(fechaNacimiento);
        paciente.setObraSocial((obraSocial != null && !obraSocial.trim().isEmpty()) ? obraSocial.trim() : "Particular");

        boolean exito = pacienteDao.modificarPaciente(paciente);

        if (!exito) {
            throw new MedMetException("No se pudo actualizar los datos del paciente en la base de datos");
        }
    }

    public boolean verificarTurnosPaciente(int idPaciente) {
        try {
            List<Turno> turnos = turnoDao.buscarTurnos(null, null, null, null);
            for (Turno t : turnos) {
                if (t.getPaciente() != null && t.getPaciente().getIdPaciente() == idPaciente) {
                    String estado = t.getEstado();
                    if ("Pendiente".equalsIgnoreCase(estado) || "En espera".equalsIgnoreCase(estado) || "En curso".equalsIgnoreCase(estado)) {
                        return true;
                    }
                }
            }
        } catch (MedMetException e) {
            System.err.println("Aviso al verificar turnos: " + e.getMessage());
        }
        return false;
    }

    public void procesarBajaPaciente(int idPaciente) throws MedMetException {       // Da de baja lógica un paciente del sistema

        Paciente pac = pacienteDao.buscarPorId(idPaciente);

        if (pac == null) {
            throw new MedMetException("El paciente no existe");
        }

        if (verificarTurnosPaciente(idPaciente)) {
            throw new MedMetException("No se puede eliminar: el paciente tiene turnos activos");
        }

        boolean exito = pacienteDao.darDeBaja(idPaciente);

        if (!exito) {
            throw new MedMetException("No se pudo dar de baja al paciente en la base de datos");
        }
    }

    public List<Paciente> listarPacientesActivos() {        // Retorna todos los pacientes activos

        try {
            return pacienteDao.listarActivos();

        } catch (MedMetException e) {
            System.err.println("Activo al listar pacientes: " + e.getMessage());

            return new ArrayList<>();
        }
    }

    public boolean registrarPaciente(Paciente paciente) {
        try {
            return pacienteDao.registrarPaciente(paciente);
        } catch (MedMetException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    public boolean modificarPaciente(Paciente paciente) {
        try {
            return pacienteDao.modificarPaciente(paciente);
        } catch (MedMetException e) {
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }
}
