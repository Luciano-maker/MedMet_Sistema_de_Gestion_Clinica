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
import medmet.gestor.GestorHistoriaClinica;
import medmet.modelo.EvolucionMedica;
import medmet.modelo.HistoriaClinica;

import java.time.format.DateTimeFormatter;
import java.util.List;

// Subpantalla para consultar el expediente médico de los pacientes
public class BuscarHCDialogo {

    private final Stage stage;
    private final GestorHistoriaClinica gestorHC;

    private final TextField txtFiltro;
    private final TableView<HistoriaClinica> tablaExpedientes;
    private final TableView<EvolucionMedica> tablaEvoluciones;
    private final TextArea txtDetalle;

    public BuscarHCDialogo(Stage parentStage) {

        this.gestorHC = new GestorHistoriaClinica();
        this.stage = new Stage();
        this.stage.initOwner(parentStage);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.setTitle("MedMet | Consulta de Historias Clínicas");

        VBox root = new VBox(12);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f4f6f8;");

        HBox panelBusqueda = new HBox(10);      // Barra de busqueda
        panelBusqueda.setAlignment(Pos.CENTER_LEFT);
        txtFiltro = new TextField();
        txtFiltro.setPromptText("Buscar por DNI o apellido del paciente...");
        txtFiltro.setPrefWidth(280);
        Button btnBuscar = new Button("Buscar");
        Button btnTodos = new Button("Ver Todos");
        panelBusqueda.getChildren().addAll(new Label("Paciente:"), txtFiltro, btnBuscar, btnTodos);

        SplitPane split = new SplitPane();      // Split o division de ventana
        split.setPrefHeight(380);

        VBox panelIzquierdo = new VBox(6);      // Lista de expedientes del lado izquierdo
        panelIzquierdo.setPadding(new Insets(5));
        Label lblExp = new Label("Expedientes Clínicos Registrados:");
        lblExp.setStyle("-fx-font-weight: bold;");

        tablaExpedientes = new TableView<>();
        tablaExpedientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<HistoriaClinica, String> colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaciente().getDni()));
        colDni.setMinWidth(95);
        colDni.setPrefWidth(105);

        TableColumn<HistoriaClinica, String> colPaciente = new TableColumn<>("Paciente");
        colPaciente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaciente().getNombreCompleto()));
        colPaciente.setMinWidth(150);
        colPaciente.setPrefWidth(180);

        tablaExpedientes.getColumns().addAll(colDni, colPaciente);
        panelIzquierdo.getChildren().addAll(lblExp, tablaExpedientes);
        VBox.setVgrow(tablaExpedientes, Priority.ALWAYS);

        VBox panelDerecho = new VBox(6);        // Detalle de evoluciones del expediente seleccionado del lado derecho
        panelDerecho.setPadding(new Insets(5));

        Label lblEvo = new Label("Evoluciones Médicas del Paciente:");
        lblEvo.setStyle("-fx-font-weight: bold;");

        tablaEvoluciones = new TableView<>();
        tablaEvoluciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaEvoluciones.setPrefHeight(160);

        TableColumn<EvolucionMedica, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaHora() !=
                null ? c.getValue().getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-"));
        colFecha.setMinWidth(125);
        colFecha.setPrefWidth(135);

        TableColumn<EvolucionMedica, String> colMedico = new TableColumn<>("Médico");
        colMedico.setCellValueFactory(c -> new SimpleStringProperty("Dr(a). " + c.getValue().getMedico().getApellido()));
        colMedico.setMinWidth(110);
        colMedico.setPrefWidth(130);

        TableColumn<EvolucionMedica, String> colMotivo = new TableColumn<>("Motivo");
        colMotivo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMotivoConsulta()));
        colMotivo.setMinWidth(150);
        colMotivo.setPrefWidth(180);

        tablaEvoluciones.getColumns().addAll(colFecha, colMedico, colMotivo);

        Label lblDetalle = new Label("Diagnóstico y Tratamiento Registrado:");
        lblDetalle.setStyle("-fx-font-weight: bold;");
        txtDetalle = new TextArea();
        txtDetalle.setEditable(false);
        txtDetalle.setPrefRowCount(4);

        panelDerecho.getChildren().addAll(lblEvo, tablaEvoluciones, lblDetalle, txtDetalle);
        VBox.setVgrow(txtDetalle, Priority.ALWAYS);

        split.getItems().addAll(panelIzquierdo, panelDerecho);
        split.setDividerPositions(0.35);

        // Botones de cierre
        HBox panelBoton = new HBox();
        panelBoton.setAlignment(Pos.CENTER_RIGHT);
        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setOnAction(e -> stage.close());
        panelBoton.getChildren().add(btnCerrar);

        root.getChildren().addAll(panelBusqueda, split, panelBoton);
        VBox.setVgrow(split, Priority.ALWAYS);

        // Eventos
        btnBuscar.setOnAction(e -> filtrar());
        btnTodos.setOnAction(e -> { txtFiltro.clear(); cargarTodos(); });

        tablaExpedientes.getSelectionModel().selectedItemProperty().addListener((obs, ant, selec) -> {
            if (selec != null) {
                tablaEvoluciones.setItems(FXCollections.observableArrayList(selec.getEvoluciones()));
                txtDetalle.clear();
            } else {
                tablaEvoluciones.getItems().clear();
                txtDetalle.clear();
            }
        });

        tablaEvoluciones.getSelectionModel().selectedItemProperty().addListener((obs, ant, selec) -> {
            if (selec != null) {
                txtDetalle.setText(String.format("DIAGNÓSTICO:%n%s%n%nINDICACIONES / TRATAMIENTO:%n%s",
                        selec.getDiagnostico(), selec.getTratamiento()));
            } else {
                txtDetalle.clear();
            }
        });

        cargarTodos();
        stage.setScene(new Scene(root, 820, 480));
    }

    public void show() {
        stage.showAndWait();
    }

    private void filtrar() {
        List<HistoriaClinica> lista = gestorHC.buscarHistoriasClinicas(txtFiltro.getText());
        tablaExpedientes.setItems(FXCollections.observableArrayList(lista));
    }

    private void cargarTodos() {
        tablaExpedientes.setItems(FXCollections.observableArrayList(gestorHC.listarTodasLasHistoriasClinicas()));
    }
}
