package medmet.gestor;

import medmet.modelo.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Repositorio central de datos de prueba basados en el script SQL
public class BancoDatos {

    private static BancoDatos instancia;

    private final List<Usuario> usuarios;
    private final List<Especialidad> especialidades;
    private final List<Medico>  medicos;
    private final List<Recepcionista> recepcionistas;
    private final List<Paciente> pacientes;
    private final List<Turno> turnos;
    private final List<Pago> pagos;
    private final List<HistoriaClinica> historiaClinicas;

    private int contIdEspecialidad;
    private int contIdMedico;
    private int contIdRecepcionista;
    private int contIdPaciente;
    private int contIdTurno;
    private int contIdPago;
    private int contIdHistoriaClinica;
    private int contIdEvolucion;

    // Constructor privado para evitar instanciaciones directas
    private BancoDatos(){

        this.usuarios = new ArrayList<>();
        this.especialidades = new ArrayList<>();
        this.medicos = new ArrayList<>();
        this.recepcionistas = new ArrayList<>();
        this.pacientes = new ArrayList<>();
        this.turnos = new ArrayList<>();
        this.pagos = new ArrayList<>();
        this.historiaClinicas = new ArrayList<>();

        cargarDatosInciales();
    }

    public static BancoDatos getInstancia(){    // Acceso global a la única instancia del repositorio
        if(instancia == null){
            instancia = new BancoDatos();
        }
        return instancia;
    }

    private void cargarDatosInciales(){     // Inicializa los datos de prueba

        // Usuarios
        usuarios.add(new Usuario(1, "recepcion_central", "Admin2026*", "RECEPCIONISTA"));
        usuarios.add(new Usuario(2, "dr_fernandez", "DocCardio26", "MEDICO"));
        usuarios.add(new Usuario(3, "dra_perez", "DocGen2026", "MEDICO"));
        usuarios.add(new Usuario(4, "dra_ruiz", "DocOdonto26", "MEDICO"));

        // Especialidades
        Especialidad cardio = new Especialidad(1, "Cardiología", "Evaluación preventiva, diagnóstico y control cardiovascular");
        Especialidad clinica = new Especialidad(2, "Medicina General","Atención médicaprimaria, clínica general y derivaciones");
        Especialidad odonto = new Especialidad(3, "Odontología","Salud bucal integral, limpiezas y tratamientos odontológicos");
        especialidades.add(cardio);
        especialidades.add(clinica);
        especialidades.add(odonto);
        contIdEspecialidad = 4;

        // Recepciosnista
        Recepcionista recep = new Recepcionista(1, "32111222","Laura","Gómez",
                "3876421001","laura.gomez@medmet.com","Av. 25 de Mayo 340", LocalDate.of(2024, 3, 1));
        recepcionistas.add(recep);
        contIdRecepcionista = 2;

        // Medicos
        Medico med1 = new Medico(1, "28444555", "Marcos", "Fernández", "3876422002",
                "m.fernandez@medmet.com", "Belgrano 120", "MP-5842", cardio);
        Medico med2 = new Medico(2, "30777888", "Valeria", "Pérez", "3876423003",
                "v.perez@medmet.com", "Mitre 550", "MP-6120", clinica);
        Medico med3 = new Medico(3, "26123987", "Andrea", "Ruiz", "3876424004",
                "a.ruiz@medmet.com", "San Martín 820", "MP-4399", odonto);
        medicos.add(med1);
        medicos.add(med2);
        medicos.add(med3);
        contIdMedico = 4;

        // Pacientes
        Paciente pac1 = new Paciente(1, "43686992", "Luciano Iván", "Ruiz Jiménez", "3876425005",
                "luciano.ruiz@gmail.com", "Güemes 150", LocalDate.of(2002, 2, 22), "Particular");
        Paciente pac2 = new Paciente(2, "35123456", "Javier", "Gómez", "3876426006",
                "javier.gomez@hotmail.com", "Alberdi 410", LocalDate.of(1990, 6, 15), "OSDE");
        Paciente pac3 = new Paciente(3, "28990112", "Marina", "Sosa", "3876427007",
                "marina.sosa@yahoo.com", "Rivadavia 930", LocalDate.of(1981, 11, 4), "INSSJP (PAMI)");
        pacientes.add(pac1);
        pacientes.add(pac2);
        pacientes.add(pac3);
        contIdPaciente = 4;

        // Historias Clinicas con sus evoluciones
        HistoriaClinica hc1 = new HistoriaClinica(1, pac1, "Paciente sin antecedentes patológicos relevantes declarados.");
        EvolucionMedica evm1 = new EvolucionMedica(1, med1, "Control cardiovascular de rutina",
                "Ritmo regular sin soplos.", "Mantener control preventivo anual.");
        evm1.setFechaHora(LocalDateTime.of(2026, 10, 19, 8, 45));
        hc1.agregarEvolucion(evm1);
        historiaClinicas.add(hc1);

        HistoriaClinica hc2 = new HistoriaClinica(2, pac2, "Paciente con antecedentes de hipertensión arterial leve.");
        EvolucionMedica evm2 = new EvolucionMedica(2, med2, "Chequeo clínico general", "Examen físico.",
                "Solicitud de laboratorio de sangre de rutina en ayunas.");
        evm2.setFechaHora(LocalDateTime.of(2026, 10, 19, 9, 20));
        hc2.agregarEvolucion(evm2);
        historiaClinicas.add(hc2);

        HistoriaClinica hc3 = new HistoriaClinica(3, pac3, "Paciente en seguimiento odontológico periódico.");
        EvolucionMedica evm3 = new EvolucionMedica(3, med3, "Molestia en pieza dental molar",
                "Gingivitis localizada.", "Limpieza dental profunda.");
        evm3.setFechaHora(LocalDateTime.of(2026, 10, 19, 9, 50));
        hc3.agregarEvolucion(evm3);
        historiaClinicas.add(hc3);

        contIdHistoriaClinica = 4;
        contIdEvolucion = 4;

        // Turnos
        Turno turno1 = new Turno(1, pac1, med1, LocalDate.of(2026, 10, 19), LocalTime.of(8, 30));
        turno1.setEstado("En curso");
        turno1.setEstadoPago("PAGADO");
        med1.agregarTurno(turno1);
        turnos.add(turno1);

        Turno turno2 = new Turno(2, pac2, med2, LocalDate.of(2026, 10, 19), LocalTime.of(9, 0));
        turno2.setEstado("En espera");
        turno2.setEstadoPago("PAGADO");
        med2.agregarTurno(turno2);
        turnos.add(turno2);

        Turno turno3 = new Turno(3, pac3, med3, LocalDate.of(2026, 10, 19), LocalTime.of(9, 30));
        turno3.setEstado("Pendiente");
        turno3.setEstadoPago("PAGADO");
        med3.agregarTurno(turno3);
        turnos.add(turno3);

        contIdTurno = 4;

        // Pagos
        Pago pago1 = new Pago(1, turno1, 8500.00, "Efectivo");
        pago1.setFechaHora(LocalDateTime.of(2026, 9, 29, 8, 20));
        pagos.add(pago1);

        Pago pago2 = new Pago(2, turno2, 12000.00, "Transferencia");
        pago2.setFechaHora(LocalDateTime.of(2026, 9, 29, 8, 50));
        pagos.add(pago2);

        Pago pago3 = new Pago(3, turno3, 9500.00, "Debito");
        pago3.setFechaHora(LocalDateTime.of(2026, 9, 29, 9, 15));
        pagos.add(pago3);

        contIdPago = 4;
    }

    // ------------------- Metodos de generacion de id -------------------
    public int generarIdEspecialidad() {
        return contIdEspecialidad++;
    }

    public int generarIdMedico() {
        return contIdMedico++;
    }

    public int generarIdRecepcionista() {
        return contIdRecepcionista++;
    }

    public int generarIdPaciente() {
        return contIdPaciente++;
    }

    public int generarIdTurno() {
        return contIdTurno++;
    }

    public int generarIdPago() {
        return contIdPago++;
    }

    public int generarIdHistoriaClinica() {
        return contIdHistoriaClinica++;
    }

    public int generarIdEvolucion() {
        return contIdEvolucion++;
    }


    // ------------------- Metodos GETTER -------------------
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Especialidad> getEspecialidades() {
        return especialidades;
    }

    public List<Medico> getMedicos() {
        return medicos;
    }

    public List<Recepcionista> getRecepcionistas() {
        return recepcionistas;
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public List<Turno> getTurnos() {
        return turnos;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public List<HistoriaClinica> getHistoriasClinicas() {
        return historiaClinicas;
    }
}
