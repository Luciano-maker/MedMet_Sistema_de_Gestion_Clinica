package medmet.gestor;

import medmet.dao.EspecialidadDAO;
import medmet.excepciones.MedMetException;
import medmet.modelo.Especialidad;
import medmet.modelo.Medico;
import medmet.modelo.Turno;

import java.util.ArrayList;
import java.util.List;

// Gestor responsable de la administracion de las Especialidades
public class GestorEspecialidades {

    private final EspecialidadDAO especialidadDao;

    // Constructor
    public GestorEspecialidades() {
        this.especialidadDao = new EspecialidadDAO();
    }

    // Registra una nueva especialidad al sistema
    public Especialidad registrarEspecialidad(String nombre, String descripcion) throws MedMetException {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new MedMetException("El campo nombre es obligatorio");
        }

        if (especialidadDao.buscarPorNombre(nombre.trim()) != null) {
            throw new MedMetException("Nombre de especialidad inválido o duplicado");
        }

        Especialidad especialidad = new Especialidad(0, nombre.trim(), descripcion != null ? descripcion.trim() : "");
        boolean exito = especialidadDao.registrarEspecialidad(especialidad);

        if (!exito) {
            throw new MedMetException("No se pudo registrar la especialidad en la base de datos");
        }

        return especialidad;
    }

    public List<Especialidad> buscarEspecialidades(String filtro) {         // Consulta y filtra especialidades segun
                                                                            // un criterio especifico
        if (filtro == null || filtro.trim().isEmpty()) {
            return listarEspecialidadesActivas();
        }

        List<Especialidad> resultado = new ArrayList<>();
        String creiterio = filtro.trim().toLowerCase();

        for (Especialidad e : listarEspecialidadesActivas()) {
            if (e.getNombre().toLowerCase().contains(creiterio) || e.getDescripcion() != null && !e.getDescripcion().toLowerCase().contains(creiterio)) {
                resultado.add(e);
            }
        }

        return resultado;
    }

    // Modifica los datos de una especialidad que ya existe
    public void actulizarEspecialidad(int idEspecialidad, String nuevoNombre, String nuevaDescripcion) throws MedMetException {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new MedMetException("El campo nombre es obligatorio");
        }

        Especialidad existente = especialidadDao.buscarPorId(idEspecialidad);

        if (existente == null) {
            throw new MedMetException("Especialidad seleccionada no existe");
        }

        existente.setNombre(nuevoNombre.trim());
        existente.setDescripcion(nuevaDescripcion != null ? nuevaDescripcion.trim() : "");

        boolean exito = especialidadDao.registrarEspecialidad(existente);

        if (!exito) {
            throw new MedMetException("No se pudieron guardar los cambios de la especialidad");
        }
    }

    public void eliminarEspecialidad(int idEspecialidad) throws MedMetException {       // Realiza una baja logica de una
                                                                                        // especialidad medica
        if (especialidadDao.tieneMedicosActivos(idEspecialidad)) {
            throw new MedMetException("No es posible dar de baja: Existen profesionales asociados");
        }

        boolean exito = especialidadDao.darDeBaja(idEspecialidad);

        if (!exito) {
            throw new MedMetException("No fue posible dar de baja la especialidad");
        }
    }

    public List<Especialidad> listarEspecialidadesActivas() {       // Retorna todas las especialidades activas

        try {
            return especialidadDao.listarActivas();

        } catch (MedMetException e) {
            System.err.println("Aviso al listar especialidade: " + e.getMessage());

            return new ArrayList<>();
        }
    }
}
