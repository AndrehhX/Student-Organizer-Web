package modulos.calificaciones;

import persistencia.ConexionBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * RF03 - Registrar Calificaciones.
 *
 * Implementación MySQL de RepositorioCalificaciones.
 * Respeta la interfaz de Fabricio: no cambia Calificacion
 * ni ControladorCalificaciones.
 *
 * Importante:
 * - idUsuario debe ser usuarios.id (número), no el carné.
 * - idCurso debe ser cursos.id (número).
 * - El nombre del curso no se guarda en calificaciones;
 *   se obtiene con JOIN desde la tabla cursos.
 */
public class RepositorioCalificacionesMySQL
        implements RepositorioCalificaciones {

    private static final String SELECCION =
            "SELECT c.id, c.usuario_id, c.curso_id, cu.nombre AS nombre_curso, "
            + "c.nombre_actividad, c.nota_obtenida, c.nota_maxima, c.fecha "
            + "FROM calificaciones c "
            + "JOIN cursos cu "
            + "ON cu.id = c.curso_id AND cu.usuario_id = c.usuario_id ";

    private final ConexionBD bd;

    public RepositorioCalificacionesMySQL(ConexionBD bd) {
        if (bd == null) {
            throw new IllegalArgumentException(
                    "La conexión a la base de datos es obligatoria."
            );
        }

        this.bd = bd;
    }

    @Override
    public void guardar(Calificacion calificacion) {
        if (calificacion == null) {
            throw new IllegalArgumentException(
                    "La calificacion no puede ser nula."
            );
        }

        long usuarioId = convertirId(
                calificacion.getIdUsuario(), "usuario"
        );
        long cursoId = convertirId(
                calificacion.getIdCurso(), "curso"
        );

        String nombreActividad = calificacion.getNombreActividad();

        if (nombreActividad.codePointCount(0, nombreActividad.length())
                > 150) {
            throw new IllegalArgumentException(
                    "El nombre de la actividad admite hasta 150 caracteres."
            );
        }

        String sql =
                "INSERT INTO calificaciones "
                + "(id, usuario_id, curso_id, nombre_actividad, "
                + "nota_obtenida, nota_maxima, fecha) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection conexion = bd.abrir();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setString(1, calificacion.getIdCalificacion());
            consulta.setLong(2, usuarioId);
            consulta.setLong(3, cursoId);
            consulta.setString(4, nombreActividad);
            consulta.setBigDecimal(5, calificacion.getNotaObtenida());
            consulta.setBigDecimal(6, calificacion.getNotaMaxima());
            consulta.setDate(7, Date.valueOf(calificacion.getFecha()));

            consulta.executeUpdate();

        } catch (SQLException error) {
            // 1062: ID de calificación repetido.
            if (error.getErrorCode() == 1062) {
                throw new IllegalStateException(
                        "Ya existe una calificacion con el ID: "
                        + calificacion.getIdCalificacion(),
                        error
                );
            }

            // 1452: el curso no existe o no es de este usuario.
            // 3819: falló el CHECK de las notas.
            if (error.getErrorCode() == 1452
                    || error.getErrorCode() == 3819) {
                throw new IllegalArgumentException(
                        "El curso no existe, no pertenece al usuario "
                        + "o las notas no son válidas.",
                        error
                );
            }

            throw new IllegalStateException(
                    "No se pudo guardar la calificación en MySQL.",
                    error
            );
        }
    }

    @Override
    public List<Calificacion> listarPorUsuario(String idUsuario) {
        long usuarioId = convertirId(idUsuario, "usuario");

        String sql = SELECCION
                + "WHERE c.usuario_id = ? "
                + "ORDER BY c.fecha, c.nombre_actividad, c.id";

        List<Calificacion> resultado = new ArrayList<>();

        try (
            Connection conexion = bd.abrir();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setLong(1, usuarioId);

            try (ResultSet filas = consulta.executeQuery()) {
                while (filas.next()) {
                    resultado.add(leerCalificacion(filas));
                }
            }

            return resultado;

        } catch (SQLException error) {
            throw new IllegalStateException(
                    "No se pudieron consultar las calificaciones.",
                    error
            );
        }
    }

    @Override
    public List<Calificacion> listarPorCurso(
            String idUsuario, String idCurso) {

        long usuarioId = convertirId(idUsuario, "usuario");
        long cursoId = convertirId(idCurso, "curso");

        String sql = SELECCION
                + "WHERE c.usuario_id = ? AND c.curso_id = ? "
                + "ORDER BY c.fecha, c.nombre_actividad, c.id";

        List<Calificacion> resultado = new ArrayList<>();

        try (
            Connection conexion = bd.abrir();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setLong(1, usuarioId);
            consulta.setLong(2, cursoId);

            try (ResultSet filas = consulta.executeQuery()) {
                while (filas.next()) {
                    resultado.add(leerCalificacion(filas));
                }
            }

            return resultado;

        } catch (SQLException error) {
            throw new IllegalStateException(
                    "No se pudieron consultar las calificaciones del curso.",
                    error
            );
        }
    }

    @Override
    public Optional<Calificacion> buscarPorId(String idCalificacion) {
        if (idCalificacion == null || idCalificacion.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El ID de calificacion es obligatorio."
            );
        }

        String sql = SELECCION + "WHERE c.id = ?";

        try (
            Connection conexion = bd.abrir();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setString(1, idCalificacion.trim());

            try (ResultSet filas = consulta.executeQuery()) {
                if (filas.next()) {
                    return Optional.of(leerCalificacion(filas));
                }

                return Optional.empty();
            }

        } catch (SQLException error) {
            throw new IllegalStateException(
                    "No se pudo consultar la calificación.",
                    error
            );
        }
    }

    private Calificacion leerCalificacion(ResultSet filas)
            throws SQLException {

        BigDecimal notaObtenida = filas.getBigDecimal("nota_obtenida");
        BigDecimal notaMaxima = filas.getBigDecimal("nota_maxima");

        return new Calificacion(
                filas.getString("id"),
                String.valueOf(filas.getLong("usuario_id")),
                String.valueOf(filas.getLong("curso_id")),
                filas.getString("nombre_curso"),
                filas.getString("nombre_actividad"),
                notaObtenida,
                notaMaxima,
                filas.getDate("fecha").toLocalDate()
        );
    }

    /**
     * La interfaz usa String, pero MySQL usa BIGINT.
     * Convierte y valida que el ID sea un número mayor que cero.
     */
    private long convertirId(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El ID de " + campo + " es obligatorio."
            );
        }

        long id;

        try {
            id = Long.parseLong(valor.trim());
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException(
                    "El ID de " + campo + " debe ser numérico "
                    + "(corresponde al id de MySQL).",
                    error
            );
        }

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID de " + campo + " debe ser mayor que cero."
            );
        }

        return id;
    }
}