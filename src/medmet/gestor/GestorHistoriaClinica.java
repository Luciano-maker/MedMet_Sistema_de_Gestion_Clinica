package medmet.gestor;

import medmet.excepciones.MedMetException;
import medmet.modelo.*;

import java.util.ArrayList;
import java.util.List;

// // Gestor responsable de la administracion de Historias Clinicas
public class GestorHistoriaClinica {

    private final BancoDatos bancoDatos;

    // Constructor
    public GestorHistoriaClinica() {
        this.bancoDatos = BancoDatos.getInstancia();
    }

    // Da de alta una historia clinica a un paciente
    public HistoriaClinica crearHistoriaClinica(int idPaciente, String observacionesIniciales) throws MedMetException {

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

        // Valida duplicidad
        if (buscarHistoriaClinicaxPaciente(idPaciente) != null) {
            throw new MedMetException("Paciente ya posee con una Historia Clinica");
        }

        int nuevoId = bancoDatos.generarIdHistoriaClinica();

        String obs = (observacionesIniciales != null) ? observacionesIniciales.trim() : "Sin observaciones registradas al ingreso";
        HistoriaClinica nuevaHC = new HistoriaClinica(nuevoId, paciente, obs);

        bancoDatos.getHistoriasClinicas().add(nuevaHC);
        return nuevaHC;
    }

    public HistoriaClinica buscarHistoriaClinicaxPaciente(int idPaciente) {     // Localiza la historia clinica mediante
                                                                                // ID del paciente
        for (HistoriaClinica hc : bancoDatos.getHistoriasClinicas()) {
            if (hc.getPaciente() != null && hc.getPaciente().getIdPaciente() == idPaciente) {
                return hc;
            }
        }
        return null;
    }

    public HistoriaClinica buscarHistoriaClinicaxDni(String dni) {      // Localiza la historia clinica mediante DNI
                                                                        // del paciente
        if (dni == null) return null;

        for (HistoriaClinica hc : bancoDatos.getHistoriasClinicas()) {
            if (hc.getPaciente() != null && hc.getPaciente().getDni().equalsIgnoreCase(dni.trim())) {
                return hc;
            }
        }

        return null;
    }

    public List<HistoriaClinica> buscarHistoriasClinicas(String criterio) {     // Filtra historias clinicas

        List<HistoriaClinica> resultado = new ArrayList<>();
        if (criterio == null || criterio.trim().isEmpty()) {
            return new ArrayList<>(bancoDatos.getHistoriasClinicas());
        }

        String flitro = criterio.trim().toLowerCase();

        for (HistoriaClinica hc : bancoDatos.getHistoriasClinicas()) {
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

        if (diagnostico == null || diagnostico.trim().isEmpty() || tratamiento == null || tratamiento.trim().isEmpty()) {
            throw new MedMetException("Ingresar diagnostico y tratamiento");
        }

        // Busca el medico responsable
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

        HistoriaClinica hc = buscarHistoriaClinicaxPaciente(idPaciente);        // Localiza o crea una historia clinica

        if (hc == null) {
            hc = crearHistoriaClinica(idPaciente, "Apertura automática efectuada en consultorio");
        }

        int nuevoIdEv = bancoDatos.generarIdEvolucion();

        String motivo = (motivoConsulta != null &&  !motivoConsulta.trim().isEmpty()) ? motivoConsulta.trim() : "Consulta médica general";

        EvolucionMedica nuevaEvolucion = new EvolucionMedica(nuevoIdEv, medico, motivo, diagnostico.trim(), tratamiento.trim());

        hc.agregarEvolucion(nuevaEvolucion);
        return nuevaEvolucion;
    }

    // Cierra el circuito de atencion desde la pantalla de medico
    public void finalizarAtencionTurno(int IdTurno, String motivoConsulta, String diagnostico, String tratamiento) throws MedMetException {

        Turno turno = null;

        for (Turno t: bancoDatos.getTurnos()) {
            if (t.getIdTurno() == IdTurno) {
                turno = t;
                break;
            }
        }

        if (turno == null) {
            throw new MedMetException("Turno seleccionado no existe");
        }

        // Asienta la consulta en la historia clinica
        registrarEvolucionMedica(turno.getPaciente().getIdPaciente(), turno.getMedico().getIdMedico(), motivoConsulta, diagnostico, tratamiento);

        turno.setEstado("Finalizado");  // Actualiza el estado del turno a Finalizado
    }

    public List<HistoriaClinica> listarTodasLasHistoriasClinicas() {        // Retorna todas las historias clinicas registradas
        return new ArrayList<>(bancoDatos.getHistoriasClinicas());
    }

}
