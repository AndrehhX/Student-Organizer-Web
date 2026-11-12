import modelo.Usuario;
import persistencia.ConexionBD;
import persistencia.RepositorioUsuariosMySQL;

public class PruebasCorreo {

    public static void main(String[] args) {
        String hash = new Usuario(
                "ana", "  Ana.Mendez@Example.Test ", "ClaveSegura1!", "TEST"
        ).getContrasena();
        Usuario usuario = Usuario.desdeArchivo(
                "ana",
                "ana.mendez@example.test",
                hash,
                "TEST"
        );

        comprobar(
                "ana.mendez@example.test".equals(usuario.getCorreo()),
                "El modelo debe normalizar el correo a minúsculas."
        );
        comprobar(
                hash.equals(usuario.getContrasena()),
                "Al reconstruir el usuario no se debe volver a codificar el hash."
        );

        RepositorioUsuariosMySQL repositorio =
                new RepositorioUsuariosMySQL(new ConexionBD(
                        "jdbc:mysql://127.0.0.1:3307/student_organizer",
                        "pruebas",
                        ""
                ));

        comprobarCorreoRechazado(
                repositorio,
                new Usuario("usuario", "correo-invalido", "Clave1!", "TEST")
        );
        comprobarCorreoRechazado(
                repositorio,
                new Usuario("usuario", " ", "Clave1!", "TEST")
        );

        System.out.println("PruebasCorreo: todas las pruebas pasaron.");
    }

    private static void comprobarCorreoRechazado(
            RepositorioUsuariosMySQL repositorio,
            Usuario usuario
    ) {
        try {
            repositorio.guardar(usuario);
            throw new AssertionError("Debe rechazar un correo inválido.");
        } catch (IllegalArgumentException esperado) {
            // La validación ocurre antes de intentar abrir MySQL.
        }
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}
