package medmet.dao;

import medmet.conexion.ConexionBD;
import medmet.excepciones.MedMetException;
import medmet.modelo.Especialidad;
import medmet.modelo.Medico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicoDAO {

    private static final String SELECT_BASE = "SELECT m.id_medico, m.matricula_profesional, m.activo AS medico_activo, " +
            "       per.dni, per.nombre, per.apellido, per.telefono, per.email, per.direccion, " +
            "       esp.id_especialidad, esp.nombre AS nom_esp, esp.descripcion AS desc_esp " +
            "FROM medico m " + "INNER JOIN persona per ON m.dni = per.dni " +
            "INNER JOIN especialidad esp ON m.id_especialidad = esp.id_especialidad ";

    public boolean registrarMedico(Medico medico) throws MedMetException {

        String sqlPersona = "INSERT INTO persona (dni, nombre, apellido, telefono, email, direccion) " + "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE nombre=?, apellido=?, telefono=?, email=?, direccion=?";

        String sqlUsuario = "INSERT INTO usuario (nombre_usuario, contrasenia, rol, activo) " + "VALUES (?, 'Doc2026*', 'MEDICO', TRUE)";

        String sqlMedico = "INSERT INTO medico (dni, matricula_profesional, id_especialidad, id_usuario, activo) " +
                "VALUES (?, ?, ?, ?, TRUE)";

        Connection conexion = null;

        try {
            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            try (PreparedStatement pstPer = conexion.prepareStatement(sqlPersona)){

                pstPer.setString(1, medico.getDni().trim());
                pstPer.setString(2, medico.getNombre().trim());
                pstPer.setString(3, medico.getApellido().trim());
                pstPer.setString(4, medico.getTelefono() != null ? medico.getTelefono().trim() : "");
                pstPer.setString(5, medico.getEmail() != null ? medico.getEmail().trim() : "");
                pstPer.setString(6, medico.getDireccion() != null ? medico.getDireccion().trim() : "");

                pstPer.setString(7, medico.getNombre().trim());
                pstPer.setString(8, medico.getApellido().trim());
                pstPer.setString(9, medico.getTelefono() != null ? medico.getTelefono().trim() : "");
                pstPer.setString(10, medico.getEmail() != null ? medico.getEmail().trim() : "");
                pstPer.setString(11, medico.getDireccion() != null ? medico.getDireccion().trim() : "");
                pstPer.executeUpdate();
            }

            int idUsuarioGenerado = 0;

            String usuarioLogin = "dr_" + medico.getApellido().toLowerCase().replaceAll("[^a-z0-9]", "") +
                    "_" + medico.getDni().substring(Math.max(0, medico.getDni().length() - 3));

            try (PreparedStatement pstUser = conexion.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)){

                pstUser.setString(1, usuarioLogin);
                pstUser.executeUpdate();

                try (ResultSet rs = pstUser.getGeneratedKeys()){
                    if (rs.next()) {
                        idUsuarioGenerado = rs.getInt(1);
                    }
                }
            }

            try (PreparedStatement pstMed = conexion.prepareStatement(sqlMedico, Statement.RETURN_GENERATED_KEYS)){

                pstMed.setString(1, medico.getDni().trim());
                pstMed.setString(2, medico.getMatriculaProfesional().trim());
                pstMed.setInt(3, medico.getEspecialidad().getIdEspecialidad());
                pstMed.setInt(4, idUsuarioGenerado);
                pstMed.executeUpdate();

                try (ResultSet rs = pstMed.getGeneratedKeys()){
                    if (rs.next()) {
                        medico.setIdMedico(rs.getInt(1));
                    }
                }
            }
            conexion.commit();
            return true;

        } catch (SQLException e) {
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {}
            }
            throw new MedMetException("Error SQL al registrar Médico: " + e.getMessage());

        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    public boolean modificarMedico(Medico medico) throws MedMetException {

        String sqlPersona = "UPDATE persona SET nombre=?, apellido=?, telefono=?, email=?, direccion=? WHERE dni=?";
        String sqlMedico = "UPDATE medico SET matricula_profesional=?, id_especialidad=? WHERE id_medico=?";

        Connection conexion = null;

        try {
            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            try (PreparedStatement pstPer = conexion.prepareStatement(sqlPersona)){

                pstPer.setString(1, medico.getNombre().trim());
                pstPer.setString(2, medico.getApellido().trim());
                pstPer.setString(3, medico.getTelefono() != null ? medico.getTelefono().trim() : "");
                pstPer.setString(4, medico.getEmail() != null ? medico.getEmail().trim() : "");
                pstPer.setString(5, medico.getDireccion() != null ? medico.getDireccion().trim() : "");
                pstPer.setString(6, medico.getDni().trim());
                pstPer.executeUpdate();
            }

            try (PreparedStatement pstMed = conexion.prepareStatement(sqlMedico)){

                pstMed.setString(1, medico.getMatriculaProfesional().trim());
                pstMed.setInt(2, medico.getEspecialidad().getIdEspecialidad());
                pstMed.setInt(3, medico.getIdMedico());
                pstMed.executeUpdate();
            }

            conexion.commit();
            return true;

        } catch (SQLException e) {
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {}
            }
            throw new MedMetException("Error SQL al actualizar Médico: " + e.getMessage());

        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    public boolean darDeBaja(int idMedico) throws MedMetException {

        String sql = "UPDATE medico SET activo = FALSE WHERE id_medico = ?";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)){

            pst.setInt(1, idMedico);

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al dar de baja el Médico: " +  e.getMessage());
        }
    }

    public List<Medico> listarActivos() throws MedMetException {

        List<Medico> lista = new ArrayList<>();

        String sql = SELECT_BASE + "WHERE m.activo = TRUE ORDER BY per.apellido ASC, per.nombre ASC";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()){

            while (rs.next()) {
                lista.add(mapearMedico(rs));
            }
            return lista;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al listar Médicos activos: " + e.getMessage());
        }
    }

    public List<Medico> listarPorEspecialidad(int idEspecialidad) throws MedMetException {

        List<Medico> lista = new ArrayList<>();

        String sql = SELECT_BASE + "WHERE m.id_especialidad = ? AND m.activo = TRUE ORDER BY per.apellido ASC";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, idEspecialidad);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearMedico(rs));
                }
            }
            return lista;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al lisar Médicos por especialidad: " + e.getMessage());
        }
    }

    public Medico buscarPorId(int idMedico) throws MedMetException {

        String sql = SELECT_BASE + "WHERE m.id_medico = ?";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, idMedico);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapearMedico(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al buscar Médico por ID: " + e.getMessage());
        }
    }

    public Medico buscarPorDni(String dni) throws MedMetException {

        String sql = SELECT_BASE + "WHERE m.dni = ? AND m.activo = TRUE";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setString(1, dni.trim());

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapearMedico(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al buscar Médico por DNI: " + e.getMessage());
        }
    }

    public Medico buscarPorMatricula(String matricula) throws MedMetException {

        String sql = SELECT_BASE + "WHERE LOWER(m.matricula_profesional) = ? AND m.activo = TRUE";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setString(1, matricula.trim().toLowerCase());

            try (ResultSet rs = pst.executeQuery()){
                if (rs.next()) {
                    return mapearMedico(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al buscar Médico por matricula: " + e.getMessage());
        }
    }

    public boolean tieneTurnosActivos(int idMedico) throws MedMetException {

        String sql = "SELECT COUNT(*) FROM turno WHERE id_medico = ? AND estado IN ('Pendiente', 'En espera', 'En curso')";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idMedico);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al validar turnos del médico: " + e.getMessage());
        }
    }

    private Medico mapearMedico(ResultSet rs) throws SQLException {

        Especialidad especialidad = new Especialidad(rs.getInt("id_especialidad"), rs.getString("nom_esp"),
                rs.getString("desc_esp"));

        Medico medico = new Medico(rs.getInt("id_medico"), rs.getString("dni"), rs.getString("nombre"),
                rs.getString("apellido"), rs.getString("telefono"), rs.getString("email"),
                rs.getString("direccion"), rs.getString("matricula_profesional"), especialidad);

        medico.setActivo(rs.getBoolean("medico_activo"));
        return medico;
    }
}
