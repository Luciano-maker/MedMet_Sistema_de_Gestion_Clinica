package medmet.gestor;

import medmet.excepciones.MedMetException;
import medmet.modelo.Paciente;
import medmet.modelo.Turno;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Gestor responsable de la administracion de Pacientes
public class GestorPacientes {

    private final BancoDatos bancoDatos;

    // Constructor
    public GestorPacientes() {
        this.bancoDatos = BancoDatos.getInstancia();
    }

    // Registra un nuevo paciente al sistema
    public Paciente registrarPaciente(String dni, String nombre, String apellido, String telefono, String email,
                                      String direccion, LocalDate fechaNacimiento, String obraSocial) throws MedMetException {

        if (dni == null || dni.trim().isEmpty() || nombre == null || nombre.trim().isEmpty() ||
                apellido == null || apellido.trim().isEmpty() || fechaNacimiento == null) {

            throw new MedMetException("Todos los campos son obligatorios");
        }

        String dniLimpio = dni.trim();

        // Valida si el DNI está duplicado
        if (buscarPacientexDni(dniLimpio) != null) {
            throw new MedMetException("DNI ingresado no valido");
        }

        int nuevoId = bancoDatos.generarIdPaciente();

        Paciente nuevo = new Paciente(nuevoId, dniLimpio, nombre.trim(), apellido.trim(), telefono != null ? telefono.trim() : "",
                email != null ? email.trim() : "", direccion != null ? direccion.trim() : "", fechaNacimiento,
                obraSocial != null && !obraSocial.trim().isEmpty() ? obraSocial.trim() : "Particular");

        bancoDatos.getPacientes().add(nuevo);
        return nuevo;
    }

    // Consulta los pacientes aplicando filtros de busqueda
    public List<Paciente> buscarPaciente(String filtro) {

        if (filtro == null || filtro.trim().isEmpty()) {

            return ordenarPacientesAlbeticamente();
        }

        List<Paciente> resultado = new ArrayList<>();
        String criterio = filtro.trim().toLowerCase();

        for (Paciente p : bancoDatos.getPacientes()) {

            boolean coincideDni = p.getDni().toLowerCase().contains(criterio);
            boolean coincideApellido = p.getApellido().toLowerCase().contains(criterio);
            boolean coincideObraSocial = p.getObraSocial().toLowerCase().contains(criterio);

            if (coincideDni || coincideApellido || coincideObraSocial) {
                resultado.add(p);
            }
        }

        return resultado;
    }

    public Paciente buscarPacientexDni(String dni) {        // Localiza un paciente por su DNI

        if (dni == null) return null;

        for (Paciente p : bancoDatos.getPacientes()) {
            if (p.getDni().equalsIgnoreCase(dni.trim())) {
                return p;
            }
        }

        return null;
    }

    public Paciente buscarPacientexId(int idPaciente) {     // Localiza un paciente por su ID
        for (Paciente p : bancoDatos.getPacientes()) {
            if (p.getIdPaciente() == idPaciente) {
                return p;
            }
        }

        return null;
    }

    public List<Paciente> ordenarPacientesAlbeticamente() {     // Ordena los pacientes en orden alfabetico

        List<Paciente> lista = listarPacientesActivos();

        lista.sort(Comparator.comparing(p -> p.getNombreCompleto() != null ? p.getNombreCompleto() : "", String.CASE_INSENSITIVE_ORDER));

        return lista;
    }

    // Modifica los datos de un paciente ya registrado
    public void actualiuzarPaciente(int idPaciente, String nombre, String apellido, String telefono, String email,
                                    String direccion, LocalDate fechaNacimiento, String obraSocial) throws MedMetException {

        if (nombre == null || nombre.trim().isEmpty() || apellido == null || apellido.trim().isEmpty() || fechaNacimiento == null) {
            throw new MedMetException("Formato de datos no valido");
        }

        Paciente paciente = buscarPacientexId(idPaciente);

        if (paciente == null) {
            throw new MedMetException("Paciente seleccionado no existe");
        }

        paciente.setNombre(nombre.trim());
        paciente.setApellido(apellido.trim());
        paciente.setTelefono(telefono != null ? telefono.trim() : "");
        paciente.setEmail(email != null ? email.trim() : "");
        paciente.setDireccion(direccion != null ? direccion.trim() : "");
        paciente.setFechaNacimiento(fechaNacimiento);
        paciente.setObraSocial(obraSocial != null && !obraSocial.trim().isEmpty() ? obraSocial.trim() : "Particular");
    }

    public boolean verificarTurnosPaciente(int idPaciente) {        // Verifica si un paciente tiene turnos asociados
        for (Turno t : bancoDatos.getTurnos()) {
            if (t.getPaciente() != null && t.getPaciente().getIdPaciente() == idPaciente) {

                String estado = t.getEstado();

                if ("Pendiente".equalsIgnoreCase(estado) || "En espera".equalsIgnoreCase(estado) || "En curso".equalsIgnoreCase(estado)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void procesarBajaPaciente(int idPaciente) throws MedMetException {       // Da de baja lógica un paciente del sistema
                                                                                    // en caso de no tener turnos asociados
        Paciente pac =  buscarPacientexId(idPaciente);

        if (pac == null) {
            throw new MedMetException("Paciente seleccionado no existe");
        }

        if (verificarTurnosPaciente(idPaciente)) {
            throw new MedMetException("No se puede eliminar: el paciente seleccionado posee turnos asociados");
        }

        pac.setActivo(false); //Realiza la baja lógica
    }

    public List<Paciente> listarPacientesActivos() {        // Retorna todos los pacientes activos

        List<Paciente> activos = new ArrayList<>();

        for (Paciente p : bancoDatos.getPacientes()) {
            if (p.isActivo()) {
                activos.add(p);
            }
        }

        if (activos.isEmpty() && !bancoDatos.getPacientes().isEmpty()) {
            for (Paciente p : bancoDatos.getPacientes()) {
                p.setActivo(true);
                activos.add(p);
            }
        }

        return activos;
    }
}
