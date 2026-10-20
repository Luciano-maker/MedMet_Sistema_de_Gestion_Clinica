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
import medmet.gestor.GestorEspecialidades;
import medmet.modelo.Especialidad;

import java.util.Optional;

// Pantalla de la sección Especialidades del Modulo de Administracion
public class EspecialidadesView {

    private final Stage stage;
    private final GestorEspecialidades gestorEspecialidades;
    private final TableView<Especialidad> tabla;
    private final TextField txtBuscar;

    public EspecialidadesView(Stage parentStage) {

        this.gestorEspecialidades = new GestorEspecialidades();
        this.stage = new Stage();
        this.stage.initOwner(parentStage);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.setTitle("MedMet | Sección de Especialidades Médicas");

        VBox root = new VBox(12);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f4f6f8;");

        // Barra de busqueda
        HBox panelBusqueda = new HBox(10);
        panelBusqueda.setAlignment(Pos.CENTER_LEFT);
        txtBuscar = new TextField();
        txtBuscar.setPromptText("Ingresar nombre de especialidad...");
        txtBuscar.setPrefWidth(280);
        Button btnBuscar = new Button("Buscar");
        Button btnLimpiar = new Button("Ver Todas");
        panelBusqueda.getChildren().addAll(new Label("Filtro:"), txtBuscar, btnBuscar, btnLimpiar);

        // Tabla de especialidades
        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Especialidad, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getIdEspecialidad())));
        colId.setPrefWidth(60);
        colId.setMinWidth(50);
        colId.setMaxWidth(70);

        TableColumn<Especialidad, String> colNombre = new TableColumn<>("Especialidad");
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colNombre.setPrefWidth(220);
        colNombre.setMinWidth(180);

        TableColumn<Especialidad, String> colDescripcion = new TableColumn<>("Descripcion");
        colDescripcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));
        colDescripcion.setPrefWidth(320);
        colDescripcion.setMinWidth(220);

        TableColumn<Especialidad, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().isActivo() ? "Activo" : "Inactivo"));
        colEstado.setPrefWidth(90);
        colEstado.setMinWidth(80);
        colEstado.setMaxWidth(100);

        tabla.getColumns().addAll(colId, colNombre, colDescripcion, colEstado);

        // Botones de Accion
        HBox panelBotones = new HBox(10);
        panelBotones.setAlignment(Pos.CENTER_RIGHT);
        Button btnNuevo = new Button("Registrar Especialidad");
        btnNuevo.setStyle("-fx-font-weight: bold;");
        Button btnModificar = new Button("Modificar");
        Button btnBaja = new Button("Dar de Baja");
        Button btnCerrar = new Button("Cerrar");
        panelBotones.getChildren().addAll(btnNuevo, btnModificar, btnBaja, btnCerrar);

        root.getChildren().addAll(panelBusqueda, tabla,panelBotones);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        // Eventos
        btnBuscar.setOnAction(e -> filtrar());
        btnLimpiar.setOnAction(e -> {
            txtBuscar.clear();
            refrescarTabla();
        });
        btnNuevo.setOnAction(e -> mostrarFormularioRegistro(null));
        btnModificar.setOnAction(e -> {
            Especialidad selec = tabla.getSelectionModel().getSelectedItem();
            if (selec != null) {
                mostrarFormularioRegistro(selec);
            } else mostrarAlerta(Alert.AlertType.WARNING, "Seleccione una especialidad");
        });
        btnBaja.setOnAction(e -> procesarBaja());
        btnCerrar.setOnAction(e -> stage.close());

        refrescarTabla();
        stage.setScene(new Scene(root, 600, 420));
    }

    public void show() {
        stage.show();
    }

    private void refrescarTabla() {
        tabla.setItems(FXCollections.observableList(gestorEspecialidades.listarEspecialidadesActivas()));
    }

    private void filtrar() {
        tabla.setItems(FXCollections.observableArrayList(gestorEspecialidades.buscarEspecialidades(txtBuscar.getText())));
    }

    private void mostrarFormularioRegistro(Especialidad existente) {

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.initOwner(stage);
        dialogo.setTitle(existente == null ? "Nueva Especialidad" : "Modificar Especialidad");
        dialogo.setHeaderText(existente == null ? "Registro de nueva especialidad médica" : "Actualización de datos de especialidad" );

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        TextField txtNombre = new TextField(existente != null ? existente.getNombre() : "");
        TextArea txtDescripcion = new TextArea(existente != null ? existente.getDescripcion() : "");
        txtDescripcion.setPrefRowCount(3);

        grid.add(new Label("Nombre:*"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Descripcion:*"), 0, 1);
        grid.add(txtDescripcion, 1, 1);

        dialogo.getDialogPane().setContent(grid);
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> respuesta = dialogo.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            try {
                if (existente == null) {
                    gestorEspecialidades.registrarEspecialidad(txtNombre.getText(), txtDescripcion.getText());
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Especialidad Registrada correctamente");

                } else {
                    gestorEspecialidades.actulizarEspecialidad(existente.getIdEspecialidad(), txtNombre.getText(), txtDescripcion.getText());
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Especialidad actualizada correctamente");
                }

                refrescarTabla();

            } catch (MedMetException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, ex.getMessage());
            }
        }
    }

    private void procesarBaja() {

        Especialidad selec = tabla.getSelectionModel().getSelectedItem();

        if (selec == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleccione la especialidad a dar de baja");
            return;
        }

        Alert confirm =  new Alert(Alert.AlertType.CONFIRMATION, "¿Dar de Baja '" + selec.getNombre() + "'?", ButtonType.YES, ButtonType.NO);
        confirm.initOwner(stage);
        confirm.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.YES) {
                try {
                    gestorEspecialidades.eliminarEspecialidad(selec.getIdEspecialidad());
                    refrescarTabla();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Especialidad dada de Baja correctamente");

                } catch (MedMetException ex) {
                    mostrarAlerta(Alert.AlertType.ERROR, ex.getMessage());
                }
            }
        });
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo, mensaje);
        alert.initOwner(stage);
        alert.showAndWait();
    }

}
