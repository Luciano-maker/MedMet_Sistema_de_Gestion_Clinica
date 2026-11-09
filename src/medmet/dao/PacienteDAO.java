package medmet.dao;

import medmet.conexion.ConexionBD;
import medmet.excepciones.MedMetException;
import medmet.modelo.Paciente;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    private static final String SELECT_BASE = "SELECT pac.id_paciente, pac.dni, per.nombre, per.apellido, per.telefono, " +
            "       per.email, per.direccion, pac.fecha_nacimiento, pac.obra_social, pac.activo " + "FROM paciente pac " +
            "INNER JOIN persona per ON pac.dni = per.dni ";

    public boolean registrarPaciente(Paciente paciente) throws MedMetException {

        String sqlPersona = "INSERT INTO persona (dni, nombre, apellido, telefono, email, direccion) " +
                "VALUES (?, ?, ?, ?, ?, ?) " + "ON DUPLICATE KEY UPDATE nombre=?, apellido=?, telefono=?, email=?, direccion=?";

        String sqlPaciente = "INSERT INTO paciente (dni, fecha_nacimiento, obra_social, activo) " + "VALUES (?, ?, ?, TRUE)";

        Connection conexion = null;

        try {
            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            try (PreparedStatement pstPer = conexion.prepareStatement(sqlPersona)) {

                pstPer.setString(1, paciente.getDni().trim());
                pstPer.setString(2, paciente.getNombre().trim());
                pstPer.setString(3, paciente.getApellido().trim());
                pstPer.setString(4, paciente.getTelefono() != null ? paciente.getTelefono().trim() : "");
                pstPer.setString(5, paciente.getEmail()!= null ? paciente.getEmail().trim() : "");
                pstPer.setString(6, paciente.getDireccion()!= null ? paciente.getDireccion().trim() : "");

                pstPer.setString(7, paciente.getNombre().trim());
                pstPer.setString(8, paciente.getApellido().trim());
                pstPer.setString(9, paciente.getTelefono() != null ? paciente.getTelefono().trim() : "");
                pstPer.setString(10, paciente.getEmail()!= null ? paciente.getEmail().trim() : "");
                pstPer.setString(11, paciente.getDireccion()!= null ? paciente.getDireccion().trim() : "");
                pstPer.executeUpdate();
            }

            try (PreparedStatement pstPac = conexion.prepareStatement(sqlPaciente,  Statement.RETURN_GENERATED_KEYS)) {

                pstPac.setString(1,paciente.getDni().trim());
                pstPac.setDate(2, Date.valueOf(paciente.getFechaNacimiento() != null ?
                        paciente.getFechaNacimiento() : LocalDate.of(1990, 1, 1)));
                pstPac.setString(3, paciente.getObraSocial() != null ? paciente.getObraSocial().trim() : "Particular");
                pstPac.executeUpdate();

                try (ResultSet rs = pstPac.getGeneratedKeys()){
                    if (rs.next()) {
                        paciente.setIdPaciente(rs.getInt(1));
                    }
                }
            }

            conexion.commit();
            return true;

        } catch (SQLException e) {
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error en rollback: " + rollbackEx.getMessage());
                }
            }
            throw new MedMetException("Error SQL al registrar paciente: " + e.getMessage());

        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    public boolean modificarPaciente(Paciente paciente) throws MedMetException {

        String sqlPersona = "UPDATE persona SET nombre=?, apellido=?, telefono=?, email=?, direccion=? WHERE dni=?";
        String sqlPaciente = "UPDATE paciente SET fecha_nacimiento=?, obra_social=? WHERE id_paciente=?";

        Connection conexion = null;

        try {
            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            try (PreparedStatement pstPer = conexion.prepareStatement(sqlPersona)){

                pstPer.setString(1, paciente.getNombre().trim());
                pstPer.setString(2, paciente.getApellido().trim());
                pstPer.setString(3, paciente.getTelefono() != null ? paciente.getTelefono().trim() : "");
                pstPer.setString(4, paciente.getEmail() != null ? paciente.getEmail().trim() : "");
                pstPer.setString(5, paciente.getDireccion() != null ? paciente.getDireccion().trim() : "");
                pstPer.setString(6, paciente.getDni().trim());
                pstPer.executeUpdate();
            }

            try (PreparedStatement pstPac = conexion.prepareStatement(sqlPaciente)){

                pstPac.setDate(1, Date.valueOf(paciente.getFechaNacimiento() != null ? paciente.getFechaNacimiento(): LocalDate.of(1990, 1, 1)));
                pstPac.setString(2, paciente.getObraSocial() != null ? paciente.getObraSocial().trim() : "Particular");
                pstPac.setInt(3, paciente.getIdPaciente());
                pstPac.executeUpdate();
            }

            conexion.commit();
            return true;

        } catch (SQLException e) {
            if (conexion != null) {
                try {
                    conexion.rollback();

                } catch (SQLException rollbackEx) {}
            }
            throw new MedMetException("Error SQL al modificar paciente: " + e.getMessage());

        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }

    public boolean darDeBaja(int idPaciente) throws MedMetException {

        String sql = "UPDATE paciente SET activo = FALSE WHERE id_paciente = ?";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, idPaciente);

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al dar de baja el paciente: " + e.getMessage());
        }
    }

    public List<Paciente> listarActivos() throws MedMetException {

        List<Paciente> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE pac.activo = TRUE ORDER BY per.apellido ASC, per.nombre ASC";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPaciente(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al listar pacientes: " + e.getMessage());
        }
    }

    public Paciente buscarPorDni(String dni) throws MedMetException {

        String sql = SELECT_BASE + "WHERE pac.dni = ? AND pac.activo = TRUE";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)){

            pst.setString(1, dni.trim());

            try (ResultSet rs = pst.executeQuery()){
                if (rs.next()) {
                    return mapearPaciente(rs);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al buscar paciente por DNI: " + e.getMessage());
        }
    }

    public Paciente buscarPorId(int idPaciente) throws MedMetException {

        String sql = SELECT_BASE + "WHERE pac.id_paciente = ?";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, idPaciente);

            try (ResultSet rs = pst.executeQuery()){
                if (rs.next()) {
                    return mapearPaciente(rs);
                }
            }
            return null;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al buscar paciente por ID: " + e.getMessage());
        }
    }

    private Paciente mapearPaciente(ResultSet rs) throws SQLException {

        LocalDate fechaNacimiento = rs.getDate("fecha_nacimiento") != null ?
                rs.getDate("fecha_nacimiento").toLocalDate() : LocalDate.of(1990, 1, 1);

        Paciente paciente = new Paciente(rs.getInt("id_paciente"), rs.getString("dni"),
                rs.getString("nombre"), rs.getString("apellido"), rs.getString("telefono"),
                rs.getString("email"), rs.getString("direccion"), fechaNacimiento, rs.getString("obra_social"));

        paciente.setActivo(rs.getBoolean("activo"));

        return paciente;
    }

}
