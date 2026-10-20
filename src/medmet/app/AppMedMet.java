package medmet.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import medmet.controlador.LoginController;
import medmet.vistas.LoginView;

public class AppMedMet extends Application {

    @Override
    public void start(Stage primaryStage) {

        LoginView vistaLogin = new LoginView();
        new LoginController(vistaLogin, primaryStage);

        Scene scene = new Scene(vistaLogin.getRoot(), 430,290);
        primaryStage.setTitle("MedMet - Acceso al Sistema");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
