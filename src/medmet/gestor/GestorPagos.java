package medmet.gestor;

import medmet.dao.PagoDAO;
import medmet.dao.TurnoDAO;
import medmet.excepciones.MedMetException;
import medmet.modelo.Pago;
import medmet.modelo.Turno;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Gestor responsable de la administracion de Pagos
public class GestorPagos {

    private final PagoDAO pagoDao;
    private final TurnoDAO turnoDao;

    // Constructor
    public GestorPagos() {
        this.pagoDao = new PagoDAO();
        this.turnoDao = new TurnoDAO();
    }

    // Registra el pago de un turno agendado
    public boolean registrarPago(int idTurno, double monto, String metodoPago) throws MedMetException {

        Turno turno = turnoDao.buscarPorId(idTurno);
        if (turno == null) {
            throw new MedMetException("Turno no encontrado en el sistema.");
        }

        if ("Pagado".equalsIgnoreCase(turno.getEstadoPago())) {
            throw new MedMetException("El turno ya se encuentra pagado.");
        }

        if (monto <= 0) {
            throw new MedMetException("El monto a cobrar debe ser un valor positivo mayor a cero.");
        }

        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new MedMetException("Debe seleccionar un método de pago válido.");
        }

        return pagoDao.procesarPago(idTurno, monto, metodoPago.trim());
    }
}
