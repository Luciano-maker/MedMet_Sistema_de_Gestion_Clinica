package medmet.vistas.recepcion;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import medmet.excepciones.MedMetException;
import medmet.excepciones.TurnoInvalidoException;
import medmet.gestor.GestorEspecialidades;
import medmet.gestor.GestorMedicos;
import medmet.gestor.GestorPacientes;
import medmet.gestor.GestorTurnos;
import medmet.modelo.Especialidad;
import medmet.modelo.Medico;
import medmet.modelo.Paciente;
import medmet.modelo.Turno;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Ventana de Dialogo o Subpantalla para la funcionalidad de Agendar o Reprogramar un Turno
public class AgendarTurnoDialogo {

    private final Stage stage;
    private final GestorTurnos gestorTurnos;
    private final GestorPacientes gestorPacientes;
    private final GestorMedicos gestorMedicos;
    private final GestorEspecialidades gestorEspecialidades;

    private final ComboBox<Paciente> cbPaciente;
    private final ComboBox<Especialidad> cbEspecialidad;
    private final ComboBox<Medico> cbMedico;
    private final DatePicker dpFecha;
    private final ComboBox<LocalTime> cbHorario;
    private final Button btnConfirmar;

    private final Turno turnoAReprogramar;
    private boolean operacionExitosa = false;

    // Constructor
    public AgendarTurnoDialogo(Stage parentStage, Turno turnoExistente) {

        this.turnoAReprogramar = turnoExistente;
        this.gestorTurnos = new GestorTurnos();
        this.gestorPacientes = new GestorPacientes();
        this.gestorMedicos = new GestorMedicos();
        this.gestorEspecialidades = new GestorEspecialidades();

        this.stage = new Stage();
        this.stage.initOwner(parentStage);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.setTitle(turnoExistente == null ? "MedMet | Agendar Nuevo Turno" : "MedMet | Reprogramar Turno");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f4f6f8;");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);

        // Controles
        cbPaciente = new ComboBox<>();
        cbPaciente.setPrefWidth(280);

        List<Paciente> pacientesDisponibles = gestorPacientes.ordenarPacientesAlbeticamente();

        if (pacientesDisponibles.isEmpty()) {
            pacientesDisponibles = gestorPacientes.listarPacientesActivos();
        }
        cbPaciente.setItems(FXCollections.observableArrayList(pacientesDisponibles));
        cbPaciente.setConverter(new StringConverter<>() {       // Convierte texto para la seleccion
            @Override
            public String toString(Paciente p) {
                return p != null ? p.getNombreCompleto() + " (" + p.getDni() + ")" : "";
            }
            @Override
            public Paciente fromString(String string) {
                return null;
            }
        });
        cbPaciente.setCellFactory(lv -> new ListCell<>() {      // Renderiza los elementos de la lista
            @Override                                                            // desplegable
            protected void updateItem(Paciente p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setText(null);
                } else {
                    setText(p.getNombreCompleto() + " (DNI: " + p.getDni() + ")");
                }
            }
        });
        cbPaciente.setButtonCell(new ListCell<>() {         // Renderiza el boton visible
            @Override
            protected void updateItem(Paciente p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setText(null);
                } else {
                    setText(p.getNombreCompleto() + " (DNI: " + p.getDni() + ")");
                }
            }
        });

        cbEspecialidad = new ComboBox<>();
        cbEspecialidad.setPrefWidth(280);
        cbEspecialidad.setItems(FXCollections.observableArrayList(gestorEspecialidades.listarEspecialidadesActivas()));
        cbEspecialidad.setConverter(new StringConverter<>() {
            @Override
            public String toString(Especialidad e) {
                return e != null ? e.getNombre() :  "";
            }
            @Override
            public Especialidad fromString(String string) {
                return null;
            }
        });

        cbMedico = new ComboBox<>();
        cbMedico.setPrefWidth(280);
        cbMedico.setConverter(new StringConverter<>() {
            @Override
            public String toString(Medico m) {
                return m != null ? "Dr(a). " + m.getApellido() + " - " + m.getMatriculaProfesional() : "";
            }
            @Override
            public Medico fromString(String string) {
                return null;
            }
        });

        // Filtra medicos por especialidad
        cbEspecialidad.setOnAction(event -> {
            Especialidad esp = cbEspecialidad.getValue();
            if (esp != null) {
                cbMedico.setItems(FXCollections.observableArrayList(gestorMedicos.listarxEspecialidad(esp.getIdEspecialidad())));
            } else {
                cbMedico.setItems(FXCollections.observableArrayList(gestorMedicos.listarMedicosActivos()));
            }
        });

        dpFecha = new DatePicker(LocalDate.of(2026, 10, 19));
        dpFecha.setPrefWidth(280);

        cbHorario = new ComboBox<>();
        cbHorario.setPrefWidth(280);
        franjaHoraria();

        grid.add(new Label("Paciente:*"), 0, 0);
        grid.add(cbPaciente, 1, 0);
        grid.add(new  Label("Especialidad:*"), 0, 1);
        grid.add(cbEspecialidad, 1, 1);
        grid.add(new  Label("Medico:*"), 0, 2);
        grid.add(cbMedico, 1, 2);
        grid.add(new Label("Fecha:*"), 0, 3);
        grid.add(dpFecha, 1, 3);
        grid.add(new  Label("Horario:*"), 0, 4);
        grid.add(cbHorario, 1, 4);

        if (turnoAReprogramar != null) {        // Precarga si es para reprogramar
            cbPaciente.setValue(turnoAReprogramar.getPaciente());
            cbPaciente.setDisable(true);
            cbMedico.setValue(turnoAReprogramar.getMedico());
            cbMedico.setDisable(true);

            if (turnoAReprogramar.getMedico() != null) {
                cbEspecialidad.setValue(turnoAReprogramar.getMedico().getEspecialidad());
            }

            cbEspecialidad.setDisable(true);
            dpFecha.setValue(turnoAReprogramar.getFecha());
            cbHorario.setValue(turnoAReprogramar.getHora());
        }

        HBox panelBotones = new HBox(10);
        panelBotones.setAlignment(Pos.CENTER_RIGHT);
        btnConfirmar = new Button(turnoAReprogramar == null ? "Confirmar Turno" : "Confirmar Reprogramación");
        btnConfirmar.setStyle("-fx-font-weight: bold;");
        Button btnCancelar = new Button("Cancelar");
        panelBotones.getChildren().addAll(btnConfirmar, btnCancelar);

        root.getChildren().addAll(grid, panelBotones);

        btnConfirmar.setOnAction(event -> procesarGuardado());
        btnCancelar.setOnAction(event -> stage.close());

        stage.setScene(new Scene(root, 410, 290));
        stage.setResizable(false);
    }

    private void franjaHoraria() {

        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fin = LocalTime.of(19, 0);

        while (!inicio.isAfter(fin)) {
            cbHorario.getItems().add(inicio);
            inicio = inicio.plusHours(30);
        }
    }

    private void procesarGuardado() {

        Paciente pac = cbPaciente.getValue();
        Medico med = cbMedico.getValue();
        LocalDate fecha = dpFecha.getValue();
        LocalTime hora = cbHorario.getValue();

        try {
            if (turnoAReprogramar == null) {
                if (pac == null || med == null || fecha == null || hora == null) {
                    throw new MedMetException("Todos los campos son obligatorios");
                }

                gestorTurnos.agendarTurno(pac.getIdPaciente(), med.getIdMedico(), fecha, hora);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Turno guardado orrectamente");

            } else {
                gestorTurnos.reprogramarTurno(turnoAReprogramar.getIdTurno(), fecha, hora);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Reprogramacion de Turno realizado correctamente");
            }

            operacionExitosa = true;
            stage.close();

        } catch (TurnoInvalidoException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Conflicto de Agenda: " + ex.getMessage());
        } catch (MedMetException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Datos: " + ex.getMessage());
        }
    }

    public boolean showAndWait() {
        stage.showAndWait();
        return operacionExitosa;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo, mensaje);
        alert.initOwner(stage);
        alert.showAndWait();
    }
}
