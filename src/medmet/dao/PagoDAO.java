package medmet.dao;

import medmet.conexion.ConexionBD;
import medmet.excepciones.MedMetException;
import medmet.modelo.Pago;
import medmet.modelo.Turno;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    private TurnoDAO turnoDao = new TurnoDAO();

    public boolean procesarPago(int idTurno, double monto, String metodoPago) throws MedMetException {

        String sqlPago = "INSERT INTO pago (id_turno, fecha_hora, monto, metodo_pago) VALUES (?, NOW(), ?, ?)";
        String sqlTurno = "UPDATE turno SET estado_pago = 'PAGADO' WHERE id_turno = ?";

        Connection conexion = null;
        try {
            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            try (PreparedStatement pstPago = conexion.prepareStatement(sqlPago);
                 PreparedStatement pstTurno = conexion.prepareStatement(sqlTurno)) {

                pstPago.setInt(1, idTurno);
                pstPago.setDouble(2, monto);
                pstPago.setString(3, metodoPago);
                pstPago.executeUpdate();

                pstTurno.setInt(1, idTurno);
                pstTurno.executeUpdate();

                conexion.commit();
                return true;

            } catch (SQLException ex) {
                if (conexion != null) {
                    conexion.rollback();
                }
                throw new MedMetException("Error en la transacción al registrar el pago: " + ex.getMessage());
            }

        } catch (SQLException e) {
            throw new MedMetException("Error al conectarse con la base de datos: " + e.getMessage());
        } finally {
            ConexionBD.cerrarConexion(conexion);
        }
    }
}
