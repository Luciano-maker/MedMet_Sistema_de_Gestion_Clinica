package medmet.gestor;

import medmet.excepciones.MedMetException;
import medmet.modelo.Especialidad;
import medmet.modelo.Medico;
import medmet.modelo.Turno;

import java.util.ArrayList;
import java.util.List;

// Gestor responsable de la administracion de Medicos
public class GestorMedicos {

    private final BancoDatos bancoDatos;

    // Constructor
    public GestorMedicos() {
        this.bancoDatos = BancoDatos.getInstancia();
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
        if (buscarMedicoxDni(dniLimpio) != null) {
            throw new MedMetException("Ya existe un médico registrado con el DNI: "  + dniLimpio);
        }

        if (buscarMedicoxMatricula(matriculaLimpia) != null) {
            throw new MedMetException("Datos ingresados no validos");
        }

        int nuevoId = bancoDatos.generarIdMedico();
        Medico nuevo = new Medico( nuevoId, dniLimpio, nombre.trim(), apellido.trim(),
                telefono != null ? telefono.trim() : "", email != null ? email.trim() : "",
                direccion != null ? direccion.trim() : "", matriculaLimpia, especialidad);

        bancoDatos.getMedicos().add(nuevo);
        return nuevo;
    }

    // Consulta los medicos aplicando filtros de busqueda
    public List<Medico> buscarMedicos(String filtro) {

        List<Medico> resultado = new ArrayList<>();

        if (filtro == null || filtro.trim().isEmpty()) {
            for (Medico m : bancoDatos.getMedicos()) {
                if (m.isActivo()) {
                    resultado.add(m);
                }
            }
            return resultado;
        }

        String creiterio = filtro.trim().toLowerCase();

        for (Medico m : bancoDatos.getMedicos()) {
            if (m.isActivo()) {

                boolean coincideDni = m.getDni().toLowerCase().contains(creiterio);
                boolean coincideApellido = m.getApellido().toLowerCase().contains(creiterio);
                boolean coincideMatricula = m.getMatriculaProfesional().toLowerCase().contains(creiterio);
                boolean coincideEspecialidad =  m.getEspecialidad() != null && m.getEspecialidad().getNombre().toLowerCase().contains(creiterio);

                if (coincideDni || coincideApellido || coincideMatricula || coincideEspecialidad) {
                    resultado.add(m);
                }
            }
        }
        return resultado;
    }

    public Medico buscarMedicoxId(int idMedico) {       // Localiza un medico por su ID

        for (Medico m : bancoDatos.getMedicos()) {
            if (m.getIdMedico() == idMedico) {
                return m;
            }
        }
        return null;
    }

    public Medico buscarMedicoxDni(String dni) {        // Localiza un medico por su DNI

        if (dni == null) return null;

        for (Medico m : bancoDatos.getMedicos()) {
            if (m.getDni().equalsIgnoreCase(dni.trim())) {
                return m;
            }
        }
        return null;
    }

    public Medico buscarMedicoxMatricula(String matricula) {        // Localiza un medico por su matricula profesional

        if (matricula == null) return null;

        for (Medico m : bancoDatos.getMedicos()) {
            if (m.getMatriculaProfesional().equalsIgnoreCase(matricula.trim())) {
                return m;
            }
        }
        return null;
    }

    // Modifica los datos de un medico ya registrado
    public void modificarMedico(int idMedico, String nombre, String apellido, String telefono, String email, String direccion,
                                String matricula, Especialidad especialidad) throws MedMetException {

        if (nombre == null || nombre.trim().isEmpty() || apellido == null || apellido.trim().isEmpty() ||
                matricula == null || matricula.trim().isEmpty() || especialidad == null) {

            throw new MedMetException("Los campos son obligatorios");
        }

        Medico med =  buscarMedicoxId(idMedico);
        if (med == null) {
            throw new MedMetException("El médico seleccionado no existe");
        }

        String matriculaLimpia = matricula.trim();
        for (Medico otro : bancoDatos.getMedicos()) {

            if (otro.getIdMedico() == idMedico && otro.getMatriculaProfesional().equalsIgnoreCase(matriculaLimpia)) {
                throw new MedMetException("La matricula profesional ya se encuentra registrada");
            }
        }

        med.setNombre(nombre.trim());
        med.setApellido(apellido.trim());
        med.setTelefono(telefono != null ? telefono.trim() : "");
        med.setEmail(email != null ? email.trim() : "");
        med.setDireccion(direccion != null ? direccion.trim() : "");
        med.setMatriculaProfesional(matriculaLimpia);
        med.setEspecialidad(especialidad);
    }

    public boolean verificarTurnosActivos(int idMedico) {       // Realiza la verificacion si un medico tiene turnos agendados
        for (Turno t : bancoDatos.getTurnos()) {

            if (t.getMedico() != null && t.getMedico().getIdMedico() == idMedico) {
                String estado = t.getEstado();

                if ("Pendiente".equalsIgnoreCase(estado) || "En espera".equalsIgnoreCase(estado) ||
                        "En curso".equalsIgnoreCase(estado)) {

                    return true;
                }
            }
        }
        return false;
    }

    public void procesarBajaMedico(int idMedico) throws MedMetException {       // Da de baja lógica un medico del sistema
        Medico med =   buscarMedicoxId(idMedico);                               // en caso de no tener turnos agendados
        if (med == null) {
            throw new MedMetException("El medico seleccionado no existe");
        }

        if (verificarTurnosActivos(idMedico)) {
            throw new MedMetException("No se puede dar de baja: el medico seleecinado posee turnos agendados");
        }

        med.setActivo(false);       // Realiza la baja lógica del sistema
    }

    public List<Medico> listarxEspecialidad(int idEspecialidad) {       // Retorna todos los medicos activos de una
                                                                        // especialidad especifica
        List<Medico> filtrados = new ArrayList<>();

        for (Medico m : bancoDatos.getMedicos()) {
            if (m.isActivo() && m.getEspecialidad() != null && m.getEspecialidad().getIdEspecialidad() == idEspecialidad) {
                filtrados.add(m);
            }
        }

        return filtrados;
    }

    public List<Medico> listarMedicosActivos() {        // Retorna todos los medicos activos

        List<Medico> activos = new ArrayList<>();

        for (Medico m : bancoDatos.getMedicos()) {
            if (m.isActivo()) {
                activos.add(m);
            }
        }

        if (activos.isEmpty() && !bancoDatos.getMedicos().isEmpty()) {
            for (Medico m : bancoDatos.getMedicos()) {
                m.setActivo(true);
                activos.add(m);
            }
        }

        return activos;
    }

}
