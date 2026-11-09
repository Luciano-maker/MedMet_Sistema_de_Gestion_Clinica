package medmet.excepciones;

public class TurnoInvalidoException extends MedMetException {

    // Contructor con mensaje explicativo
    public TurnoInvalidoException(String mensaje){
        super(mensaje);
    }

    // Contructor cargado con causa previa
    public TurnoInvalidoException(String mensaje, Throwable cause) {
        super(mensaje, cause);
    }
}
