package medmet.dao;

import medmet.conexion.ConexionBD;
import medmet.excepciones.MedMetException;
import medmet.modelo.Especialidad;
import medmet.modelo.Medico;
import medmet.modelo.Paciente;
import medmet.modelo.Turno;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TurnoDAO {

    private static final String SELECT_BASE = "SELECT t.id_turno, t.fecha, t.hora, t.estado AS estado_turno, t.estado_pago, " +
            "       pac.id_paciente, per_pac.dni AS dni_pac, per_pac.nombre AS nom_pac, per_pac.apellido AS ape_pac, " +
            "       per_pac.telefono AS tel_pac, per_pac.email AS email_pac, per_pac.direccion AS dir_pac, " + "       pac.fecha_nacimiento, pac.obra_social, " +
            "       med.id_medico, med.matricula_profesional, per_med.dni AS dni_med, per_med.nombre AS nom_med, " +
            "       per_med.apellido AS ape_med, per_med.telefono AS tel_med, per_med.email AS email_med, " +
            "       per_med.direccion AS dir_med, " + "       esp.id_especialidad, esp.nombre AS nom_esp, esp.descripcion AS desc_esp " +
            "FROM turno t " + "INNER JOIN paciente pac ON t.id_paciente = pac.id_paciente " + "INNER JOIN persona per_pac ON pac.dni = per_pac.dni " +
            "INNER JOIN medico med ON t.id_medico = med.id_medico " + "INNER JOIN persona per_med ON med.dni = per_med.dni " +
            "INNER JOIN especialidad esp ON med.id_especialidad = esp.id_especialidad ";

    public List<Turno> buscarTurnos(LocalDate fecha, Integer idMedico, Integer idEspecialidad, String criterioPaciente) throws MedMetException {

        List<Turno> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_BASE).append("WHERE 1=1 ");
        List<Object> parametros = new ArrayList<>();

        if (fecha != null) {
            sql.append("AND t.fecha = ? ");
            parametros.add(Date.valueOf(fecha));
        }

        if (idMedico != null) {
            sql.append("AND t.id_medico = ? ");
            parametros.add(idMedico);
        }

        if (idEspecialidad != null) {
            sql.append("AND med.id_especialidad = ? ");
            parametros.add(idEspecialidad);
        }

        if (criterioPaciente != null && !criterioPaciente.trim().isEmpty()) {
            sql.append("AND (LOWER(per_pac.dni) LIKE ? OK LOWER(per_pac.nombre) LIKE ? OR LOWER(per_pac.apellido) LIKE ?) ");
            String patron = "%" +  criterioPaciente.trim().toLowerCase() + "%";
            parametros.add(patron);
            parametros.add(patron);
            parametros.add(patron);
        }

        sql.append("ORDER BY t.hora ASC");

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                pst.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearTurno(rs));
                }
            }
            return lista;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al consultar la agenda de turnos: " + e.getMessage());
        }
    }

    public List<Turno> listarPorFecha(LocalDate fecha) throws MedMetException {
        return buscarTurnos(fecha, null, null, null);
    }

    public boolean registrarTurno(int idPaciente, int idMedico, LocalDate fecha, LocalTime hora) throws MedMetException {

        String sql = "INSERT INTO turno (id_paciente, id_medico, fecha, hora, estado, estado_pago) " + "VALUES (?, ?, ?, ?, 'Pendiente', 'Pendiente')";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)){

            pst.setInt(1, idPaciente);
            pst.setInt(2, idMedico);
            pst.setDate(3, Date.valueOf(fecha));
            pst.setTime(4, Time.valueOf(hora));

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al registrar turno: " + e.getMessage());
        }
    }

    public boolean actualizarEstado(int idTurno, String nuevoEstado)  throws MedMetException {

        String sql = "UPDATE turno SET estado = ? WHERE id_turno = ?";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setString(1, nuevoEstado);
            pst.setInt(2, idTurno);

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al actualizar el estado del turno: " + e.getMessage());
        }
    }

    public boolean actualizarEstadoPago(int idTurno, String nuevoEstadoPago)  throws MedMetException {

        String sql = "UPDATE turno SET estado_pago = ? WHERE id_turno = ?";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setString(1,  nuevoEstadoPago);
            pst.setInt(2, idTurno);

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al actualizar el pago del turno: " + e.getMessage());
        }
    }

    public boolean verificarSolapamiento(int idMedico, LocalDate fecha, LocalTime hora) throws MedMetException {

        String sql = "SELECT COUNT(*) FROM turno WHERE id_medico = ? AND fecha = ? AND hora = ? AND estado != 'Cancelado'";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, idMedico);
            pst.setDate(2, Date.valueOf(fecha));
            pst.setTime(3, Time.valueOf(hora));

            try (ResultSet rs = pst.executeQuery()){
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al validar disponibilidad de agenda: " + e.getMessage());
        }
    }

    /*
    public Turno buscarPorId(int idTurno) throws MedMetException {

        String sql = SELECT_BASE + "WHERE t.id_turno = ?";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, idTurno);

            try (ResultSet rs = pst.executeQuery()){
                if (rs.next()) {
                    return mapearTurno(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al consultar el turno #" + idTurno + ": " + e.getMessage());
        }
    }

     */

    public boolean reprogramarTurno(int idTurno, LocalDate nuevaFecha, LocalTime nuevaHora) throws MedMetException {

        String sql = "UPDATE turno SET fecha = ?, hora = ?, estado = 'Pendiente' WHERE id_turno = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setDate(1, Date.valueOf(nuevaFecha));
            pst.setTime(2, Time.valueOf(nuevaHora));
            pst.setInt(3, idTurno);

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al reprogramar turno #" + idTurno + ": " + e.getMessage());
        }
    }

    public boolean solapamientoReprogramacion(int idMedico, LocalDate fecha, LocalTime hora, int idTurnoExcluir) throws MedMetException {

        String sql = "SELECT COUNT(*) FROM turno WHERE id_medico = ? AND fecha = ? AND hora = ? AND id_turno != ? AND estado != 'Cancelado'";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idMedico);
            pst.setDate(2, Date.valueOf(fecha));
            pst.setTime(3, Time.valueOf(hora));
            pst.setInt(4, idTurnoExcluir);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new MedMetException("Error SQL al verificar disponibilidad horaria " + e.getMessage());
        }
    }

    public Turno buscarPorId(int idTurno) throws MedMetException {
        String sql = "SELECT t.id_turno, t.fecha, t.hora, t.estado AS estado_turno, t.estado_pago, " +
                "       pac.id_paciente, per_pac.dni AS dni_pac, per_pac.nombre AS nom_pac, per_pac.apellido AS ape_pac, " +
                "       pac.obra_social, " +
                "       med.id_medico, med.matricula_profesional, per_med.dni AS dni_med, per_med.nombre AS nom_med, per_med.apellido AS ape_med, " +
                "       esp.id_especialidad, esp.nombre AS nom_esp " +
                "FROM turno t " +
                "INNER JOIN paciente pac ON t.id_paciente = pac.id_paciente " +
                "INNER JOIN persona per_pac ON pac.dni = per_pac.dni " +
                "INNER JOIN medico med ON t.id_medico = med.id_medico " +
                "INNER JOIN persona per_med ON med.dni = per_med.dni " +
                "INNER JOIN especialidad esp ON med.id_especialidad = esp.id_especialidad " +
                "WHERE t.id_turno = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, idTurno);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapearTurno(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al consultar el turno #" + idTurno + ": " + e.getMessage());
        }
    }

    private Turno mapearTurno(ResultSet rs) throws SQLException {
        // 1. Especialidad
        Especialidad esp = new Especialidad(
                rs.getInt("id_especialidad"),
                rs.getString("nom_esp"),
                ""
        );

        // 2. Médico
        Medico med = new Medico(
                rs.getInt("id_medico"),
                rs.getString("dni_med"),
                rs.getString("nom_med"),
                rs.getString("ape_med"),
                "", "", "",
                rs.getString("matricula_profesional"),
                esp
        );

        // 3. Paciente
        Paciente pac = new Paciente(
                rs.getInt("id_paciente"),
                rs.getString("dni_pac"),
                rs.getString("nom_pac"),
                rs.getString("ape_pac"),
                "", "", "",
                LocalDate.of(1990, 1, 1),
                rs.getString("obra_social")
        );

        // 4. Turno
        Turno turno = new Turno(
                rs.getInt("id_turno"),
                pac,
                med,
                rs.getDate("fecha").toLocalDate(),
                rs.getTime("hora").toLocalTime()
        );
        turno.setEstado(rs.getString("estado_turno"));
        turno.setEstadoPago(rs.getString("estado_pago"));

        return turno;
    }




    /*
    public Turno mapearTurno(ResultSet rs) throws SQLException {

        Especialidad esp = new Especialidad(rs.getInt("id_especialidad"), rs.getString("nom_esp"), rs.getString("desc_esp"));

        Medico med = new Medico(rs.getInt("id_medico"), rs.getString("dni_med"), rs.getString("nom_med"),
                rs.getString("ape_med"), rs.getString("tel_med"), rs.getString("email_med"),
                rs.getString("dir_med"), rs.getString("matricula_profesional"), esp);

        LocalDate fechaNacimiento = rs.getDate("fecha_nacimiento") != null ?
                rs.getDate("fecha_nacimiento").toLocalDate() : LocalDate.of(1990, 1, 1);

        Paciente pac = new Paciente(rs.getInt("id_paciente"), rs.getString("dni_pac"), rs.getString("nom_pac"),
                rs.getString("ape_pac"), rs.getString("tel_pac"), rs.getString("email_pac"),
                rs.getString("dir_pac"), fechaNacimiento, rs.getString("obra_social"));

        Turno turno = new Turno(rs.getInt("id_turno"), pac, med, rs.getDate("fecha").toLocalDate(),
                rs.getTime("hora").toLocalTime());

        turno.setEstado(rs.getString("estado_turno"));
        turno.setEstadoPago(rs.getString("estado_pago"));

        return turno;
    }
     */
}
