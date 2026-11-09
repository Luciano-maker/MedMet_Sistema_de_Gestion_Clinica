package medmet.conexion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Gestor de conexion JDBC con la Base de Datos MySQL
public class ConexionBD {

    // Configuracion de datos de la base de datos MySQL
    private static final String Controlador = "com.mysql.cj.jdbc.Driver";
    private static final String ARCHIVO_CONFIG = "db_config.properties";

    private static String host = "localhost";
    private static String puerto = "3306";
    private static String BaseDatos = "sistemamedmetdb";
    private static String Usuario= "root";
    private static String Contrasenia = "root";

    static {
        cargarConfiguracion();
    }

    private static String construirURL() {
        return "jdbc:mysql://" + host + ":" + puerto + "/" + BaseDatos + "?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires&allowPublicKeyRetrieval=true";
    }

    public static Connection obtenerConexion() throws SQLException {     // Establece la conexion con la base de datos MySQL
        try {
            Class.forName(Controlador);
            return DriverManager.getConnection(construirURL(), Usuario, Contrasenia);

        } catch (ClassNotFoundException e) {
            throw new SQLException("No se pudo conectar con el driver JDBC (" + Controlador + "): " + e.getMessage());
        }
    }

    public static boolean verificarConexion() {
        try (Connection conexion = obtenerConexion()) {
            return conexion != null && !conexion.isClosed();

        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean conexionManual(String h, String p, String db, String user, String pass) {
        String urlPrueba = "jdbc:mysql://" + h + ":" + p + "/" + db + "?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires&allowPublicKeyRetrieval=true";

        try {
            Class.forName(Controlador);
            try (Connection conexion = DriverManager.getConnection(urlPrueba, user, pass)) {
                return conexion != null && !conexion.isClosed();
            }

        } catch (Exception e) {
            return false;
        }
    }

    public static void guardarConfiguracion(String h, String p, String db, String user, String pass) {

        host = h.trim();
        puerto = p.trim();
        BaseDatos = db.trim();
        Usuario = user.trim();
        Contrasenia = pass != null ? pass : "";

        Properties prop = new Properties();
        prop.setProperty("db.host", host);
        prop.setProperty("db.port", puerto);
        prop.setProperty("db.name", BaseDatos);
        prop.setProperty("db.user", Usuario);
        prop.setProperty("db.pass", Contrasenia);

        try (FileOutputStream out = new FileOutputStream(ARCHIVO_CONFIG)) {
            prop.store(out, "Configuracion de conexion MySQL - MedMet");
        } catch (IOException e) {
            System.err.println("Aviso: No se pudo guardar " + ARCHIVO_CONFIG + ": " + e.getMessage());
        }
    }

    public static void cargarConfiguracion() {
        File f =  new File(ARCHIVO_CONFIG);

        if (!f.exists()) {
            Properties prop = new Properties();
            try (FileInputStream in = new FileInputStream(f)) {
                prop.load(in);
                host = prop.getProperty("db.host", host);
                puerto = prop.getProperty("db.port", puerto);
                BaseDatos = prop.getProperty("db.name", BaseDatos);
                Usuario = prop.getProperty("db.user", Usuario);
                Contrasenia = prop.getProperty("db.pass", Contrasenia);

            } catch (IOException e) {
                System.err.println("Error al leer " + ARCHIVO_CONFIG + ": " + e.getMessage());
            }
        }
    }

    public static void cerrarConexion(Connection conexion) {        // Cierra la conexion de forma segura
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                System.err.println("Advertencia al cerrar la conexión JDBC " + e.getMessage());
            }
        }
    }

    public static String getHost() {
        return host;
    }

    public static String getPuerto() {
        return puerto;
    }

    public static String getBaseDatos() {
        return BaseDatos;
    }

    public static String getUsuario() {
        return Usuario;
    }

    public static String getContrasenia() {
        return Contrasenia;
    }
}