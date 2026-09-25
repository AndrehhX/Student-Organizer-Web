import controlador.ControladorLogin;
import modelo.Usuario;
import persistencia.PersistenciaDatos;
import util.Seguridad;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class PruebasLogin {
    public static void main(String[] args) throws Exception {
        Path carpetaTemporal = Files.createTempDirectory("student-organizer-login-");
        try {
            PersistenciaDatos persistencia = new PersistenciaDatos(
                    carpetaTemporal.resolve("usuarios.csv"));
            ControladorLogin controlador = new ControladorLogin(persistencia);

            comprobar(!controlador.iniciarSesion("", ""),
                    "Los campos vacíos deben rechazarse");
            comprobar(!controlador.iniciarSesion("nadie", "demo123"),
                    "Un usuario inexistente debe rechazarse");
            comprobar(!controlador.iniciarSesion("demo", "incorrecta"),
                    "Una contraseña incorrecta debe rechazarse");
            comprobar(controlador.iniciarSesion("demo", "demo123"),
                    "La cuenta demo debe iniciar sesión");
            comprobar(controlador.getUsuarioActual() != null,
                    "Debe guardarse el usuario actual");

            Usuario nuevo = new Usuario("ana", "clave123", "0001");
            persistencia.guardarUsuario(nuevo);
            comprobar(persistencia.leerUsuarios().size() == 2,
                    "Debe guardar un usuario nuevo");
            comprobar(Seguridad.codificar("clave123").length() == 64,
                    "El hash SHA-256 debe tener 64 caracteres");

            System.out.println("PruebasLogin: todas las pruebas pasaron.");
        } finally {
            try (var archivos = Files.walk(carpetaTemporal)) {
                archivos.sorted(Comparator.reverseOrder()).forEach(archivo -> {
                    try {
                        Files.deleteIfExists(archivo);
                    } catch (Exception error) {
                        throw new RuntimeException(error);
                    }
                });
            }
        }
    }

    private static void comprobar(boolean resultado, String mensaje) {
        if (!resultado) {
            throw new AssertionError(mensaje);
        }
    }
}
