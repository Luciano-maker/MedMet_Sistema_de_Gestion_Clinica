package medmet.modelo;

// Clase abstracta de Persona, representa los atributos que tendrá cualquier individuo del Sistema
public abstract class Persona {
    protected String dni;
    protected String nombre;
    protected String apellido;
    protected String telefono;
    protected String email;
    protected String direccion;

    // ----------- Constructor -------------------
    public  Persona(String dni, String nombre, String apellido, String telefono, String email, String direccion) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    // ------------------- Metodos GETTER -------------------
    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getNombreCompleto() {
        return this.nombre + " " + this.apellido;
    }

    // ------------------- Metodos SETTER -------------------
    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}

