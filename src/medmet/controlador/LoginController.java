package medmet.controlador;

import javafx.scene.Scene;
import javafx.stage.Stage;
import medmet.excepciones.MedMetException;
import medmet.gestor.GestorAutenticacion;
import medmet.gestor.GestorMedicos;
import medmet.modelo.Medico;
import medmet.modelo.Usuario;
import medmet.vistas.LoginView;
import medmet.vistas.medico.MedicoMainView;
import medmet.vistas.recepcion.RecepcionistaMainView;

// Controlador de acceso para la pantalla de Login
public class LoginController {

    private final LoginView vista;
    private final GestorAutenticacion gestorAutenticacion;
    private final Stage stageActual;

    // Constructor
    public LoginController(LoginView vista, Stage stageActual) {

        this.vista = vista;
        this.stageActual = stageActual;
        this.gestorAutenticacion = new GestorAutenticacion();

        inicializarEventos();
    }

    private void inicializarEventos() {     // Ejecuta el inicio de sesion

        vista.getBtnIniciarSesion().setOnAction(e -> procesarLogin());
        vista.getTxtPassword().setOnAction(e -> procesarLogin());
        vista.getTxtUsuario().setOnAction(e -> procesarLogin());
    }

    private void procesarLogin() {      // Realiza la validacion de usuario

        vista.limpiarError();
        String usuario = vista.getTxtUsuario().getText();
        String password = vista.getTxtPassword().getText();

        boolean perfilRecepcionistaSelcted = vista.getRbRecepcionista().isSelected();

        try {
            Usuario usuarioAutenticado = gestorAutenticacion.autenticarUsuario(usuario, password);

            String rolEsperado = perfilRecepcionistaSelcted ? "RECEPCIONISTA" : "MEDICO";

            if (!usuarioAutenticado.getRol().equalsIgnoreCase(rolEsperado)) {
                vista.mostrarError(("El usuario ingresado no corresponde al perfil " + (perfilRecepcionistaSelcted ? "RECEPCIONISTA" : "MEDICO")));
                return;
            }

            abrirPantallaPrincipal(usuarioAutenticado);

        }catch (MedMetException ex) {
            vista.mostrarError(ex.getMessage());        // Credenciales no validas
        }
    }

    private void abrirPantallaPrincipal(Usuario usuario) {      // Dirige a la pantalla principal segun el perfil
        try {
            Stage stagePrincipal = new Stage();
            stagePrincipal.setResizable(true);

            if ("RECEPCIONISTA".equalsIgnoreCase(usuario.getRol())) {

                stagePrincipal.setTitle("MedMet | Módulo de Administración - Recepción");

                RecepcionistaMainView vistaRecepcionista = new RecepcionistaMainView();
                new RecepcionistaController(vistaRecepcionista, stagePrincipal);

                Scene scene = new Scene(vistaRecepcionista.getRoot(), 980, 580);
                stagePrincipal.setScene(scene);
                stagePrincipal.show();

                stageActual.close(); // Cierra el login

            } else {
                GestorMedicos gestorMed = new GestorMedicos();
                Medico medicoSesion = null;
                String user = usuario.getNombreUsuario().toLowerCase();

                // Busca el médico por coincidencia de apellido
                for (Medico m : gestorMed.listarMedicosActivos()) {

                    String apellidoNormalizado = java.text.Normalizer.normalize(m.getApellido().toLowerCase(), java.text.Normalizer.Form.NFD)
                            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");       // Normaliza el apellido
                                                                                                        // quitando acentos para comparar
                    if (user.contains(apellidoNormalizado)) {
                        medicoSesion = m;
                        break;
                    }
                }

                if (medicoSesion == null && !gestorMed.listarMedicosActivos().isEmpty()) {      // Toma el primer médico disponible
                    medicoSesion = gestorMed.listarMedicosActivos().get(0);                     // en caso de no coincidir el nombre
                }

                if (medicoSesion == null) {         // Valida para evitar una excepcion
                    vista.mostrarError("No se encontró el perfil del médico en la base de datos.");
                    return;
                }

                stagePrincipal.setTitle("MedMet | Consultorio Clínico - " + (medicoSesion != null ? medicoSesion.getNombreCompleto() : "Médico"));

                MedicoMainView vistaMedico = new MedicoMainView();
                new MedicoController(vistaMedico, stagePrincipal, medicoSesion);

                Scene scene = new Scene(vistaMedico.getRoot(), 940, 560);
                stagePrincipal.setScene(scene);
                stagePrincipal.show();

                stageActual.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            vista.mostrarError("Error al abrir la sesión: " + ex.getMessage());
        }

    }
}
