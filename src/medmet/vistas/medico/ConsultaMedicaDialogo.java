package medmet.vistas.medico;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import medmet.excepciones.MedMetException;
import medmet.gestor.GestorHistoriaClinica;
import medmet.modelo.EvolucionMedica;
import medmet.modelo.HistoriaClinica;
import medmet.modelo.Paciente;
import medmet.modelo.Turno;

import java.time.format.DateTimeFormatter;

// Ventana para la atencion medica en consultorio
public class ConsultaMedicaDialogo {

    private final Stage stage;
    private final Turno turno;
    private final GestorHistoriaClinica gestorHC;

    private final TextField txtMotivo;
    private final TextArea txtDiagnostico;
    private final TextArea txtTratamiento;
    private final TableView<EvolucionMedica> tablaAntecedentes;

    private boolean atencionFinalizada = false;

    public ConsultaMedicaDialogo(Stage parentStage, Turno turno) {

        this.turno = turno;
        this.gestorHC = new GestorHistoriaClinica();
        this.stage = new Stage();
        this.stage.initOwner(parentStage);
        this.stage.initModality(Modality.APPLICATION_MODAL);

        Paciente pac = turno.getPaciente();
        this.stage.setTitle("MedMet | Consulta Médica - Paciente: " + pac.getNombreCompleto());

        VBox root = new VBox(12);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f4f6f8;");

        VBox cardPaciente = new VBox(6);        // Tarjeta informativa con los datos del paciente
        cardPaciente.setPadding(new Insets(10, 15, 10, 15));
        cardPaciente.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 13px;");

        Label lblTituloPac = new Label("Datos del Paciente");
        lblTituloPac.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 13px;");

        HBox filaDatos = new HBox(25);
        filaDatos.setAlignment(Pos.CENTER_LEFT);
        Label lblNom = new Label("Nombre: " + pac.getNombreCompleto());
        lblNom.setStyle("-fx-font-weight: bold;");
        Label lblDni = new Label("DNI: " + pac.getDni());
        Label lblEdad = new Label("Edad: " + pac.calcularEdad() + " años");
        Label lblObra = new Label("Cobertura Médica: " + pac.getObraSocial());
        filaDatos.getChildren().addAll(lblNom, lblDni, lblEdad, lblObra);

        cardPaciente.getChildren().addAll(lblTituloPac, filaDatos);

        HBox panelIntermedio = new HBox(12);        // Tabla con antecedentes previos de un lado
                                                       // y del otro, motivos de consulta
        // Tabla de consultas pasadas
        VBox colAntecedentes = new VBox(5);
        Label lblAntecedente = new Label("Antecedentes y Consultas Previas:");
        lblAntecedente.setStyle("-fx-font-weight: bold; -fx-text-fill: #444444;");

        tablaAntecedentes = new TableView<>();
        tablaAntecedentes.setPrefHeight(130);
        tablaAntecedentes.setPrefWidth(420);
        tablaAntecedentes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<EvolucionMedica, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaHora() !=
                null ? c.getValue().getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-"));
        colFecha.setMinWidth(125);
        colFecha.setPrefWidth(135);

        TableColumn<EvolucionMedica, String> colDiagnostico = new TableColumn<>("Diagnóstico Previo");
        colDiagnostico.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDiagnostico()));
        colDiagnostico.setMinWidth(180);

        tablaAntecedentes.getColumns().addAll(colFecha, colDiagnostico);
        colAntecedentes.getChildren().addAll(lblAntecedente, tablaAntecedentes);
        HBox.setHgrow(colAntecedentes, Priority.ALWAYS);

        // Motivo de consulta actual
        VBox colMotivo = new VBox(5);
        Label lblMot = new Label("Motivo de Consulta Actual:*");
        lblMot.setStyle("-fx-font-weight: bold; -fx-text-fill: #444444;");
        txtMotivo = new TextField("Control clínico de rutina");
        txtMotivo.setPrefHeight(35);
        colMotivo.getChildren().addAll(lblMot, txtMotivo);
        colMotivo.setPrefWidth(300);

        panelIntermedio.getChildren().addAll(colAntecedentes, colMotivo);

        // Panel de Evolución Médica y Tratamiento
        VBox panelEvolucion = new VBox(8);
        panelEvolucion.setPadding(new Insets(12));
        panelEvolucion.setStyle("-fx-background-color: #ffffff; -fx-border-color: #c8c8c8; -fx-border-radius: 4;");

        Label lblTituloEvo = new Label("Evolución Médica y Plan Terapéutico");
        lblTituloEvo.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 13px;");

        Label lblDiagnosticoActual = new Label("Diagnóstico Clínico:*");
        lblDiagnosticoActual.setStyle("-fx-font-weight: bold;");
        txtDiagnostico = new TextArea();
        txtDiagnostico.setPromptText("Ingrese el diagnóstico del examen físico, signos vitales...");
        txtDiagnostico.setPrefRowCount(3);

        Label lblTratamientoActual = new Label("Indicaciones y Tratamiento:*");
        lblTratamientoActual.setStyle("-fx-font-weight: bold;");
        txtTratamiento = new TextArea();
        txtTratamiento.setPromptText("Plan terapéutico, solicitudes de estudios...");
        txtTratamiento.setPrefRowCount(3);

        panelEvolucion.getChildren().addAll(lblTituloEvo, lblDiagnosticoActual, txtDiagnostico, lblTratamientoActual, txtTratamiento);

        // Botones de la parte inferior
        HBox panelBotones = new HBox(12);
        panelBotones.setAlignment(Pos.CENTER_RIGHT);

        Button btnCancelar = new Button("Cancelar Consulta");
        Button btnFinalizar = new Button("Guardar Historia Clínica y Finalizar");
        btnFinalizar.setStyle("-fx-font-weight: bold; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");

        panelBotones.getChildren().addAll(btnCancelar, btnFinalizar);

        root.getChildren().addAll(cardPaciente, panelIntermedio, panelEvolucion, panelBotones);

        cargarAntecedentes(pac.getIdPaciente());        // Carga historial previo si existe

        // Eventos
        btnCancelar.setOnAction(e -> stage.close());
        btnFinalizar.setOnAction(e -> guardadoDeEvolucion());

        stage.setScene(new Scene(root, 760, 580));
    }

    private void cargarAntecedentes(int idPaciente) {
        HistoriaClinica hc = gestorHC.buscarHistoriaClinicaxPaciente(idPaciente);
        if (hc != null && hc.getEvoluciones() != null) {
            tablaAntecedentes.setItems(FXCollections.observableArrayList(hc.getEvoluciones()));
        }
    }

    private void guardadoDeEvolucion() {

        String motivo = txtMotivo.getText();
        String diagnostico = txtDiagnostico.getText();
        String tratamiento = txtTratamiento.getText();

        try {
            gestorHC.finalizarAtencionTurno(turno.getIdTurno(), motivo, diagnostico, tratamiento);

            atencionFinalizada = true;
            Alert exito = new Alert(Alert.AlertType.INFORMATION);
            exito.setTitle("Consulta Finalizada");
            exito.setHeaderText(null);
            exito.setContentText("La evolución clínica se registró y el turno pasó a estado 'Finalizado'.");
            exito.initOwner(stage);
            exito.showAndWait();

            stage.close();

        } catch (MedMetException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Validación");
            alert.setHeaderText(null);
            alert.setContentText(ex.getMessage());
            alert.initOwner(stage);
            alert.showAndWait();
        }
    }

    public boolean showAndWait() {
        stage.showAndWait();
        return atencionFinalizada;
    }
}
