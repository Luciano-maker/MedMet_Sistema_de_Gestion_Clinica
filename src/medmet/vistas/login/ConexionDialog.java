package medmet.vistas.login;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import medmet.conexion.ConexionBD;

public class ConexionDialog {

    private final Stage stage;
    private boolean conexionEstablecida = false;

    public ConexionDialog(Stage parentstage) {
        this.stage = new Stage();
        if (parentstage != null && parentstage.isShowing()) {
            this.stage.initOwner(parentstage);
        }
        this.stage.initModality(Modality.APPLICATION_MODAL);
        this.stage.setTitle("MedMet | Configuración de Base de Datos");
        this.stage.setResizable(false);

        VBox root = new VBox(12);

        root.setPadding(new Insets(18));
        root.setStyle("-fx-background-color: #f7f9fa;");

        Label lblTitulo = new Label("Parámetros de Conexión MySQL");
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 11pt; -fx-text-fill: #2c3e50;");

        Label lblDescripcion = new Label("Ajuste los datos según su motor Local");
        lblDescripcion.setStyle("-fx-font-size: 9pt; -fx-text-fill: #7f8c8d;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 0, 0, 0));

        TextField txtHost = new TextField(ConexionBD.getHost());
        TextField txtPuerto = new TextField(ConexionBD.getPuerto());
        TextField txtDB = new TextField(ConexionBD.getBaseDatos());
        TextField txtUser = new TextField(ConexionBD.getUsuario());
        PasswordField txtPass = new PasswordField();
        txtPass.setText(ConexionBD.getContrasenia());

        grid.add(new Label("Servidor (Host):"), 0, 0); grid.add(txtHost, 1, 0);
        grid.add(new Label("Puerto:"), 0, 1);           grid.add(txtPuerto, 1, 1);
        grid.add(new Label("Base de Datos:"), 0, 2);     grid.add(txtDB, 1, 2);
        grid.add(new Label("Usuario:"), 0, 3);     grid.add(txtUser, 1, 3);
        grid.add(new Label("Contraseña:"), 0, 4);        grid.add(txtPass, 1, 4);

        Label lblMensaje = new Label();
        lblMensaje.setStyle("-fx-font-size: 9.5pt; -fx-font-weight: bold;");

        HBox panelBotones = new HBox(10);
        panelBotones.setAlignment(Pos.CENTER_RIGHT);

        Button btnProbar = new Button("Probar Conexión");
        Button btnGuardar = new Button("Guardar y Continuar");
        btnGuardar.setStyle("-fx-font-weight: bold;");

        panelBotones.getChildren().addAll(btnProbar, btnGuardar);

        btnProbar.setOnAction(e -> {
            boolean ok = ConexionBD.conexionManual(txtHost.getText(), txtPuerto.getText(), txtDB.getText(), txtUser.getText(), txtPass.getText());

            if (ok) {
                lblMensaje.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                lblMensaje.setText("Conexión exitosa con " + ConexionBD.getBaseDatos() + "!!");

            } else {
                lblMensaje.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
                lblMensaje.setText("Error: No se pudo conectar con la Base de Datos. Verifique las credenciales");
            }
        });

        btnGuardar.setOnAction(e -> {
            boolean ok = ConexionBD.conexionManual(txtHost.getText(), txtPuerto.getText(), txtDB.getText(), txtUser.getText(), txtPass.getText());

            if (ok) {
                ConexionBD.guardarConfiguracion(txtHost.getText(), txtPuerto.getText(), txtDB.getText(), txtUser.getText(), txtPass.getText());
                conexionEstablecida = true;
                stage.close();

            } else {
                Alert alerta = new Alert(Alert.AlertType.ERROR, "No se pudo conectar con el servidor\n" +
                        "Verifique que el servicio esté inciado y que la Bsae de Datos exista", ButtonType.OK);

                alerta.initOwner(stage);
                alerta.showAndWait();
            }
        });

        root.getChildren().addAll(lblTitulo, lblDescripcion, grid, lblMensaje, panelBotones);
        stage.setScene(new Scene(root, 380, 330));
    }

    public boolean showAndWait() {
        stage.showAndWait();
        return conexionEstablecida;
    }
}
