import controlador.ControladorLogin;
import modelo.Usuario;
import persistencia.ConexionBD;
import persistencia.RepositorioUsuariosMySQL;

import java.util.Optional;

public class PruebasLogin {

    public static void main(String[] args) {
        String clave = System.getenv("DB_PASSWORD");

        if (clave == null || clave.isBlank()) {
            throw new IllegalStateException(
                    "Falta configurar DB_PASSWORD."
            );
        }

        ConexionBD bd = new ConexionBD(
                "jdbc:mysql://127.0.0.1:3307/student_organizer",
                "organizer_app",
                clave
        );

        RepositorioUsuariosMySQL repositorio =
                new RepositorioUsuariosMySQL(bd);

        String nombre = "prueba_login_" + System.currentTimeMillis();

        long id = repositorio.guardar(
                new Usuario(nombre, "demo123", "LOGIN")
        );

        comprobar(id > 0, "Debe guardar el usuario.");

        ControladorLogin controlador =
                new ControladorLogin(repositorio);

        comprobar(
                controlador.iniciarSesion("", "").isEmpty(),
                "No debe aceptar campos vacíos."
        );

        comprobar(
                controlador.iniciarSesion(nombre, "incorrecta").isEmpty(),
                "No debe aceptar contraseña incorrecta."
        );

        Optional<String> token =
                controlador.iniciarSesion(nombre, "demo123");

        comprobar(
                token.isPresent(),
                "El usuario debe iniciar sesión."
        );

        comprobar(
                controlador.getUsuarioActual(token.get()) != null,
                "Debe encontrar al usuario usando el token."
        );

        controlador.cerrarSesion(token.get());

        comprobar(
                controlador.getUsuarioActual(token.get()) == null,
                "La sesión debe cerrarse correctamente."
        );

        System.out.println(
                "PruebasLogin: todas las pruebas pasaron."
        );
    }

    private static void comprobar(
            boolean resultado,
            String mensaje
    ) {
        if (!resultado) {
            throw new AssertionError(mensaje);
        }
    }
}
