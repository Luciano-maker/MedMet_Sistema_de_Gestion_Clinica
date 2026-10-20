package medmet.vistas.recepcion;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import medmet.excepciones.MedMetException;
import medmet.gestor.GestorPacientes;
import medmet.modelo.Paciente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Subpantalla de la seccion Pacientes del Modulo de Administracion
public class PacientesView {

    private final Stage stage;
    private final GestorPacientes gestorPacientes;
    private final TableView<Paciente> tabla;
    private final TextField txtBuscar;

    public PacientesView(Stage presentStage) {

        this.gestorPacientes = new GestorPacientes();
        this.stage = new Stage();
        this.stage.initOwner(presentStage);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.setTitle("MedMet | Sección de Pacientes");

        VBox root = new VBox(12);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f4f6f8;");

        // Barra de busqueda
        HBox panelBusqueda = new HBox(10);
        panelBusqueda.setAlignment(Pos.CENTER_LEFT);
        txtBuscar = new TextField();
        txtBuscar.setPromptText("Ingresar DNI, apellido u obra social...");
        txtBuscar.setPrefWidth(300);
        Button btnBuscar = new Button("Buscar");
        Button btnLimpiar = new Button("Ver Todos");
        panelBusqueda.getChildren().addAll(new Label("Filtro: "), txtBuscar, btnBuscar, btnLimpiar);

        // Tabla de pacientes
        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Paciente, String> colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDni()));
        colDni.setMinWidth(95);
        colDni.setPrefWidth(100);

        TableColumn<Paciente, String> colNombre = new TableColumn<>("Paciente");
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colNombre.setMinWidth(180);
        colNombre.setPrefWidth(210);

        TableColumn<Paciente, String> colObraSocial = new TableColumn<>("Obra Social");
        colObraSocial.setCellValueFactory(c ->  new SimpleStringProperty(c.getValue().getObraSocial()));
        colObraSocial.setMinWidth(110);
        colObraSocial.setPrefWidth(125);

        TableColumn<Paciente, String> colEdad = new TableColumn<>("Edad");
        colEdad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().calcularEdad() + " años"));
        colEdad.setMinWidth(80);
        colEdad.setPrefWidth(85);

        TableColumn<Paciente, String> colTelefono = new TableColumn<>("Telefono");
        colTelefono.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelefono()));
        colTelefono.setMinWidth(110);
        colTelefono.setPrefWidth(120);

        TableColumn<Paciente, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colEmail.setMinWidth(160);
        colEmail.setPrefWidth(180);

        tabla.getColumns().addAll(colDni, colNombre, colObraSocial, colEdad, colTelefono, colEmail);

        // Botones de Accion
        HBox panelBotones = new HBox(10);
        panelBotones.setAlignment(Pos.CENTER_RIGHT);
        Button btnNuevo = new Button("Registrar Paciente");
        btnNuevo.setStyle("-fx-font-weight: bold;");
        Button btnModificar = new Button("Modificar");
        Button btnBaja = new Button("Dar de Baja");
        Button btnCerrar = new Button("Cerrar");
        panelBotones.getChildren().addAll(btnNuevo, btnModificar, btnBaja, btnCerrar);

        root.getChildren().addAll(panelBusqueda, tabla, panelBotones);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        // Eventos
        btnBuscar.setOnAction(e -> filtrar());
        btnLimpiar.setOnAction(e -> {
            txtBuscar.clear();
            refrescarTabla();
        });
        btnNuevo.setOnAction(e -> mostrarFormulario(null));
        btnModificar.setOnAction(e -> {
            Paciente p = tabla.getSelectionModel().getSelectedItem();
            if (p != null) {
                mostrarFormulario(p);
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un paciente de la tabla");
            }
        });
        btnBaja.setOnAction(e -> procesarBaja());
        btnCerrar.setOnAction(e -> stage.close());

        refrescarTabla();
        stage.setScene(new Scene(root, 880, 460));
    }

    public void show() {
        stage.show();
    }

    public void abrirFormulario() {
        mostrarFormulario(null);
    }

    private void refrescarTabla() {
        List<Paciente> lista = gestorPacientes.ordenarPacientesAlbeticamente();
        tabla.setItems(FXCollections.observableArrayList(lista));
    }

    private void filtrar() {

        String texto = txtBuscar.getText();

        if (texto == null || texto.trim().isEmpty()) {
            refrescarTabla();

        } else {
            List<Paciente> filtrados = gestorPacientes.buscarPaciente(texto);
            tabla.setItems(FXCollections.observableArrayList(filtrados));
        }
    }

    private void mostrarFormulario(Paciente existente) {

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.initOwner(stage);
        dialogo.setTitle(existente == null ? "Registrar Paciente" : "Modificar Paciente");
        dialogo.setHeaderText(existente == null ? "Registro de ficha de paciente" : "Actualización de datos personales");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        TextField txtDni = new TextField(existente != null ? existente.getDni() : "");
        txtDni.setDisable(existente != null);
        TextField txtNombre = new TextField(existente != null ? existente.getNombreCompleto() : "");
        TextField txtApellido = new TextField(existente != null ? existente.getApellido() : "");
        DatePicker dpNacimiento = new DatePicker(existente != null ? existente.getFechaNacimiento() : LocalDate.of(1995, 1, 1));
        TextField txtObraSocial = new TextField(existente != null ? existente.getObraSocial() : "Particular");
        TextField txtTelefono = new TextField(existente != null ? existente.getTelefono() : "");
        TextField txtEmail = new TextField(existente != null ? existente.getEmail() : "");
        TextField txtDireccion = new TextField(existente != null ? existente.getDireccion() : "");

        grid.add(new Label("DNI:*"), 0, 0);
        grid.add(txtDni, 1, 0);
        grid.add(new Label("Fecha Nacimiento:*"), 2, 0);
        grid.add(dpNacimiento, 3, 0);
        grid.add(new Label("Nombre:*"), 0, 1);
        grid.add(txtNombre, 1, 1);
        grid.add(new Label("Apellido:*"), 2, 1);
        grid.add(txtApellido, 3, 1);
        grid.add(new Label("Obra Social:*"), 0, 2);
        grid.add(txtObraSocial, 1, 2);
        grid.add(new Label("Telefono:*"), 2, 2);
        grid.add(txtTelefono, 3, 2);
        grid.add(new Label("Email:*"), 0, 3);
        grid.add(txtEmail, 1, 3);
        grid.add(new Label("Direccion:*"), 2, 3);
        grid.add(txtDireccion, 3, 3);

        dialogo.getDialogPane().setContent(grid);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> respuesta = dialogo.showAndWait();

        if  (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                if (existente == null) {
                    gestorPacientes.registrarPaciente(txtDni.getText(), txtNombre.getText(), txtApellido.getText(),
                            txtTelefono.getText(), txtEmail.getText(), txtDireccion.getText(), dpNacimiento.getValue(), txtObraSocial.getText());

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente registrado correctamente");

                } else {
                    gestorPacientes.actualiuzarPaciente(existente.getIdPaciente(), txtNombre.getText(), txtApellido.getText(),
                            txtTelefono.getText(), txtEmail.getText(), txtDireccion.getText(), dpNacimiento.getValue(), txtObraSocial.getText());

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Datos del Paciente actualizados correctamente");
                }

                refrescarTabla();

            } catch (MedMetException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, ex.getMessage());
            }
        }
    }

    private void procesarBaja() {

        Paciente p =  tabla.getSelectionModel().getSelectedItem();

        if (p == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un paciente de la tabla");
            return;
        }

        Alert confirm =  new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Baja");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Está seguro de dar de baja al paciente " + p.getNombreCompleto() + "?");
        confirm.initOwner(stage);

        Optional <ButtonType> respuesta = confirm.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                gestorPacientes.procesarBajaPaciente(p.getIdPaciente());
                refrescarTabla();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente dado de baja correctamente");

            } catch (MedMetException ex) {
                mostrarAlerta(Alert.AlertType.ERROR,ex.getMessage());
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : "MedMet");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.initOwner(stage);
        alert.showAndWait();
    }
}
