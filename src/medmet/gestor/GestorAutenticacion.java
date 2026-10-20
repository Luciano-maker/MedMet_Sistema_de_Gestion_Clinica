package medmet.gestor;

import medmet.excepciones.MedMetException;
import medmet.modelo.Usuario;

import java.util.List;

// Gestor responsable de administrar el inicio de sesion
public class GestorAutenticacion {

    private final BancoDatos bancoDatos;
    private Usuario usuarioActual;

    public GestorAutenticacion(){
        this.bancoDatos = BancoDatos.getInstancia();
    }

    // Auntentica las credenciales ingresadas por el usuario
    public Usuario autenticarUsuario(String nombreUsuario, String contrasenia) throws MedMetException {

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty() ||      // Valida los campos obligatorios
                contrasenia == null || contrasenia.trim().isEmpty()) {
            throw new MedMetException("Ingresar usuario y contraseña");
        }

        String usr  = nombreUsuario.trim();
        String pass = contrasenia.trim();

        // Busca los datos en el repositorio
        List<Usuario> usuarios = bancoDatos.getUsuarios();
        for (Usuario u : usuarios) {
            if (u.autenticar(usr, pass)) {
                this.usuarioActual = u;
                return u;
            }
        }
        throw new MedMetException("Usuario no encontrado");
    }

    public Usuario getUsuarioActual() {     // Obtiene el usuario autenticado
        return usuarioActual;
    }

    public void cerarSesion() {     // Cierra la sesion
        this.usuarioActual = null;
    }
}
