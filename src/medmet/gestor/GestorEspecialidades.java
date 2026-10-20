package medmet.gestor;

import medmet.excepciones.MedMetException;
import medmet.modelo.Especialidad;
import medmet.modelo.Medico;
import medmet.modelo.Turno;

import java.util.ArrayList;
import java.util.List;

// Gestor responsable de la administracion de las Especialidades
public class GestorEspecialidades {

    private final BancoDatos bancoDatos;

    // Constructor
    public GestorEspecialidades() {
        this.bancoDatos = BancoDatos.getInstancia();
    }

    // Registra una nueva especialidad al sistema
    public Especialidad registrarEspecialidad(String nombre, String descripcion) throws MedMetException {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new MedMetException("El campo nombre es obligatorio");
        }

        String nombreLimpio = nombre.trim();

        for (Especialidad esp : bancoDatos.getEspecialidades()) {
            if (esp.getNombre().equalsIgnoreCase(nombreLimpio)) {
                throw new MedMetException("El nombre ingresado no es válido");
            }
        }

        int nuevoId = bancoDatos.generarIdEspecialidad();
        String descripcionLimpia = (descripcion == null) ? descripcion.trim() : "";
        Especialidad nueva = new Especialidad(nuevoId, nombreLimpio, descripcionLimpia);
        bancoDatos.getEspecialidades().add(nueva);
        return nueva;
    }

    public List<Especialidad> buscarEspecialidades(String filtro) {         // Consulta y filtra especialidades segun
                                                                            // un criterio especifico
        List<Especialidad> resultado = new ArrayList<>();
        if (filtro == null || filtro.trim().isEmpty()) {
            for (Especialidad e :  bancoDatos.getEspecialidades()) {
                if (e.isActivo()) {
                    resultado.add(e);
                }
            }

            return resultado;
        }

        String creiterio = filtro.trim().toLowerCase();

        for (Especialidad e : bancoDatos.getEspecialidades()) {
            if (e.isActivo()) {

                boolean coincideNombre = e.getNombre().toLowerCase().contains(creiterio);

                if (coincideNombre) {
                    resultado.add(e);
                }
            }
        }

        return resultado;
    }

    public Especialidad buscarEspecialidadxId(int idEspecialidad) {     // Busca una especialidad por su ID
        for (Especialidad e :  bancoDatos.getEspecialidades()) {
            if (e.getIdEspecialidad() == idEspecialidad) {
                return e;
            }
        }
        return null;
    }

    // Modifica los datos de una especialidad que ya existe
    public void actulizarEspecialidad(int idEspecialidad, String nuevoNombre, String nuevaDescripcion) throws MedMetException {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new MedMetException("El campo nombre es obligatorio");
        }

        Especialidad esp = buscarEspecialidadxId(idEspecialidad);
        if (esp == null) {
            throw new MedMetException("Especialidad seleccionada no existe");
        }

        String nombreLimpio = nuevoNombre.trim();
        for (Especialidad otra : bancoDatos.getEspecialidades()) {
            if (otra.getIdEspecialidad() != idEspecialidad && otra.getNombre().equalsIgnoreCase(nombreLimpio)) {
                throw new MedMetException("Ya existe una especialidad con ese nombre.");
            }
        }

        esp.setNombre(nombreLimpio);
        esp.setDescripcion(nuevaDescripcion != null ? nuevaDescripcion.trim() : "");
    }

    public boolean verificarTurnosActivosEspecialidad(int idEspecialidad) {     // Verifica si existen turnos asociados
        for (Turno t : bancoDatos.getTurnos()) {                                // a medicos de esa especialidad
            Medico med = t.getMedico();
            if (med != null && med.getEspecialidad() != null && med.getEspecialidad().getIdEspecialidad() == idEspecialidad) {

                String estado = t.getEstado();
                if ("Pendiente".equalsIgnoreCase(estado) || "En espera".equalsIgnoreCase(estado) || "En curso".equalsIgnoreCase(estado)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void eliminarEspecialidad(int idEspecialidad) throws MedMetException {       // Realiza una baja logica de una
        Especialidad esp =  buscarEspecialidadxId(idEspecialidad);                      // especialidad medica
        if (esp == null) {
            throw new MedMetException("Especialidad seleccionada no existe");
        }

        if (verificarTurnosActivosEspecialidad(idEspecialidad)) {
            throw new MedMetException("Nose puede eliminar: Existen turnos asociados.");
        }

        esp.setActivo(false);       // Baja lógica
    }

    public List<Especialidad> listarEspecialidadesActivas() {       // Retorna todas las especialidades activas
        List<Especialidad> activas = new ArrayList<>();
        for (Especialidad esp : bancoDatos.getEspecialidades()) {
            if (esp.isActivo()) {
                activas.add(esp);
            }
        }
        return activas;
    }
}
