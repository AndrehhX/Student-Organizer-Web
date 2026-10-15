package persistencia;

import modelo.Usuario;
import util.Seguridad;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Guarda y consulta usuarios en MySQL (tabla usuarios).
 *
 * Es el equivalente en MySQL de PersistenciaDatos (CSV).
 * No modifica el modelo Usuario: el ID de MySQL (usuarios.id)
 * se entrega por separado, porque es el que necesitan
 * tareas y calificaciones.
 *
 * La contraseña nunca se guarda en texto plano: se usa
 * el mismo hash SHA-256 que ya genera el modelo Usuario.
 */
public class RepositorioUsuariosMySQL {

    private final ConexionBD bd;

    public RepositorioUsuariosMySQL(ConexionBD bd) {
        if (bd == null) {
            throw new IllegalArgumentException(
                    "La conexión a la base de datos es obligatoria."
            );
        }

        this.bd = bd;
    }

    /**
     * Inserta un usuario nuevo y devuelve su ID (usuarios.id).
     *
     * @throws IllegalArgumentException si los datos no son válidos
     *         o el nombre de usuario ya existe.
     */
    public long guardar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario es obligatorio."
            );
        }

        String nombre = validarTexto(
                usuario.getNombreUsuario(), "El nombre de usuario"
        );
        String carne = validarTexto(usuario.getCarne(), "El carné");
        String hash = usuario.getContrasena();

        if (nombre.codePointCount(0, nombre.length()) > 100) {
            throw new IllegalArgumentException(
                    "El nombre de usuario admite hasta 100 caracteres."
            );
        }

        if (carne.codePointCount(0, carne.length()) > 30) {
            throw new IllegalArgumentException(
                    "El carné admite hasta 30 caracteres."
            );
        }

        // contrasena_hash es CHAR(64): SHA-256 en hexadecimal.
        if (hash == null || hash.length() != 64) {
            throw new IllegalArgumentException(
                    "La contraseña debe estar codificada (SHA-256)."
            );
        }

        String sql =
                "INSERT INTO usuarios "
                + "(nombre_usuario, contrasena_hash, carne) "
                + "VALUES (?, ?, ?)";

        try (
            Connection conexion = bd.abrir();
            PreparedStatement consulta = conexion.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            )
        ) {
            consulta.setString(1, nombre);
            consulta.setString(2, hash);
            consulta.setString(3, carne);

            consulta.executeUpdate();

            try (ResultSet claves = consulta.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getLong(1);
                }
            }

            throw new SQLException(
                    "MySQL no devolvió el ID del usuario."
            );

        } catch (SQLException error) {
            if (error.getErrorCode() == 1062) {
                throw new IllegalArgumentException(
                        "Ya existe un usuario con ese nombre.",
                        error
                );
            }

            throw new IllegalStateException(
                    "No se pudo guardar el usuario en MySQL.",
                    error
            );
        }
    }

    /**
     * Valida usuario y contraseña.
     *
     * Devuelve el ID del usuario (usuarios.id) si las credenciales
     * son correctas; vacío si el usuario no existe o la contraseña
     * no coincide. No distingue entre ambos casos a propósito.
     */
    public Optional<Long> autenticar(
            String nombreUsuario, String contrasena) {

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()
                || contrasena == null || contrasena.isEmpty()) {
            return Optional.empty();
        }

        String sql =
                "SELECT id, contrasena_hash "
                + "FROM usuarios WHERE nombre_usuario = ?";

        try (
            Connection conexion = bd.abrir();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setString(1, nombreUsuario.trim());

            try (ResultSet filas = consulta.executeQuery()) {
                if (!filas.next()) {
                    return Optional.empty();
                }

                String guardado = filas.getString("contrasena_hash");
                String recibido = Seguridad.codificar(contrasena);

                boolean coincide = MessageDigest.isEqual(
                        guardado.getBytes(StandardCharsets.UTF_8),
                        recibido.getBytes(StandardCharsets.UTF_8)
                );

                return coincide
                        ? Optional.of(filas.getLong("id"))
                        : Optional.empty();
            }

        } catch (SQLException error) {
            throw new IllegalStateException(
                    "No se pudo validar el usuario.",
                    error
            );
        }
    }

    /**
     * Devuelve el ID (usuarios.id) de un nombre de usuario.
     */
    public Optional<Long> buscarId(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            return Optional.empty();
        }

        String sql =
                "SELECT id FROM usuarios WHERE nombre_usuario = ?";

        try (
            Connection conexion = bd.abrir();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setString(1, nombreUsuario.trim());

            try (ResultSet filas = consulta.executeQuery()) {
                return filas.next()
                        ? Optional.of(filas.getLong("id"))
                        : Optional.empty();
            }

        } catch (SQLException error) {
            throw new IllegalStateException(
                    "No se pudo consultar el usuario.",
                    error
            );
        }
    }

    /**
     * Recupera un usuario por su ID. Útil cuando el backend solo
     * conoce usuarios.id y necesita el nombre o el carné.
     */
    public Optional<Usuario> buscarPorId(long usuarioId) {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException(
                    "El ID de usuario debe ser mayor que cero."
            );
        }

        String sql =
                "SELECT nombre_usuario, contrasena_hash, carne "
                + "FROM usuarios WHERE id = ?";

        try (
            Connection conexion = bd.abrir();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setLong(1, usuarioId);

            try (ResultSet filas = consulta.executeQuery()) {
                if (!filas.next()) {
                    return Optional.empty();
                }

                return Optional.of(Usuario.desdeArchivo(
                        filas.getString("nombre_usuario"),
                        filas.getString("contrasena_hash"),
                        filas.getString("carne")
                ));
            }

        } catch (SQLException error) {
            throw new IllegalStateException(
                    "No se pudo consultar el usuario.",
                    error
            );
        }
    }

    private String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    campo + " es obligatorio."
            );
        }

        return valor.trim();
    }
}