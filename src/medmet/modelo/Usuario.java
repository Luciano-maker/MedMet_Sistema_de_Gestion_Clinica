package medmet.modelo;

public class Usuario {

    private int idUsuario;
    private String nombreUsuario;
    private String contraseniaUsuario;
    private String rolUsuario;     // Recepcionista, Medico
    private boolean activo;

    // ----------- Constructor -------------------
    public Usuario(int idUsuario, String nombreUsuario, String contraseniaUsuario, String rolUsuario) {

        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contraseniaUsuario = contraseniaUsuario;
        this.rolUsuario = rolUsuario;
        this.activo = true;
    }

    // ----------- Metodos Logicos -------------------
    public boolean autenticar(String user, String pass) {      // Valida si las credenciales ingresadas coinciden con un usuario activo
        if (!activo) {
            return false;
        }
        return this.nombreUsuario.equals(user) && this.contraseniaUsuario.equals(pass);
    }

    public void cambiarContrasenia(String nuevaContrasenia) {       // Permite cambiar contraseña
        if (nuevaContrasenia != null && !nuevaContrasenia.trim().isEmpty()) {
            this.contraseniaUsuario = nuevaContrasenia;
        }
    }

    // ----------- Metodos GETTER -------------------
    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContraseniaUsuario() {
        return contraseniaUsuario;
    }

    public String getRol() {
        return rolUsuario;
    }

    public boolean isActivo() {
        return activo;
    }

    // ----------- Metodos SETTER -------------------
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNombre(String nombre) {
        this.nombreUsuario = nombre;
    }

    public void setContrasenia(String contrasenia) {
        this.contraseniaUsuario = contrasenia;
    }

    public void setRol(String rol) {
        this.rolUsuario = rol;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
