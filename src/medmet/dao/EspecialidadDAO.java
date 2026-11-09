package medmet.dao;

import medmet.conexion.ConexionBD;
import medmet.excepciones.MedMetException;
import medmet.modelo.Especialidad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EspecialidadDAO {

    public boolean registrarEspecialidad(Especialidad especialidad) throws MedMetException {

        String sql = "INSERT INTO especialidad (nombre, descripcion, activo) VALUES (?, ?, TRUE)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement pst = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, especialidad.getNombre().trim());
            pst.setString(2, especialidad.getDescripcion() != null ? especialidad.getDescripcion().trim() : "");

            int filas = pst.executeUpdate();

            if (filas > 0) {
                try (ResultSet rs = pst.executeQuery()){
                    if (rs.next()) {
                        especialidad.setIdEspecialidad(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al resitrar especilidad: " + e.getMessage());
        }
    }

    public boolean modificarEspecialidad(Especialidad especialidad) throws MedMetException {

        String sql = "UPDATE especialidad SET nombre = ?, descripcion = ? WHERE id_especialidad = ?";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)){

            pst.setString(1, especialidad.getNombre().trim());
            pst.setString(2, especialidad.getDescripcion() != null ? especialidad.getDescripcion().trim() : "");
            pst.setInt(3, especialidad.getIdEspecialidad());

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al modificar especialidad: " + e.getMessage());
        }
    }

    public boolean darDeBaja(int idEspecialidad) throws MedMetException {

        String sql = "UPDATE especialidad SET activo = FALSE WHERE id_especialidad = ?";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, idEspecialidad);

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al dar de baja la especialidad: " + e.getMessage());
        }
    }

    public List<Especialidad> listarActivas() throws MedMetException {

        List<Especialidad> lista = new ArrayList<>();

        String sql = "SELECT id_especialidad, nombre, descripcion, activo FROM especialidad WHERE activo = TRUE ORDER BY nombre ASC";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()){

            while (rs.next()) {
                Especialidad especialidad = new Especialidad(rs.getInt("id_especialidad"), rs.getString("nombre"),
                        rs.getString("descripcion"));

                especialidad.setActivo(rs.getBoolean("activo"));
                lista.add(especialidad);
            }
            return lista;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al listar especialidades activas: " + e.getMessage());
        }
    }

    public Especialidad buscarPorId(int idEspecialidad) throws MedMetException {

        String sql = "SELECT id_especialidad, nombre, descripcion, activo FROM especialidad WHERE id_especialidad = ?";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, idEspecialidad);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Especialidad especialidad = new Especialidad(rs.getInt("id_especialidad"),
                            rs.getString("nombre"), rs.getString("descripcion"));

                    return especialidad;
                }
            }
            return null;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al buscar especialidad por ID: " + e.getMessage());
        }
    }

    public Especialidad buscarPorNombre(String nombre) throws MedMetException {

        String sql = "SELECT id_especialidad, nombre, descripcion, activo FROM especialidad WHERE LOWER(nombre) = ? AND activo = TRUE";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setString(1, nombre.trim().toLowerCase());

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Especialidad especialidad = new Especialidad(rs.getInt("id_especialidad"),
                            rs.getString("nombre"), rs.getString("descripcion"));

                    especialidad.setActivo(rs.getBoolean("activo"));

                    return especialidad;
                }
            }
            return null;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al buscar especialidad por nombre: " + e.getMessage());
        }
    }

    public boolean tieneMedicosActivos(int idEspecialidad) throws MedMetException {

        String sql = "SELECT COUNT(*) FROM medico WHERE id_especialidad = ? AND activo = TRUE";

        try (Connection conexion = ConexionBD.obtenerConexion(); PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, idEspecialidad);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;

        } catch (SQLException e) {
            throw new MedMetException("Error SQL al validar médicos de la especialidad: " + e.getMessage());
        }

    }
}
