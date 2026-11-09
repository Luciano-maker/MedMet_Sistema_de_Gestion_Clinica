package medmet.gestor;

import medmet.dao.HistoriaClinicaDAO;
import medmet.dao.PacienteDAO;
import medmet.excepciones.MedMetException;
import medmet.modelo.*;

import java.util.ArrayList;
import java.util.List;

// // Gestor responsable de la administracion de Historias Clinicas
public class GestorHistoriaClinica {

    private final HistoriaClinicaDAO historiaClinicaDao;
    private final PacienteDAO pacienteDao;

    // Constructor
    public GestorHistoriaClinica() {
        this.historiaClinicaDao = new  HistoriaClinicaDAO();
        this.pacienteDao = new  PacienteDAO();
    }

    // Da de alta una historia clinica a un paciente
    public HistoriaClinica crearHistoriaClinica(int idPaciente, String observacionesIniciales) throws MedMetException {

        Paciente paciente = pacienteDao.buscarPorId(idPaciente);

        if (paciente == null) {
            throw new MedMetException("Paciente seleccionado no existe");
        }

        // Valida duplicidad
        if (historiaClinicaDao.buscarPorIdPaciente(idPaciente) != null) {
            throw new MedMetException("Paciente ya posee con una Historia Clinica");
        }

        return historiaClinicaDao.crearHistoriaClinica(idPaciente, observacionesIniciales);
    }

    public HistoriaClinica buscarHistoriaClinicaxPaciente(int idPaciente) {     // Localiza la historia clinica mediante
                                                                                // ID del paciente
        try {
            return historiaClinicaDao.buscarPorIdPaciente(idPaciente);

        } catch (MedMetException e) {
            return null;
        }
    }

    public HistoriaClinica buscarHistoriaClinicaxDni(String dni) {      // Localiza la historia clinica mediante DNI
                                                                        // del paciente
        try {
            return historiaClinicaDao.buscarPorDni(dni);

        } catch (MedMetException e) {
            return null;
        }
    }

    public List<HistoriaClinica> buscarHistoriasClinicas(String criterio) {     // Filtra historias clinicas

        if (criterio == null || criterio.trim().isEmpty()) {
            return listarTodasLasHistoriasClinicas();
        }

        List<HistoriaClinica> resultado = new ArrayList<>();
        String flitro = criterio.trim().toLowerCase();

        for (HistoriaClinica hc : listarTodasLasHistoriasClinicas()) {
            Paciente pac = hc.getPaciente();
            if (pac != null) {
                boolean coincideDni = pac.getDni().toLowerCase().contains(flitro);
                boolean coincideApellido = pac.getApellido().toLowerCase().contains(flitro);

                if (coincideDni || coincideApellido) {
                    resultado.add(hc);
                }
            }
        }

        return resultado;
    }

    // Registra una nueva evolucion medica dentro una historia clinica
    public EvolucionMedica registrarEvolucionMedica(int idPaciente, int idMedico, String motivoConsulta, String diagnostico,
                                                    String tratamiento) throws MedMetException {

        if (diagnostico == null || diagnostico.trim().isEmpty()) {
            throw new MedMetException("Ingresar diagnostico y/u observaciones");
        }

        HistoriaClinica hc = historiaClinicaDao.buscarPorIdPaciente(idPaciente);

        if (hc == null) {
            hc = historiaClinicaDao.crearHistoriaClinica(idPaciente, "Apertura atomática efectuada en consultprio");
        }

        return historiaClinicaDao.registrarEvolucion(hc.getIdHistoriaClinica(), idMedico, motivoConsulta, diagnostico, tratamiento);
    }

    // Cierra el circuito de atencion desde la pantalla de medico
    public void finalizarAtencionTurno(int IdTurno, String motivoConsulta, String diagnostico, String tratamiento) throws MedMetException {

        if (diagnostico == null || diagnostico.trim().isEmpty()) {
            throw new MedMetException("Ingresar diagnóstico y/u observaciones");
        }

        boolean exito = historiaClinicaDao.finalizarAtencionTurno(IdTurno, motivoConsulta, diagnostico, tratamiento);

        if (!exito) {
            throw new MedMetException("No se pudo finalizar la atencin médica");
        }
    }

    public List<HistoriaClinica> listarTodasLasHistoriasClinicas() {        // Retorna todas las historias clinicas registradas

        try {
            return historiaClinicaDao.listarTodas();

        } catch (MedMetException e) {
            System.err.println("Aciso al listar Historias Clinicas" + e.getMessage());
            return new ArrayList<>();
        }
    }

}
