package medmet.dao;

import medmet.conexion.ConexionBD;
import medmet.excepciones.MedMetException;
import medmet.modelo.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoriaClinicaDAO {

    private final PacienteDAO pacienteDao = new PacienteDAO();
    private final MedicoDAO medicoDao = new MedicoDAO();
    private final TurnoDAO turnoDao = new TurnoDAO();

    public HistoriaClinica crearHistoriaClinica(int idPaciente, String observacionesIniciales) throws MedMetException {

        String sql = "INSERT INTO historia_clinica (id_paciente, fecha_apertura, observaciones_iniciales) VALUES (?, CURDATE(), ?)";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, idPaciente);
            pst.setString(2, observacionesIniciales != null ? observacionesIniciales : "Sin observaciones registradas");

            int filas = pst.executeUpdate();

            if (filas > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int nuevoId = rs.getInt(1);
                        Paciente pac = pacienteDao.buscarPorId(nuevoId);
                        return new HistoriaClinica(idPaciente, pac, observacionesIniciales);
                    }
                }
            }
            throw new MedMetException("No se pudo obtener la Historia Clinica");

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al abrir la Historia Clínica: " + e.getMessage());
        }
    }

    public HistoriaClinica buscarPorIdPaciente(int idPaciente) throws MedMetException {

        String sql = "SELECT id_historia_clinica, id_paciente, fecha_apertura, observaciones_iniciales " +
                "FROM historia_clinica WHERE id_paciente = ?";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, idPaciente);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    HistoriaClinica hc = mapearHistoriaClinica(rs);
                    cargarEvoluciones(hc, conexion);

                    return hc;
                }
            }
            return null;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al buscar historia clinica: " + e.getMessage());
        }
    }

    public HistoriaClinica buscarPorDni(String dni) throws MedMetException {

        Paciente pac = pacienteDao.buscarPorDni(dni);

        if (pac == null) {
            return null;
        }
        return buscarPorIdPaciente(pac.getIdPaciente());
    }

    public List<HistoriaClinica> listarTodas() throws MedMetException {

        List<HistoriaClinica> lista = new ArrayList<>();

        String sql = "SELECT id_historia_clinica, id_paciente, fecha_apertura, observaciones_iniciales " +
                "FROM historia_clinica ORDER BY id_historia_clinica ASC";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                HistoriaClinica hc = mapearHistoriaClinica(rs);
                cargarEvoluciones(hc, conexion);
                lista.add(hc);
            }
            return lista;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al listar las historias clinicas: " + e.getMessage());
        }
    }

    public EvolucionMedica registrarEvolucion(int idHistoriaClinica, int idMedico, String motivo, String diagnostico,
                                              String tratamiento) throws MedMetException {

        String sql = "INSERT INTO evolucion_medica (id_historia_clinica, id_medico, fecha_hora, motivo_consulta, diagnostico, tratamiento) " +
                "VALUES (?, ?, NOW(), ?, ?, ?)";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, idHistoriaClinica);
            pst.setInt(2, idMedico);
            pst.setString(3, motivo != null ? motivo.trim() : "Consulta médica");
            pst.setString(4, diagnostico.trim());
            pst.setString(5, tratamiento != null ? tratamiento.trim() : "");

            int filas = pst.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        Medico med = medicoDao.buscarPorId(idMedico);
                        return new EvolucionMedica(rs.getInt(1), med, motivo, diagnostico, tratamiento);
                    }
                }
            }
            throw new MedMetException("No se pudo registrar la evolución médica");

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al registrar evolución médica: " + e.getMessage());
        }
    }

    public boolean finalizarAtencionTurno(int idTurno, String motivo, String diagnostico, String tratamiento) throws MedMetException {

        Turno turno  = turnoDao.buscarPorId(idTurno);

        if (turno == null) {
            throw new MedMetException("El Turno #" + idTurno + " no existe");
        }

        Connection conexion = null;

        try {
            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            int idPaciente = turno.getPaciente().getIdPaciente();
            int idHistoriaClinica = 0;

            String sqlCheckHC = "SELECT id_historia_clinica FROM historia_clinica WHERE id_paciente = ?";

            try (PreparedStatement pstCheck = conexion.prepareStatement(sqlCheckHC)) {
                pstCheck.setInt(1, idPaciente);
                try (ResultSet rs = pstCheck.executeQuery()) {
                    if (rs.next()) {
                        idHistoriaClinica = rs.getInt(1);
                    }
                }
            }

            if (idHistoriaClinica == 0) {

                String sqlInsertHC = "INSERT INTO historia_clinica (id_paciente, fecha_apertura, observaciones_iniciales) VALUES (?, CURDATE(), 'Apertura automática efectuada en consultorio.')";
                try (PreparedStatement pstHC = conexion.prepareStatement(sqlInsertHC, Statement.RETURN_GENERATED_KEYS)) {
                    pstHC.setInt(1, idPaciente);
                    pstHC.executeUpdate();
                    try (ResultSet rs = pstHC.getGeneratedKeys()) {
                        if (rs.next()) idHistoriaClinica = rs.getInt(1);
                    }
                }
            }

            String sqlInsertEv = "INSERT INTO evolucion_medica (id_historia_clinica, id_medico, fecha_hora, motivo_consulta, diagnostico, tratamiento) " +
                    "VALUES (?, ?, NOW(), ?, ?, ?)";
            try (PreparedStatement pstEv = conexion.prepareStatement(sqlInsertEv)) {
                pstEv.setInt(1, idHistoriaClinica);
                pstEv.setInt(2, turno.getMedico().getIdMedico());
                pstEv.setString(3, (motivo != null && !motivo.trim().isEmpty()) ? motivo.trim() : "Consulta médica general");
                pstEv.setString(4, diagnostico.trim());
                pstEv.setString(5, tratamiento != null ? tratamiento.trim() : "");
                pstEv.executeUpdate();
            }

            String sqlUpdateTurno = "UPDATE turno SET estado = 'Atendido' WHERE id_turno = ?";
            try (PreparedStatement pstT = conexion.prepareStatement(sqlUpdateTurno)) {
                pstT.setInt(1, idTurno);
                pstT.executeUpdate();
            }

            conexion.commit();
            return true;

        } catch (SQLException e) {
            if (conexion != null) {
                try { conexion.rollback(); } catch (SQLException ex) {}
            }
            throw new MedMetException("Error en transacción al finalizar atención: " + e.getMessage());

        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    private HistoriaClinica mapearHistoriaClinica(ResultSet rs) throws SQLException, MedMetException {

        int idPaciente = rs.getInt("id_paciente");
        Paciente pac = pacienteDao.buscarPorId(idPaciente);

        HistoriaClinica hc = new HistoriaClinica(rs.getInt("id_historia_clinica"), pac,
                rs.getString("observaciones_iniciales"));

        Date fechaA = rs.getDate("fecha_apertura");

        if (fechaA != null) hc.setFechaApertura(fechaA.toLocalDate());
        return hc;
    }

    private void cargarEvoluciones(HistoriaClinica hc, Connection conexion) throws SQLException {

        String sql = "SELECT em.id_evolucion, em.fecha_hora, em.motivo_consulta, em.diagnostico, em.tratamiento, " +
                "       m.id_medico, m.matricula_profesional, p.dni, p.nombre, p.apellido, " +
                "       e.id_especialidad, e.nombre AS esp_nombre, e.descripcion AS esp_desc " +
                "FROM evolucion_medica em " + "INNER JOIN medico m ON em.id_medico = m.id_medico " +
                "INNER JOIN persona p ON m.dni = p.dni " + "INNER JOIN especialidad e ON m.id_especialidad = e.id_especialidad " +
                "WHERE em.id_historia_clinica = ? ORDER BY em.fecha_hora DESC";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, hc.getIdHistoriaClinica());

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Especialidad esp = new Especialidad(rs.getInt("id_especialidad"), rs.getString("esp_nombre"),
                            rs.getString("esp_desc"));

                    Medico med = new Medico(rs.getInt("id_medico"), rs.getString("dni"), rs.getString("nombre"),
                            rs.getString("apellido"), "", "", "", rs.getString("matricula_profesional"), esp);

                    EvolucionMedica evo = new EvolucionMedica(rs.getInt("id_evolucion"), med, rs.getString("motivo_consulta"),
                            rs.getString("diagnostico"), rs.getString("tratamiento"));

                    Timestamp ts = rs.getTimestamp("fecha_hora");

                    if (ts != null) {
                        evo.setFechaHora(ts.toLocalDateTime());
                    }

                    hc.agregarEvolucion(evo);
                }
            }
        }
    }
}