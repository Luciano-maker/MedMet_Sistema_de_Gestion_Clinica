package medmet.vistas.recepcion;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import medmet.excepciones.MedMetException;
import medmet.gestor.GestorPagos;
import medmet.modelo.Pago;
import medmet.modelo.Turno;

public class RegistrarPagoDialogo {

    private final Stage stage;
    private final GestorPagos gestorPagos;
    private final Turno turno;

    private final TextField txtMonto;
    private final ComboBox<String> cbMetodo;
    private boolean pagoProcesado = false;

    public RegistrarPagoDialogo(Stage parentStage, Turno turno) {

        this.turno = turno;
        this.gestorPagos = new GestorPagos();
        this.stage = new Stage();
        this.stage.initOwner(parentStage);
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.setTitle("MedMet | Registrar Pago de Turno #" + turno.getIdTurno());

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f4f6f8;");

        VBox panelResumen = new VBox(6);
        panelResumen.setPadding(new Insets(10));
        panelResumen.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d0d0d0; -fx-border-radius: 4;");

        Label lblTitulo = new Label("Detalle del Turno a Pagar");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

        Label lblPaciente = new Label("Paciente: " + turno.getPaciente().getNombreCompleto() + " (" + turno.getPaciente().getObraSocial() + ")");
        Label lblMedico = new Label("Médico: Dr(a). " + turno.getMedico().getApellido());
        Label lblFechaHora = new Label("Fecha/Hora: " + turno.getFecha() + " a las " + turno.getHora() + "hs");
        panelResumen.getChildren().addAll(lblTitulo, lblPaciente, lblMedico, lblFechaHora);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        txtMonto = new TextField("12000.00");
        txtMonto.setPrefWidth(180);

        cbMetodo = new ComboBox<>();
        cbMetodo.getItems().addAll("Efectivo", "Débito", "Transferencia", "Crédito");
        cbMetodo.setValue("Efectivo");
        cbMetodo.setPrefWidth(180);

        grid.add(new Label("Importe ($):*"), 0, 0);
        grid.add(txtMonto, 1, 0);
        grid.add(new Label("Método de Pago:*"), 0, 1);
        grid.add(cbMetodo, 1, 1);

        HBox panelBotones = new HBox(10);
        panelBotones.setAlignment(Pos.CENTER_RIGHT);
        Button btnPagar = new Button("Confirmar Pago");
        btnPagar.setStyle("-fx-font-weight: bold;");
        Button btnCancelar = new Button("Cancelar");
        panelBotones.getChildren().addAll(btnPagar, btnCancelar);

        root.getChildren().addAll(panelResumen, grid, panelBotones);

        btnPagar.setOnAction(e -> procesarPago());
        btnCancelar.setOnAction(e -> stage.close());

        stage.setScene(new Scene(root, 420, 270));
        stage.setResizable(false);
    }

    private void procesarPago() {
        try {
            double monto = Double.parseDouble(txtMonto.getText().trim());
            String metodo = cbMetodo.getValue();

            Pago pago = gestorPagos.registrarPago(turno.getIdTurno(), monto, metodo);

            pagoProcesado = true;
            Alert exito = new Alert(Alert.AlertType.INFORMATION);
            exito.initOwner(stage);
            exito.setHeaderText("Pago Registrado");
            exito.setHeaderText("Comprobante de Pago #" + pago.getIdPago());
            exito.setContentText(String.format("Monto: $%.2fnMétodo: %s%nFecha: %s%nEstado: PAGADO", pago.getMonto(), pago.getMetodoPago(), pago.getFechaHora()));
            exito.showAndWait();

            stage.close();

        } catch (NumberFormatException ex) {
            mostrarAlerta("El importe ingresado no es valido");
        } catch (MedMetException ex) {
            mostrarAlerta(ex.getMessage());
        }
    }

    public boolean showAndWait() {
        stage.showAndWait();
        return pagoProcesado;
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR, mensaje);
        alert.initOwner(stage);
        alert.showAndWait();
    }
}
