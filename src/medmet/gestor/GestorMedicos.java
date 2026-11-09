package medmet.gestor;

import medmet.dao.MedicoDAO;
import medmet.excepciones.MedMetException;
import medmet.modelo.Especialidad;
import medmet.modelo.Medico;
import medmet.modelo.Turno;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Gestor responsable de la administracion de Medicos
public class GestorMedicos {

    private final MedicoDAO medicoDao;

    // Constructor
    public GestorMedicos() {
        this.medicoDao = new MedicoDAO();
    }

    // Registra un nuevo Medico al sistema
    public Medico registrarMedico(String dni, String nombre, String apellido, String telefono, String email,
                                  String direccion, String matricula, Especialidad especialidad) throws MedMetException {

        if (dni == null || dni.trim().isEmpty() || nombre == null || nombre.trim().isEmpty() ||
                apellido == null || apellido.trim().isEmpty() || matricula == null || matricula.trim().isEmpty() ||
                especialidad == null) {

            throw new MedMetException("Todos los cammpos son abligatorios");
        }

        String dniLimpio = dni.trim();
        String matriculaLimpia = matricula.trim();

        // Valida si el DNI está duplicado
        if (medicoDao.buscarPorDni(dniLimpio) != null) {
            throw new MedMetException("Ya existe un médico registrado con el DNI: "  + dniLimpio);
        }

        if (medicoDao.buscarPorMatricula(matriculaLimpia) != null) {
            throw new MedMetException("Datos ingresados no validos");
        }

        Medico nuevo = new Medico(0, dniLimpio, nombre.trim(), apellido.trim(), telefono != null ? telefono.trim() : "",
                email != null ? email.trim() : "", direccion != null ? direccion.trim() : "", matriculaLimpia, especialidad);

        boolean exito = medicoDao.registrarMedico(nuevo);

        if (!exito) {
            throw new MedMetException("No se pudo registrar el profesional");
        }

        return nuevo;
    }

    // Consulta los medicos aplicando filtros de busqueda
    public List<Medico> buscarMedicos(String filtro) {

        if (filtro == null || filtro.trim().isEmpty()) {
            return medicosOrdenadosAlfabeticamente();
        }

        List<Medico> resultado = new ArrayList<>();
        String criterio = filtro.trim().toLowerCase();

        for (Medico med : listarMedicosActivos()) {

            boolean coincideDni = med.getDni() != null && med.getDni().toLowerCase().contains(criterio);
            boolean coincideMat = med.getMatriculaProfesional() != null && med.getMatriculaProfesional().toLowerCase().contains(criterio);
            boolean coincideNombre = med.getNombreCompleto() != null && med.getNombreCompleto().toLowerCase().contains(criterio);
            boolean coincideEsp = med.getEspecialidad() != null && med.getEspecialidad().getNombre().toLowerCase().contains(criterio);

            if (coincideDni || coincideMat || coincideNombre || coincideEsp) {
                resultado.add(med);
            }
        }

        return resultado;
    }

    // Modifica los datos de un medico ya registrado
    public void modificarMedico(int idMedico, String nombre, String apellido,
                                String telefono, String email, String direccion,
                                String matricula, Especialidad especialidad) throws MedMetException {

        if (nombre == null || nombre.trim().isEmpty() || apellido == null || apellido.trim().isEmpty() ||
                matricula == null || matricula.trim().isEmpty() || especialidad == null) {

            throw new MedMetException("Los campos son obligatorios");
        }

        Medico existente =  medicoDao.buscarPorId(idMedico);

        if (existente == null) {
            throw new MedMetException("El médico seleccionado no existe");
        }

        existente.setNombre(nombre.trim());
        existente.setApellido(apellido.trim());
        existente.setTelefono(telefono != null ? telefono.trim() : "");
        existente.setEmail(email != null ? email.trim() : "");
        existente.setDireccion(direccion != null ? direccion.trim() : "");
        existente.setMatriculaProfesional(matricula.trim());
        existente.setEspecialidad(especialidad);

        boolean exito = medicoDao.modificarMedico(existente);

        if (!exito) {
            throw new MedMetException("No se pudieron modificar los datos del Médico");
        }
    }

    public void procesarBajaMedico(int idMedico) throws MedMetException {       // Da de baja lógica un medico del sistema
                                                                                // en caso de no tener turnos agendados
        Medico med =   medicoDao.buscarPorId(idMedico);

        if (med == null) {
            throw new MedMetException("El Médico seleccionado no existe");
        }

        if (medicoDao.tieneTurnosActivos(idMedico)) {
            throw new MedMetException("No se puede dar de baja: el Médico seleecinado posee turnos agendados");
        }

        boolean exito = medicoDao.darDeBaja(idMedico);

        if (!exito) {
            throw new MedMetException("No se pudo de dar de baja al Médico seleccionado");
        }
    }

    public List<Medico> listarxEspecialidad(int idEspecialidad) {       // Retorna todos los medicos activos de una
                                                                        // especialidad especifica
        try {
            return medicoDao.listarPorEspecialidad(idEspecialidad);

        } catch (MedMetException e) {
            System.err.println("Aviso al listar Médicos por especialidad: " + e.getMessage());

            return new ArrayList<>();
        }
    }

    public List<Medico> listarMedicosActivos() {        // Retorna todos los medicos activos

        try {
            return medicoDao.listarActivos();

        } catch (MedMetException e) {
            System.err.println("Aviso al listar Médicos: " + e.getMessage());

            return new  ArrayList<>();
        }
    }

    public List<Medico> medicosOrdenadosAlfabeticamente() {

        List<Medico> lista = new ArrayList<>();
        lista.sort(Comparator.comparing(m -> m.getNombreCompleto() != null ? m.getNombreCompleto() : "",
                String.CASE_INSENSITIVE_ORDER));

        return lista;
    }

}
