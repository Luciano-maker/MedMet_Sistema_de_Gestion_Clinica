package medmet.vistas;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

// Interfaz visual del Login de la aplicacion
public class LoginView {

    private final VBox root;
    private final RadioButton rbRecepcionista;
    private final RadioButton rbMedico;
    private final ToggleGroup grupoPerfil;
    private final TextField txtUsuario;
    private final PasswordField txtPassword;
    private final Button btnIniciarSesion;
    private final Label lblMensajeError;
    private final Label lblEstadoConexion;

    // Constructor
    public LoginView() {

        root = new VBox(15);
        root.setPadding(new Insets(20,25,15,25));
        root.setStyle("-fx-background-color: #f0f0f0;");

        // Contenedor de Identificacion de Usuario
        VBox panelIdentificacion = new VBox(12);
        panelIdentificacion.setPadding(new Insets(15));
        panelIdentificacion.setStyle("-fx-border-color: #b0b0b0; " + "-fx-border-width: 1; " + "-fx-border-radius: 4; " +
                "-fx-background-color: #ffffff");

        Label lblTituloPanel = new Label("Identificación de Usuario");
        lblTituloPanel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-size: 13px;");

        // Seleccion de Perfil
        HBox filaPerfil = new HBox(15);
        filaPerfil.setAlignment(Pos.CENTER_LEFT);
        Label lblPerfil = new Label("Perfil");
        lblPerfil.setPrefWidth(90);
        lblPerfil.setStyle("-fx-font-weight: bold");

        grupoPerfil = new ToggleGroup();
        rbRecepcionista = new RadioButton("Recepcionista");
        rbRecepcionista.setToggleGroup(grupoPerfil);
        rbRecepcionista.setSelected(true);

        rbMedico = new RadioButton("Medico");
        rbMedico.setToggleGroup(grupoPerfil);

        filaPerfil.getChildren().addAll(lblPerfil, rbRecepcionista, rbMedico);

        // Campo Usuario
        HBox filaUsuario = new HBox(10);
        filaUsuario.setAlignment(Pos.CENTER_LEFT);
        Label lblUsuario = new Label("Usuario");
        lblUsuario.setPrefWidth(90);
        lblUsuario.setStyle("-fx-font-weight: bold");

        txtUsuario = new TextField();
        txtUsuario.setPrefWidth(220);
        txtUsuario.setPromptText("Ingresar usuario");
        filaUsuario.getChildren().addAll(lblUsuario, txtUsuario);

        // Campo Contraseña
        HBox filaPassword = new HBox(10);
        filaPassword.setAlignment(Pos.CENTER_LEFT);
        Label lblPassword = new Label("Password");
        lblPassword.setPrefWidth(90);
        lblPassword.setStyle("-fx-font-weight: bold");

        txtPassword = new PasswordField();
        txtPassword.setPrefWidth(220);
        txtPassword.setPromptText("Ingresar contraseña");
        filaPassword.getChildren().addAll(lblPassword, txtPassword);

        panelIdentificacion.getChildren().addAll(lblTituloPanel, filaPerfil, filaUsuario, filaPassword);

        // Mensaje de Error
        lblMensajeError = new Label();
        lblMensajeError.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 11px; -fx-font-weight: bold");
        lblMensajeError.setWrapText(false);

        // Boton de Iniciar Sesion
        HBox filaBoton = new HBox();
        filaBoton.setAlignment(Pos.CENTER_RIGHT);
        btnIniciarSesion = new Button("Iniciar Sesion");
        btnIniciarSesion.setPrefWidth(120);
        btnIniciarSesion.setPrefHeight(-30);
        btnIniciarSesion.setStyle("-fx-font-weight: bold; -fx-cursor: hand;");
        filaBoton.getChildren().addAll(btnIniciarSesion);

        // Barra de estado
        HBox barraEstado = new HBox();
        barraEstado.setAlignment(Pos.CENTER_LEFT);
        barraEstado.setPadding(new Insets(-10,0,0,0));
        lblEstadoConexion = new Label("Conexion local establelcida con BancoDatos (BD mock-up)");
        lblEstadoConexion.setStyle("-fx-text-fill: #666666; -fx-font-size: 11px;");
        barraEstado.getChildren().addAll(lblEstadoConexion);

        root.getChildren().addAll(panelIdentificacion, lblMensajeError, filaBoton, new Separator(),  barraEstado);
    }

    public Parent getRoot() {
        return root;
    }

    public RadioButton getRbRecepcionista() {
        return rbRecepcionista;
    }

    public RadioButton getRbMedico() {
        return rbMedico;
    }

    public TextField getTxtUsuario() {
        return txtUsuario;
    }

    public PasswordField getTxtPassword() {
        return txtPassword;
    }

    public Button getBtnIniciarSesion() {
        return btnIniciarSesion;
    }

    public Label getLblMensajeError() {
        return lblMensajeError;
    }

    public void mostrarError(String mensaje) {
        lblMensajeError.setText(mensaje);
        lblMensajeError.setVisible(true);
        txtPassword.clear();
        txtPassword.requestFocus();
    }

    public void limpiarError() {
        lblMensajeError.setText("");
        lblMensajeError.setVisible(false);
    }
}

