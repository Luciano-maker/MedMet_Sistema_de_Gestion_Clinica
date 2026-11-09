package medmet.dao;

import medmet.conexion.ConexionBD;
import medmet.excepciones.MedMetException;
import medmet.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario autenticar(String nombreUsuario, String contrasenia, String rol) throws MedMetException {

        String sql = "SELECT u.id_usuario, u.nombre_usuario, u.rol, u.activo, " +
                "       COALESCE(CONCAT(pm.nombre, ' ', pm.apellido), CONCAT(pr.nombre, ' ', pr.apellido), u.nombre_usuario) AS nombre_completo, " +
                "       COALESCE(m.id_medico, r.id_recepcionista, 0) AS id_referencia " + "FROM usuario u " +
                "LEFT JOIN medico m ON u.id_usuario = m.id_usuario " + "LEFT JOIN persona pm ON m.dni = pm.dni " +
                "LEFT JOIN recepcionista r ON u.id_usuario = r.id_usuario " + "LEFT JOIN persona pr ON r.dni = pr.dni " +
                "WHERE u.nombre_usuario = ? AND u.contrasenia = ? AND u.rol = ? AND u.activo = TRUE";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)){

            pst.setString(1, nombreUsuario.trim());
            pst.setString(2, contrasenia.trim());
            pst.setString(3, rol.trim().toUpperCase());

            try (ResultSet rs = pst.executeQuery()){
                if (rs.next()) {
                    Usuario usuario = new Usuario(rs.getInt("id_usuario"), rs.getString("nombre_usuario"),
                            null, rs.getString("rol"), rs.getBoolean("activo"));

                    usuario.setNombreVisible(rs.getString("nombre_completo"));
                    usuario.setIdReferencia(rs.getInt("id_referencia"));
                    return usuario;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new MedMetException("Error al autenticars en la base de datos: " + e.getMessage());
        }
    }

    public Usuario buscarPorId(int idUsuario) throws MedMetException {

        String sql = "SELECT id_usuario, nombre_usuario, rol, activo FROM usuario WHERE id_usuario = ?";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, idUsuario);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getInt("id_usuario"), rs.getString("nombre_usuario"),
                    null, rs.getString("rol"), rs.getBoolean("activo"));
                }
            }
            return null;
        } catch (SQLException e) {
            throw new MedMetException("Error al buscar el usuario: " + e.getMessage());
        }
    }
}
