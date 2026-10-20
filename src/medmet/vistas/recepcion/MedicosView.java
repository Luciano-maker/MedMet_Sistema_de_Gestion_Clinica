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
import javafx.util.StringConverter;
import medmet.excepciones.MedMetException;
import medmet.gestor.GestorEspecialidades;
import medmet.gestor.GestorMedicos;
import medmet.modelo.Especialidad;
import medmet.modelo.Medico;

import java.util.Optional;

// Subpantalla de la sección Medicos del Modulo de Administracion
public class MedicosView {

    private final Stage stage;
    private final GestorMedicos gestorMedicos;
    private final GestorEspecialidades gestorEspecialidades;
    private final TableView<Medico> tabla;
    private final TextField txtBuscar;

    public MedicosView(Stage presentStage) {

        this.gestorMedicos = new GestorMedicos();
        this.gestorEspecialidades = new GestorEspecialidades();
        this.stage = new Stage();
        this.stage.initOwner(presentStage);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.setTitle("MedMet | Sección de Médicos");

        VBox root = new VBox(12);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f4f6f8;");

        // Barra de busqueda
        HBox panelBusqueda = new HBox(10);
        panelBusqueda.setAlignment(Pos.CENTER_LEFT);
        txtBuscar = new TextField();
        txtBuscar.setPromptText("Ingresar DNI, apellido, matrícula o especialidad");
        txtBuscar.setPrefWidth(320);
        Button btnBuscar = new Button("Buscar");
        Button btnLimpiar = new Button("Ver todos");
        panelBusqueda.getChildren().addAll(new Label("Filtro:"), txtBuscar, btnBuscar, btnLimpiar);

        // Tabla de Medicos
        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Medico, String> colMatricula = new TableColumn<>("Matrícula");
        colMatricula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMatriculaProfesional()));
        colMatricula.setMinWidth(95);
        colMatricula.setPrefWidth(110);

        TableColumn<Medico, String> colNombre = new TableColumn<>("Médico");
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colNombre.setMinWidth(170);
        colNombre.setPrefWidth(200);

        TableColumn<Medico, String> colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDni()));
        colDni.setMinWidth(95);
        colDni.setPrefWidth(105);

        TableColumn<Medico, String> colEspecialidad = new TableColumn<>("Especialidad");
        colEspecialidad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEspecialidad()
                != null ? c.getValue().getEspecialidad().getNombre() : "Sin asignar"));
        colEspecialidad.setMinWidth(130);
        colEspecialidad.setPrefWidth(140);

        TableColumn<Medico, String> colTelefono = new TableColumn<>("Telefono");
        colTelefono.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelefono()));
        colTelefono.setMinWidth(110);

        TableColumn<Medico, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colEmail.setMinWidth(150);
        colEmail.setPrefWidth(180);

        tabla.getColumns().addAll(colMatricula,  colNombre, colDni, colEspecialidad, colTelefono, colEmail);

        // Botones de Accion
        HBox panelBotones = new HBox(10);
        panelBotones.setAlignment(Pos.CENTER_RIGHT);
        Button btnNuevo = new Button("Registrar Médico");
        btnNuevo.setStyle("fx-font-weight: bold;");
        Button btnModificar = new Button("Modificar");
        Button btnBaja = new Button("Dar de Baja");
        Button btnCerrar = new  Button("Cerrar");
        panelBotones.getChildren().addAll(btnNuevo, btnModificar, btnBaja, btnCerrar);

        root.getChildren().addAll(panelBusqueda, tabla,panelBotones);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        // Eventos
        btnBuscar.setOnAction(e -> filtrar());
        btnLimpiar.setOnAction(e -> {
            txtBuscar.clear();
            refrescarTabla();
        });
        btnNuevo.setOnAction(e -> mostrarFormulario(null));
        btnModificar.setOnAction(e -> {
            Medico m = tabla.getSelectionModel().getSelectedItem();
            if(m != null){
                mostrarFormulario(m);
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un médico");
            }
        });
        btnBaja.setOnAction(e -> procesarBaja());
        btnCerrar.setOnAction(e -> stage.close());

        refrescarTabla();
        stage.setScene(new Scene(root, 800, 460));
    }

    public void show() {
        stage.showAndWait();
    }

    private void refrescarTabla(){
        tabla.setItems(FXCollections.observableArrayList(gestorMedicos.listarMedicosActivos()));
    }

    private void filtrar(){
        tabla.setItems(FXCollections.observableArrayList(gestorMedicos.buscarMedicos(txtBuscar.getText())));
    }

    private void mostrarFormulario(Medico existente) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.initOwner(stage);
        dialogo.setTitle(existente == null ? "Registrar Médico" : "Modificar Médico");
        dialogo.setHeaderText(existente == null ? "Ingreso de nuevo profesional a cartilla" : "Actualización de datos profesionales");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        TextField txtDni = new TextField(existente != null ? existente.getDni() : "");
        txtDni.setDisable(existente != null); // El DNI no se edita en modificaciones
        TextField txtMatricula = new TextField(existente != null ? existente.getMatriculaProfesional() : "");
        TextField txtNombre = new TextField(existente != null ? existente.getNombre() : "");
        TextField txtApellido = new TextField(existente != null ? existente.getApellido() : "");
        TextField txtTelefono = new TextField(existente != null ? existente.getTelefono() : "");
        TextField txtEmail = new TextField(existente != null ? existente.getEmail() : "");
        TextField txtDireccion = new TextField(existente != null ? existente.getDireccion() : "");

        ComboBox<Especialidad> cbEsp = new ComboBox<>();
        cbEsp.setItems(FXCollections.observableArrayList(gestorEspecialidades.listarEspecialidadesActivas()));
        cbEsp.setConverter(new StringConverter<>() {
            @Override public String toString(Especialidad e) {
                return e != null ? e.getNombre() : "";
            }
            @Override public Especialidad fromString(String s) {
                return null;
            }
        });

        // Preseleccionar la especialidad si estamos editando
        if (existente != null && existente.getEspecialidad() != null) {
            int idBuscado = existente.getEspecialidad().getIdEspecialidad();
            for (Especialidad esp : cbEsp.getItems()) {
                if (esp.getIdEspecialidad() == idBuscado) {
                    cbEsp.setValue(esp);
                    break;
                }
            }
        } else if (!cbEsp.getItems().isEmpty()) {
            cbEsp.getSelectionModel().selectFirst();
        }

        grid.add(new Label("DNI: " + (existente != null ? "(fijo)" : "*")), 0, 0);
        grid.add(txtDni, 1, 0);

        grid.add(new Label("Matrícula:*"), 2, 0);
        grid.add(txtMatricula, 3, 0);

        grid.add(new Label("Nombre:*"), 0, 1);
        grid.add(txtNombre, 1, 1);

        grid.add(new Label("Apellido:*"), 2, 1);
        grid.add(txtApellido, 3, 1);

        grid.add(new Label("Especialidad:*"), 0, 2);
        grid.add(cbEsp, 1, 2);

        grid.add(new Label("Teléfono:"), 2, 2);
        grid.add(txtTelefono, 3, 2);

        grid.add(new Label("Email:"), 0, 3);
        grid.add(txtEmail, 1, 3);

        grid.add(new Label("Dirección:"), 2, 3);
        grid.add(txtDireccion, 3, 3);

        dialogo.getDialogPane().setContent(grid);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> res = dialogo.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            try {
                if (existente == null) {
                    gestorMedicos.registrarMedico(txtDni.getText(), txtNombre.getText(), txtApellido.getText(), txtTelefono.getText(),
                            txtEmail.getText(), txtDireccion.getText(), txtMatricula.getText(), cbEsp.getValue());

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Médico registrado correctamente");

                } else {
                    gestorMedicos.modificarMedico(existente.getIdMedico(), txtNombre.getText(), txtApellido.getText(),
                            txtTelefono.getText(), txtEmail.getText(), txtDireccion.getText(), txtMatricula.getText(), cbEsp.getValue());

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Datos del médico modificados correctamente");
                }

                refrescarTabla();

            } catch (MedMetException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, ex.getMessage());
            }
        }
    }

    private void procesarBaja() {

        Medico m = tabla.getSelectionModel().getSelectedItem();

        if (m == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione un profesional a dar de baja");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Dar de baja a '" + m.getNombreCompleto() + "'?",  ButtonType.YES, ButtonType.NO);
        confirm.initOwner(stage);
        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                try {
                    gestorMedicos.procesarBajaMedico(m.getIdMedico());
                    refrescarTabla();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Profesional dado de bajja correctamente");

                } catch (MedMetException ex) {
                    mostrarAlerta(Alert.AlertType.ERROR, ex.getMessage());
                }
            }
        });
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
