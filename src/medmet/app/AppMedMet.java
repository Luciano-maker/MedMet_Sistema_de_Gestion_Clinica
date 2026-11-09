package medmet.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import medmet.conexion.ConexionBD;
import medmet.controlador.LoginController;
import medmet.vistas.login.ConexionDialog;
import medmet.vistas.login.LoginView;

public class AppMedMet extends Application {

    @Override
    public void start(Stage primaryStage) {

        if (ConexionBD.verificarConexion()) {
            ConexionDialog configDialog = new ConexionDialog(null);
            boolean conectado = configDialog.showAndWait();

            if (!conectado) {
                return;
            }
        }

        abrirLogin(primaryStage);
    }

    public static void abrirLogin(Stage stage) {
        if (stage == null) {
            stage = new Stage();
        }
        LoginView vistaLogin = new LoginView();
        new LoginController(vistaLogin, stage);

        Scene scene = new Scene(vistaLogin.getRoot(), 430, 290);
        stage.setTitle("MedMet - Acceso al Sistema");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
