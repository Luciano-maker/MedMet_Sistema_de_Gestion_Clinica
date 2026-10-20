package medmet.controlador;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import medmet.app.AppMedMet;
import medmet.gestor.GestorTurnos;
import medmet.modelo.Medico;
import medmet.modelo.Turno;
import medmet.vistas.medico.BuscarHCDialogo;
import medmet.vistas.medico.ConsultaMedicaDialogo;
import medmet.vistas.medico.MedicoMainView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Controlador para la pantalla principal del modulo de consultorio
public class MedicoController {

    private final MedicoMainView vista;
    private final Stage stageActual;
    private final Medico medicoActual;
    private final GestorTurnos gestorTurnos;

    // Constructor
    public MedicoController(MedicoMainView vista, Stage stageActual, Medico medicoActual) {

        this.vista = vista;
        this.stageActual = stageActual;
        this.medicoActual = medicoActual;
        this.gestorTurnos = new GestorTurnos();

        configurarEncabezado();
        inicializarEventos();
        cargarAgendaDelDia();
    }

    private void configurarEncabezado() {

        String info = String.format("Profesional: Dr(a). %s | Especialidad: %s | Matrícula: %s", medicoActual.getNombreCompleto(),
                medicoActual.getEspecialidad() != null ? medicoActual.getEspecialidad().getNombre() : "General", medicoActual.getMatriculaProfesional());

        vista.getLblInfoMedico().setText(info);
    }

    private void inicializarEventos() {

        // Filtros y actualizar
        vista.getBtnBuscar().setOnAction(e -> filtrarAgenda());
        vista.getBtnRefrescar().setOnAction(e -> cargarAgendaDelDia());

        vista.getBtnAtender().setOnAction(e -> atenderPaciente());      // Atender turno

        // Menu superior
        vista.getMiBuscarHC().setOnAction(e -> new BuscarHCDialogo(stageActual).show());
        vista.getMiSalir().setOnAction(e -> stageActual.close());
        vista.getMiCerrarSesion().setOnAction(e -> {
            stageActual.close();
            new AppMedMet().start(new Stage());
        });

        // Actualiza el estado
        vista.getTablaTurnos().getSelectionModel().selectedItemProperty().addListener((obs, ant, selec) -> {
            if (selec != null) {
                vista.getLblEstadoAgenda().setText(String.format("Seleccionado: %s (%s) - Estado: %s",
                        selec.getPaciente().getNombreCompleto(), selec.getHora(), selec.getEstado()));
            } else {
                vista.getLblEstadoAgenda().setText("Seleccione un paciente en espera para iniciar la atención clínica.");
            }
        });
    }

    public void cargarAgendaDelDia() {

        LocalDate fecha = vista.getDpFecha().getValue();
        List<Turno> turnos = gestorTurnos.buscarTurnos(fecha, medicoActual.getIdMedico(), null, null);
        vista.getTablaTurnos().setItems(FXCollections.observableArrayList(turnos));
        actualizarContadorEstado(turnos);
    }

    private void filtrarAgenda() {

        LocalDate fecha = vista.getDpFecha().getValue();
        String estadoFiltro = vista.getCbFiltroEstado().getValue();

        List<Turno> turnos = gestorTurnos.buscarTurnos(fecha, medicoActual.getIdMedico(), null, null);
        if (estadoFiltro != null && !estadoFiltro.equalsIgnoreCase("Todos")) {
            List<Turno> filtrados = new ArrayList<>();
            for (Turno t : turnos) {
                if (t.getEstado().equalsIgnoreCase(estadoFiltro)) {
                    filtrados.add(t);
                }
            }

            vista.getTablaTurnos().setItems(FXCollections.observableArrayList(filtrados));
            actualizarContadorEstado(filtrados);

        } else {
            vista.getTablaTurnos().setItems(FXCollections.observableArrayList(turnos));
            actualizarContadorEstado(turnos);
        }
    }

    private void actualizarContadorEstado(List<Turno> lista) {

        long enEspera = lista.stream().filter(t -> "En espera".equalsIgnoreCase(t.getEstado())).count();
        vista.getLblEstadoAgenda().setText(String.format("Total turnos agendados: %d | Pacientes en espera: %d", lista.size(), enEspera));
    }

    private void atenderPaciente() {

        Turno selec = vista.getTablaTurnos().getSelectionModel().getSelectedItem();
        if (selec == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", "Debe seleccionar un turno de la lista para atender.");
            return;
        }

        if ("Finalizado".equalsIgnoreCase(selec.getEstado())) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Consulta Finalizada",
                    "El paciente ya fue atendido en este turno. Puede consultar su expediente en 'Historia Clínica'.");
            return;
        }

        if ("Cancelado".equalsIgnoreCase(selec.getEstado())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Turno Cancelado", "No es posible atender un turno cancelado.");
            return;
        }

        // Abre la ventana de atención clínica
        ConsultaMedicaDialogo dialog = new ConsultaMedicaDialogo(stageActual, selec);
        if (dialog.showAndWait()) {
            cargarAgendaDelDia();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {

        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.initOwner(stageActual);
        alert.showAndWait();
    }
}