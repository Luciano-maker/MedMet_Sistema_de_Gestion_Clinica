package medmet.controlador;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import medmet.app.AppMedMet;
import medmet.excepciones.MedMetException;
import medmet.gestor.GestorEspecialidades;
import medmet.gestor.GestorMedicos;
import medmet.gestor.GestorTurnos;
import medmet.modelo.Especialidad;
import medmet.modelo.Medico;
import medmet.modelo.Turno;
import medmet.vistas.recepcion.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Controlador para la pantalla principal de Recepcion
public class RecepcionistaController {

    private final RecepcionistaMainView vista;
    private final Stage stageActual;

    private final GestorTurnos gestorTurnos;
    private final GestorMedicos gestorMedicos;
    private final GestorEspecialidades gestorEspecialidades;

    // Constructor
    public RecepcionistaController(RecepcionistaMainView vista, Stage stageActual) {

        this.vista = vista;
        this.stageActual = stageActual;

        this.gestorTurnos = new GestorTurnos();
        this.gestorMedicos = new GestorMedicos();
        this.gestorEspecialidades = new GestorEspecialidades();

        inicializarCombos();
        inicializarEventos();
        cargarAgenda();
    }

    private void inicializarCombos() {

        // Carga las especialidades y agrega la opcion de "Todas" en filtros
        List<Especialidad> listaEsp = new ArrayList<>();
        listaEsp.add(null);
        listaEsp.addAll(gestorEspecialidades.listarEspecialidadesActivas());

        vista.getCbEspecialidad().setItems(FXCollections.observableArrayList(listaEsp));
        vista.getCbEspecialidad().setConverter(new StringConverter<>() {
            @Override
            public String toString(Especialidad e) {
                return e != null ? e.getNombre() : "Todas";
            }
            @Override
            public Especialidad fromString(String string) {
                return null;
            }
        });
        vista.getCbEspecialidad().getSelectionModel().selectFirst();

        // Carga los medicos y agrega la opcion de "Todos" en filtros
        actualizarComboMedicos(null);

        vista.getCbEspecialidad().setOnAction(event -> {
            Especialidad seleccionada = vista.getCbEspecialidad().getValue();
            actualizarComboMedicos(seleccionada);
        });

    }

    private void actualizarComboMedicos(Especialidad esp) {

        List<Medico> listaMedicos = new ArrayList<>();
        listaMedicos.add(null);

        if (esp != null) {
            listaMedicos.addAll(gestorMedicos.listarxEspecialidad(esp.getIdEspecialidad()));
        } else {
            listaMedicos.addAll(gestorMedicos.listarMedicosActivos());
        }

        vista.getCbMedico().setItems(FXCollections.observableArrayList(listaMedicos));
        vista.getCbMedico().setConverter(new StringConverter<>() {
            @Override
            public String toString(Medico m) {
                return m != null ? "Dr(a). " + m.getApellido() + " (" + m.getMatriculaProfesional() + ")" : "Todos";
            }
            @Override
            public Medico fromString(String string) {
                return null;
            }
        });
        vista.getCbMedico().getSelectionModel().selectFirst();
    }

    private void inicializarEventos() {

        // Filtra y Busca
        vista.getBtnBuscar().setOnAction(e -> aplicarFiltroAgenda());
        vista.getBtnLimpiarFiltros().setOnAction(e -> reestrablecerFiltros());

        // Actualiza de forma dinamica la barra de estado segun la fila seleccionada
        vista.getTablaTurnos().getSelectionModel().selectedItemProperty().addListener((observable, anterior, seleccionado) -> {

            if (seleccionado != null) {

                vista.getLblEstadoSeleccion().setText(String.format("Turno seleccionado: %s - %s | Estado cita: %s | Estado pago: %s",
                        seleccionado.getHora(), seleccionado.getPaciente().getNombreCompleto(), seleccionado.getEstado(), seleccionado.getEstadoPago()));

            } else {
                vista.getLblEstadoSeleccion().setText("Seleccione un Turno de la tabla para comenzar");
            }
        });

        // Acciones operativas de botones
        vista.getBtnConfirmarLlegada().setOnAction(e -> procesarConfirmarLlegada());
        vista.getBtnCancelarTurno().setOnAction(e -> procesarCancelarTurno());

        // Abre ventana de Nuevo Paciente
        vista.getBtnNuevoPaciente().setOnAction(e -> {
            PacientesView pacView = new PacientesView(stageActual);
            pacView.abrirFormulario();
        });

        // Abre ventana para Agendar Turno
        vista.getBtnAgendarTurno().setOnAction(e -> {
            AgendarTurnoDialogo dialogo = new AgendarTurnoDialogo(stageActual, null);
            if (dialogo.showAndWait()) {
                cargarAgenda();
            }
        });

        // Reprograma un Turno sleccionado
        vista.getBtnReprogramar().setOnAction(e -> {
            Turno selec = vista.getTablaTurnos().getSelectionModel().getSelectedItem();
            if (selec == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione un turno para reprogramar");
                return;
            }
            AgendarTurnoDialogo dialogo = new AgendarTurnoDialogo(stageActual, selec);
            if (dialogo.showAndWait()) {
                cargarAgenda();
            }
        });

        // Registra el pago de un turno seleccionado
        vista.getBtnRegistrarPago().setOnAction(e -> {
            Turno selec = vista.getTablaTurnos().getSelectionModel().getSelectedItem();
            if (selec == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atención",  "Seleccione un turno para registrar el pago");
                return;
            }
            RegistrarPagoDialogo dialogo = new RegistrarPagoDialogo(stageActual, selec);
            if (dialogo.showAndWait()) {
                cargarAgenda();
            }
        });

        // Menu Archivo
        vista.getMiSalir().setOnAction(e -> stageActual.close());
        vista.getMiCerrarSesion().setOnAction(e -> {
            stageActual.close();
            new AppMedMet().start(new Stage());
        });

        vista.getMiVerAgenda().setOnAction(e -> cargarAgenda());

        vista.getMiListarPacientes().setOnAction(e -> new PacientesView(stageActual).show());

        vista.getMiListarMedicos().setOnAction(e -> new MedicosView(stageActual).show());

        vista.getMiListarEspecialidades().setOnAction(e -> new EspecialidadesView(stageActual).show());

        vista.getMiAcercaDe().setOnAction(e -> {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Acerca de MedMet",
                    ":: MedMet - Sistema de Gestión :: \n:: Seminario de Práctica de Informática ::");
        });
    }

    public void cargarAgenda() {

        LocalDate fecha = vista.getDpFecha().getValue();
        List<Turno> turnos = gestorTurnos.ordenarTurnosxHorario(fecha);
        vista.getTablaTurnos().setItems(FXCollections.observableArrayList(turnos));
    }

    private void aplicarFiltroAgenda() {

        LocalDate fecha = vista.getDpFecha().getValue();
        Especialidad esp = vista.getCbEspecialidad().getValue();
        Medico med = vista.getCbMedico().getValue();

        Integer idEsp = (esp != null) ? esp.getIdEspecialidad() : null;
        Integer idMed = (med != null) ? med.getIdMedico() : null;

        List<Turno> resultado = gestorTurnos.buscarTurnos(fecha, idMed,idEsp, null);
        vista.getTablaTurnos().setItems(FXCollections.observableArrayList(resultado));

        if (resultado.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Agenda", "No se encontraron turnos con los filtros seleccionados");
        }
    }

    private void reestrablecerFiltros() {

        List<Turno> turnosExistentes = gestorTurnos.listaCompletaTurnos();

        if (!turnosExistentes.isEmpty()) {      // Detecta de forma dinamica la fecha base de los turnos registrados
            vista.getDpFecha().setValue(turnosExistentes.get(0).getFecha());
        }

        vista.getCbEspecialidad().getSelectionModel().selectFirst();        // Selecciona "Todaas" en el filtro de
                                                                            // especialidad
        actualizarComboMedicos(null);
        cargarAgenda();
    }

    private void procesarConfirmarLlegada() {

        Turno seleccionado = vista.getTablaTurnos().getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione un turno para cofirmar llegada");
            return;
        }

        try {
            gestorTurnos.confirmarLlegada(seleccionado.getIdTurno());
            cargarAgenda();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente en Espera", "El paciente " +
                    seleccionado.getPaciente().getNombreCompleto() + " pasó a estado 'En espera'");

        } catch (MedMetException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Operación", ex.getMessage());
        }
    }

    private void procesarCancelarTurno() {

        Turno seleccionado = vista.getTablaTurnos().getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Seleccione un turno para cancelar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cancelar Turno");
        confirmacion.setHeaderText("Confirmación de Cancelación");
        confirmacion.setContentText("Está seguro de cancelar el turno de las " + seleccionado.getHora() +
                " para el paciente" + seleccionado.getPaciente().getNombreCompleto() + "?");

        Optional<ButtonType> respuesta =  confirmacion.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                gestorTurnos.cancelarTurno(seleccionado.getIdTurno());
                cargarAgenda();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El turno fue cancelado correactamente");

            } catch (Exception ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Restricción Operativa", ex.getMessage());
            }
        }
    }



    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {

        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public RecepcionistaMainView getVista() {
        return vista;
    }
}
