package medmet.modelo;

import java.time.LocalDateTime;

// Clase Pago
public class Pago {

    private int idPago;
    private Turno turno;
    private double monto;
    private String metodoPago;      // Efectivo, Debito, Transferencia
    private LocalDateTime fechaHora;

    // ----------- Constructor -------------------
    public Pago(int idPago, Turno turno, double monto, String metodoPago) {

        this.idPago = idPago;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.fechaHora = null;
    }

    //  ------------------- Metodo Logico -------------------
    public boolean procesarCobro(){     // Procesa la transaccion enconomia, validando el monto e impara en el estado del turno
        if (monto > 0 && turno != null) {
            this.fechaHora = LocalDateTime.now();
            this.turno.registrarPago();
            return true;
        }
        return false;
    }

    // ------------------- Metodos GETTER -------------------
    public int getIdPago() {
        return idPago;
    }

    public Turno getTurno() {
        return turno;
    }

    public double getMonto() {
        return monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    // ------------------- Metodos SETTER -------------------
    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}
