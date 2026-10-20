package medmet.gestor;

import medmet.excepciones.MedMetException;
import medmet.modelo.Pago;
import medmet.modelo.Turno;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Gestor responsable de la administracion de Pagos
public class GestorPagos {

    private final BancoDatos bancoDatos;

    // Constructor
    public GestorPagos() {
        this.bancoDatos = BancoDatos.getInstancia();
    }

    // Registra el pago de un turno agendado
    public Pago registrarPago(int idTurno, double monto, String metodoPago) throws MedMetException {

        // Busca el turno asociado
        Turno turno = null;
        for (Turno t : bancoDatos.getTurnos()) {
            if (t.getIdTurno() == idTurno) {
                turno = t;
                break;
            }
        }

        if (turno == null) {
            throw new MedMetException("Turno seleccionado no existe");
        }

        // Valida el estado de pago previo del turno
        if ("Pagado".equalsIgnoreCase(turno.getEstadoPago())) {
            throw new MedMetException("El turno seleccionado ya se encuentra pagado");
        }

        if (monto <= 0) {
            throw new MedMetException("El monto debe ser mayor que 0");
        }

        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new MedMetException("Seleccione un metodo de pago valido");
        }

        // Instancia y procesa el pago
        int nuevoIdPago = bancoDatos.generarIdPago();
        Pago nuevoPago = new Pago(nuevoIdPago, turno, monto, metodoPago.trim());

        boolean cobroExitoso = nuevoPago.procesarCobro();

        if (!cobroExitoso) {
            throw new MedMetException("No se pudo procesar el pago correctamente");
        }

        bancoDatos.getPagos().add(nuevoPago);   // Almacena el pago en el repositorio

        return nuevoPago;
    }
}
