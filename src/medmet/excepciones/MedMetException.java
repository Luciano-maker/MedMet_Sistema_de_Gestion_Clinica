package medmet.excepciones;

public class MedMetException  extends Exception{

    // Constructor que recibe el mensaje descriptivo del fallo
    public MedMetException(String mensaje){
        super(mensaje);
    }

    // Contructor que permite encadenar una causa previa
    public MedMetException(String mensaje, Throwable causa){
        super(mensaje, causa);
    }
}
