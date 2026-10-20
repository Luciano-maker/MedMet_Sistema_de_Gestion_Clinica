package medmet.vistas.recepcion;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import medmet.modelo.Especialidad;
import medmet.modelo.Medico;
import medmet.modelo.Turno;

import java.time.LocalDate;

public class RecepcionistaMainView {

    private final BorderPane root;

    // Menu Superior
    private MenuBar menuBar;
    private MenuItem miCerrarSesion, miSalir;
    private MenuItem miNuevoPaciente, miListarPacientes;
    private MenuItem miNuevoMedico,  miListarMedicos;
    private MenuItem miNuevaEspecialidad, miListarEspecialidades;
    private MenuItem miAgendarTurno, miVerAgenda;
    private MenuItem miRegistrarPago, miListarPagos;
    private MenuItem miAcercaDe;

    // Filtros de Agenda
    private DatePicker dpFecha;
    private ComboBox<Especialidad> cbEspecialidad;
    private ComboBox<Medico> cbMedico;
    private Button btnBuscar;
    private Button btnLimpiarFiltros;

    // Tabla Interactiva
    private TableView<Turno> tablaTurnos;
    private TableColumn<Turno, String> colHora;
    private TableColumn<Turno, String> colPaciente;
    private TableColumn<Turno, String> colDni;
    private TableColumn<Turno, String> colMedico;
    private TableColumn<Turno, String> colEstadoCita;
    private TableColumn<Turno, String> colEstadoPago;

    // Botones de accion
    private Button btnNuevoPaciente;
    private Button btnAgendarTurno;
    private Button btnRegistrarPago;
    private Button btnReprogramar;
    private Button btnCancelarTurno;
    private Button btnConfirmarLlegada;

    // Barra Inferior
    private Label lblEstadoSeleccion;

    public RecepcionistaMainView() {

        root = new BorderPane();
        root.setStyle("-fx-background-color: #f4f6f8");

        construirMenuSuperior();        // Barra de Menu Superior
        root.setTop(menuBar);

        VBox panelCentral = new VBox(12);       // Panel Central
        panelCentral.setPadding(new Insets(15,20,10,20));

        VBox panelFiltros = construirPanelFiltros();
        construirTablaTurnos();

        panelCentral.getChildren().addAll(panelFiltros, tablaTurnos);
        VBox.setVgrow(tablaTurnos, Priority.ALWAYS);
        root.setCenter(panelCentral);

        VBox panelInferior = new VBox(8);       // Panel Inferior
        panelInferior.setPadding(new Insets(5,20,10,20));

        HBox barraAcciones = construirBotonesAccion();
        lblEstadoSeleccion = new Label("Seleccionar un turno para comenzar");
        lblEstadoSeleccion.setStyle("-fx-text-fill: #555555; -fx-font-size: 11px; -fx-padding: 3 0 0 5;");

        panelInferior.getChildren().addAll(barraAcciones, new Separator(),lblEstadoSeleccion);
        root.setBottom(panelInferior);
    }

    private void construirMenuSuperior() {

        menuBar = new MenuBar();

        Menu menuSesion = new Menu("Sesión");
        miCerrarSesion = new MenuItem("Cerrar Sesión");
        miSalir = new MenuItem("Salir");
        menuSesion.getItems().addAll(miCerrarSesion, new SeparatorMenuItem(), miSalir);

        Menu menuPacientes = new Menu("Pacientes");
        miListarPacientes = new MenuItem("Gestionar Pacientes");
        menuPacientes.getItems().addAll(miListarPacientes);

        Menu menuMedicos = new Menu("Medicos");
        miListarMedicos = new MenuItem("Gestionar Médicos");
        menuMedicos.getItems().addAll(miListarMedicos);

        Menu menuEspecialidades = new Menu("Especialidades");
        miListarEspecialidades = new MenuItem("Gestionar Especialidades");
        menuEspecialidades.getItems().addAll(miListarEspecialidades);

        Menu menuTurnos = new Menu("Turnos");
        miVerAgenda = new  MenuItem("Actualizar Agenda");
        menuTurnos.getItems().addAll(miVerAgenda);

        Menu menuAyuda = new Menu("Ayuda");
        miAcercaDe = new MenuItem("Acerca de MedMet");
        menuAyuda.getItems().addAll(miAcercaDe);

        menuBar.getMenus().addAll(menuSesion, menuPacientes, menuMedicos, menuEspecialidades, menuTurnos, menuAyuda);
    }

    private VBox construirPanelFiltros() {

        VBox panel = new VBox(8);
        panel.setPadding(new Insets(10,15,10,15));
        panel.setStyle("-fx-border-color: #c8c8c8; -fx-border-width: 1; -fx-border-radius: 4; -fx-background-color: #ffffff");

        Label lblTitulo = new Label("Filtros de Agenda Médica");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        HBox filaControles = new HBox(12);
        filaControles.setAlignment(Pos.CENTER_LEFT);

        Label lblFecha = new Label("Fecha:");
        dpFecha = new DatePicker(LocalDate.of(2026, 10, 19));
        dpFecha.setPrefWidth(130);

        Label lblEspecialidad = new Label("Especialidad:");
        cbEspecialidad = new ComboBox<>();
        cbEspecialidad.setPromptText("Todas");
        cbEspecialidad.setPrefWidth(160);

        Label lblMedico = new Label("Medico:");
        cbMedico = new ComboBox<>();
        cbMedico.setPromptText("Todos");
        cbMedico.setPrefWidth(180);

        btnBuscar = new Button("Buscar");
        btnBuscar.setStyle("-fx-font-weight: bold; -fx-padding: 5 15;");

        btnLimpiarFiltros = new Button("Reestablecer");

        filaControles.getChildren().addAll(lblFecha, dpFecha, lblEspecialidad, cbEspecialidad, lblMedico, cbMedico, btnBuscar, btnLimpiarFiltros);
        panel.getChildren().addAll(lblTitulo, filaControles);
        return panel;
    }

    private void construirTablaTurnos() {

        tablaTurnos = new TableView<>();
        tablaTurnos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colHora = new TableColumn<>("Hora");
        colHora.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHora().toString()));
        colHora.setMinWidth(70);
        colHora.setMaxWidth(90);

        colPaciente = new TableColumn<>("Paciente");
        colPaciente.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPaciente().getApellido() + ", " + data.getValue().getPaciente().getNombre()));
        colPaciente.setMinWidth(180);

        colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPaciente().getDni()));
        colDni.setMinWidth(100);
        colDni.setMaxWidth(130);

        colMedico = new TableColumn<>("Medico");
        colMedico.setCellValueFactory(data -> new SimpleStringProperty("Dr(a). " + data.getValue().getMedico().getApellido()));
        colMedico.setMinWidth(160);

        colEstadoCita = new TableColumn<>("Estado Cita");
        colEstadoCita.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstado()));
        colEstadoCita.setMinWidth(100);

        colEstadoPago = new  TableColumn<>("Estado Pago");
        colEstadoPago.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEstadoPago()));
        colEstadoPago.setMinWidth(100);

        tablaTurnos.getColumns().addAll(colHora, colPaciente, colDni, colMedico, colEstadoCita, colEstadoPago);
    }

    private HBox construirBotonesAccion() {

        HBox panel = new HBox(10);
        panel.setAlignment(Pos.CENTER_LEFT);

        btnNuevoPaciente = new Button("Nuevo Paciente");

        Region separador = new Region();
        HBox.setHgrow(separador, Priority.ALWAYS);

        btnConfirmarLlegada = new Button("Confirmar Llegada");
        btnAgendarTurno = new Button("Agendar Turno");
        btnAgendarTurno.setStyle("-fx-font-weight: bold;");

        btnRegistrarPago =  new Button("Registrar Pago");
        btnReprogramar = new Button("Reprogramar");
        btnCancelarTurno = new Button("Cancelar Turno");

        panel.getChildren().addAll(btnNuevoPaciente, separador, btnConfirmarLlegada,  btnAgendarTurno, btnRegistrarPago, btnReprogramar,  btnCancelarTurno);
        return panel;
    }

    // Metodos GETTER de componentes
    public Parent getRoot() {
        return root;
    }

    public DatePicker getDpFecha() {
        return dpFecha;
    }

    public ComboBox<Especialidad> getCbEspecialidad() {
        return cbEspecialidad;
    }

    public ComboBox<Medico> getCbMedico() {
        return cbMedico;
    }

    public Button getBtnBuscar() {
        return btnBuscar;
    }

    public Button getBtnLimpiarFiltros() {
        return btnLimpiarFiltros;
    }

    public TableView<Turno> getTablaTurnos() {
        return tablaTurnos;
    }

    public Button getBtnNuevoPaciente() {
        return btnNuevoPaciente;
    }

    public Button getBtnConfirmarLlegada() {
        return btnConfirmarLlegada;
    }

    public Button getBtnAgendarTurno() {
        return btnAgendarTurno;
    }

    public Button getBtnRegistrarPago() {
        return btnRegistrarPago;
    }

    public Button getBtnReprogramar() {
        return btnReprogramar;
    }

    public Button getBtnCancelarTurno() {
        return btnCancelarTurno;
    }

    public Label getLblEstadoSeleccion() {
        return lblEstadoSeleccion;
    }

    // Metodos GETTER de menú
    public MenuItem getMiCerrarSesion() {
        return miCerrarSesion;
    }

    public MenuItem getMiSalir() {
        return miSalir;
    }

    public MenuItem getMiNuevoPaciente() {
        return miNuevoPaciente;
    }

    public MenuItem getMiListarPacientes() {
        return miListarPacientes;
    }

    public MenuItem getMiNuevoMedico() {
        return miNuevoMedico;
    }

    public MenuItem getMiListarMedicos() {
        return miListarMedicos;
    }

    public MenuItem getMiNuevaEspecialidad() {
        return miNuevaEspecialidad;
    }

    public MenuItem getMiListarEspecialidades() {
        return miListarEspecialidades;
    }

    public MenuItem getMiAgendarTurno() {
        return miAgendarTurno;
    }

    public MenuItem getMiVerAgenda() {
        return miVerAgenda;
    }

    public MenuItem getMiRegistrarPago() {
        return miRegistrarPago;
    }

    public MenuItem getMiListarPagos() {
        return miListarPagos;
    }

    public MenuItem getMiAcercaDe() {
        return miAcercaDe;
    }
}
