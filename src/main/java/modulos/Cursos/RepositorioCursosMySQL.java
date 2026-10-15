package modulos.Cursos;

import persistencia.ConexionBD;
import modulos.Cursos.RepositorioCursosMySQL;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



public class RepositorioCursosMySQL {

    
    private final ConexionBD bd;

    

    public RepositorioCursosMySQL(ConexionBD bd) {
        if (bd == null) {
            throw new IllegalArgumentException(
                    "La conexión a la base de datos es obligatoria."
            );
        }

        this.bd = bd;
    }

    public Curso guardar(long usuarioId, String nombreCurso) {
        validarId(usuarioId, "usuario");

        if (nombreCurso == null || nombreCurso.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del curso es obligatorio."
            );
        }

        String nombre = nombreCurso.trim();

        if (nombre.codePointCount(0, nombre.length()) > 150) {
            throw new IllegalArgumentException(
                    "El nombre del curso admite hasta 150 caracteres."
            );
        }

        String sql =
                "INSERT INTO cursos (usuario_id, nombre) "
                + "VALUES (?, ?)";

        try (
            Connection conexion = bd.abrir();
            PreparedStatement consulta = conexion.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            )
        ) {
            consulta.setLong(1, usuarioId);
            consulta.setString(2, nombre);

            consulta.executeUpdate();

            try (ResultSet claves = consulta.getGeneratedKeys()) {
                if (claves.next()) {
                    return new Curso(
                            claves.getLong(1),
                            usuarioId,
                            nombre
                    );
                }
            }

            throw new SQLException(
                    "MySQL no devolvió el ID del curso."
            );

        } catch (SQLException error) {
            if (error.getErrorCode() == 1062) {
                throw new IllegalArgumentException(
                        "Ya tienes un curso con ese nombre.",
                        error
                );
            }

            throw new IllegalStateException(
                    "No se pudo guardar el curso en MySQL.",
                    error
            );
        }
    }

    public List<Curso> listarPorUsuario(long usuarioId) {
        validarId(usuarioId, "usuario");

        String sql =
                "SELECT id, usuario_id, nombre "
                + "FROM cursos "
                + "WHERE usuario_id = ? "
                + "ORDER BY nombre, id";

        List<Curso> cursos = new ArrayList<>();

        try (
            Connection conexion = bd.abrir();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setLong(1, usuarioId);

            try (ResultSet resultado = consulta.executeQuery()) {
                while (resultado.next()) {
                    cursos.add(leerCurso(resultado));
                }
            }

            return cursos;

        } catch (SQLException error) {
            throw new IllegalStateException(
                    "No se pudieron consultar los cursos.",
                    error
            );
        }
    }

    public Optional<Curso> buscarPorId(
            long usuarioId,
            long cursoId
    ) {
        validarId(usuarioId, "usuario");
        validarId(cursoId, "curso");

        String sql =
                "SELECT id, usuario_id, nombre "
                + "FROM cursos "
                + "WHERE id = ? AND usuario_id = ?";

        try (
            Connection conexion = bd.abrir();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setLong(1, cursoId);
            consulta.setLong(2, usuarioId);

            try (ResultSet resultado = consulta.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(leerCurso(resultado));
                }

                return Optional.empty();
            }

        } catch (SQLException error) {
            throw new IllegalStateException(
                    "No se pudo consultar el curso.",
                    error
            );
        }
    }

    private Curso leerCurso(ResultSet resultado)
            throws SQLException {
        return new Curso(
                resultado.getLong("id"),
                resultado.getLong("usuario_id"),
                resultado.getString("nombre")
        );
    }

    private void validarId(long id, String campo) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID de " + campo + " debe ser mayor que cero."
            );
        }
    }
}
