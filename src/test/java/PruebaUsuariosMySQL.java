import modelo.Usuario;
import persistencia.ConexionBD;
import persistencia.RepositorioUsuariosMySQL;

import java.util.Optional;

/**
 * Prueba manual de RepositorioUsuariosMySQL 
 * Cada ejecución agrega un usuario nuevo con carné PRUEBA.
 */
public class PruebaUsuariosMySQL {

    public static void main(String[] args) {
        String clave = System.getenv("DB_PASSWORD");

        if (clave == null || clave.isBlank()) {
            throw new IllegalStateException("Falta configurar DB_PASSWORD.");
        }

        ConexionBD bd = new ConexionBD(
                "jdbc:mysql://127.0.0.1:3307/student_organizer",
                "organizer_app",
                clave
        );

        RepositorioUsuariosMySQL repositorio =
                new RepositorioUsuariosMySQL(bd);

        String nombre = "prueba_" + System.currentTimeMillis();

        // 1. Guardar
        long id = repositorio.guardar(
                new Usuario(nombre, "Prueba123", "PRUEBA")
        );
        comprobar(id > 0, "Debe devolver el ID generado.");
        System.out.println("Usuario guardado. ID: " + id);

        // 2. Login correcto devuelve el mismo ID
        Optional<Long> correcto =
                repositorio.autenticar(nombre, "Prueba123");
        comprobar(correcto.isPresent() && correcto.get() == id,
                "Con la contraseña correcta debe devolver el ID.");

        // 3. Contraseña incorrecta
        comprobar(repositorio.autenticar(nombre, "otra").isEmpty(),
                "Con contraseña incorrecta no debe autenticar.");

        // 4. Usuario inexistente
        comprobar(
                repositorio.autenticar("no_existe_" + nombre, "x")
                        .isEmpty(),
                "Un usuario inexistente no debe autenticar.");

        // 5. Buscar ID por nombre
        comprobar(
                repositorio.buscarId(nombre).equals(Optional.of(id)),
                "buscarId debe devolver el mismo ID.");

        // 6. Buscar por ID
        Optional<Usuario> recuperado = repositorio.buscarPorId(id);
        comprobar(
                recuperado.isPresent()
                        && recuperado.get().getNombreUsuario()
                                .equals(nombre)
                        && recuperado.get().getCarne()
                                .equals("PRUEBA"),
                "buscarPorId debe recuperar nombre y carné.");

        // 7. Nombre duplicado
        try {
            repositorio.guardar(
                    new Usuario(nombre, "Prueba123", "PRUEBA")
            );
            throw new IllegalStateException(
                    "FALLÓ: debió rechazar el nombre repetido.");
        } catch (IllegalArgumentException esperado) {
            System.out.println("Nombre repetido rechazado: OK");
        }

        System.out.println("Todas las comprobaciones pasaron.");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new IllegalStateException("FALLÓ: " + mensaje);
        }
    }
}