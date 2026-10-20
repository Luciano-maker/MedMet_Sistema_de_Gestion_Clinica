package medmet.vistas.medico;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import medmet.modelo.Turno;

import java.time.LocalDate;

// Pantalla principal del Módulo de Consultorio
public class MedicoMainView {

    private final BorderPane root;

    private MenuBar menuBar;
    private MenuItem miCerrarSesion, miSalir;
    private MenuItem miBuscarHC;

    private Label lblInfoMedico;
    private DatePicker dpFecha;
    private ComboBox<String> cbFiltroEstado;
    private Button btnBuscar;

    private TableView<Turno> tablaTurnos;

    private Button btnAtender;
    private Button btnRefrescar;
    private Label lblEstadoAgenda;

    public MedicoMainView() {

        root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6f8;");

        construirMenu();        // Menu superior
        root.setTop(menuBar);

        VBox panelCentral = new VBox(12);       // Información del Profesional, filtros y tabla
        panelCentral.setPadding(new Insets(15, 20, 10, 20));

        VBox panelCabecera = construirPanelCabecera();
        construirTablaTurnos();

        panelCentral.getChildren().addAll(panelCabecera, tablaTurnos);
        VBox.setVgrow(tablaTurnos, Priority.ALWAYS);
        root.setCenter(panelCentral);

        VBox panelInferior = new VBox(8);       // Botones de accion
        panelInferior.setPadding(new Insets(5, 20, 12, 20));

        HBox barraAcciones = construirBotones();
        lblEstadoAgenda = new Label("Seleccione un paciente en espera");
        lblEstadoAgenda.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px;");

        panelInferior.getChildren().addAll(barraAcciones, new Separator(), lblEstadoAgenda);
        root.setBottom(panelInferior);
    }

    private void construirMenu() {

        menuBar = new MenuBar();

        Menu menuArchivo = new Menu("Sesión");
        miCerrarSesion = new MenuItem("Cerrar Sesión");
        miSalir = new MenuItem("Salir");
        menuArchivo.getItems().addAll(miCerrarSesion, new SeparatorMenuItem(), miSalir);

        Menu menuHC = new Menu("Historia Clínica");
        miBuscarHC = new MenuItem("Ver expedientes clinicos");
        menuHC.getItems().add(miBuscarHC);

        menuBar.getMenus().addAll(menuArchivo, menuHC);
    }

    private VBox construirPanelCabecera() {

        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10, 15, 10, 15));
        panel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #c8c8c8; -fx-border-radius: 4;");

        lblInfoMedico = new Label("Profesional: Cargando datos...");
        lblInfoMedico.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        HBox filaFiltros = new HBox(12);
        filaFiltros.setAlignment(Pos.CENTER_LEFT);

        Label lblFecha = new Label("Fecha:");
        dpFecha = new DatePicker(LocalDate.of(2026, 10, 19));
        dpFecha.setPrefWidth(130);

        Label lblEstado = new Label("Estado:");
        cbFiltroEstado = new ComboBox<>();
        cbFiltroEstado.getItems().addAll("Todos", "En espera", "Pendiente", "Finalizado");
        cbFiltroEstado.setValue("Todos");
        cbFiltroEstado.setPrefWidth(120);

        btnBuscar = new Button("Filtrar Agenda");
        btnBuscar.setStyle("-fx-font-weight: bold;");

        filaFiltros.getChildren().addAll(lblFecha, dpFecha, lblEstado, cbFiltroEstado, btnBuscar);
        panel.getChildren().addAll(lblInfoMedico, filaFiltros);
        return panel;
    }

    private void construirTablaTurnos() {

        tablaTurnos = new TableView<>();
        tablaTurnos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Turno, String> colHora = new TableColumn<>("Hora");
        colHora.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHora().toString()));
        colHora.setMinWidth(70);
        colHora.setMaxWidth(85);

        TableColumn<Turno, String> colPaciente = new TableColumn<>("Paciente");
        colPaciente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaciente().getNombreCompleto()));
        colPaciente.setMinWidth(180);

        TableColumn<Turno, String> colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaciente().getDni()));
        colDni.setMinWidth(95);
        colDni.setMaxWidth(110);

        TableColumn<Turno, String> colObraSocial = new TableColumn<>("Cobertura");
        colObraSocial.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPaciente().getObraSocial()));
        colObraSocial.setMinWidth(110);

        TableColumn<Turno, String> colEstado = new TableColumn<>("Estado Cita");
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado()));
        colEstado.setMinWidth(100);

        tablaTurnos.getColumns().addAll(colHora, colPaciente, colDni, colObraSocial, colEstado);
    }

    private HBox construirBotones() {

        HBox panel = new HBox(12);
        panel.setAlignment(Pos.CENTER_LEFT);

        btnAtender = new Button("Atender Paciente / Iniciar Consulta");
        btnAtender.setStyle("-fx-font-weight: bold; -fx-cursor: hand;");

        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);

        btnRefrescar = new Button("Actualizar Agenda");

        panel.getChildren().addAll(btnAtender, esp, btnRefrescar);
        return panel;
    }

    // Metodos GETTER
    public Parent getRoot() {
        return root;
    }

    public Label getLblInfoMedico() {
        return lblInfoMedico;
    }

    public DatePicker getDpFecha() {
        return dpFecha;
    }

    public ComboBox<String> getCbFiltroEstado() {
        return cbFiltroEstado;
    }

    public Button getBtnBuscar() {
        return btnBuscar;
    }

    public TableView<Turno> getTablaTurnos() {
        return tablaTurnos;
    }

    public Button getBtnAtender() {
        return btnAtender;
    }

    public Button getBtnRefrescar() {
        return btnRefrescar;
    }

    public Label getLblEstadoAgenda() {
        return lblEstadoAgenda;
    }

    public MenuItem getMiCerrarSesion() {
        return miCerrarSesion;
    }

    public MenuItem getMiSalir() {
        return miSalir;
    }

    public MenuItem getMiBuscarHC() {
        return miBuscarHC;
    }
}
