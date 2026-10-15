package modulos.tareas;

import persistencia.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

// guarda y consulta tareas en MySQL
public class PersistenciaTareasMySQL {

    private final ConexionBD bd;

    public PersistenciaTareasMySQL(ConexionBD bd) {
        if (bd == null) {
            throw new IllegalArgumentException(
                    "La conexión a MySQL es obligatoria."
            );
        }

        this.bd = bd;
    }

    /**
     * Inserta una tarea nueva y le asigna el ID generado por MySQL.
     * El curso debe existir y pertenecer al usuario indicado.
     */
    public long guardarTarea(long usuarioId, Tarea tarea) {
        validarId(usuarioId, "usuario");

        if (tarea == null) {
            throw new IllegalArgumentException(
                    "La tarea es obligatoria."
            );
        }

        if (tarea.getId() != 0) {
            throw new IllegalArgumentException(
                    "Esta tarea ya tiene ID. No se puede insertar otra vez."
            );
        }

        if (tarea.getCursoId() == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar un curso para la tarea."
            );
        }

        validarId(tarea.getCursoId(), "curso");

        String nombre = tarea.getNombre();

        if (nombre.codePointCount(0, nombre.length()) > 150) {
            throw new IllegalArgumentException(
                    "El nombre admite hasta 150 caracteres."
            );
        }

        LocalDate fecha;

        try {
            fecha = LocalDate.parse(tarea.getfechaEntrega());
        } catch (DateTimeParseException error) {
            throw new IllegalArgumentException(
                    "La fecha debe ser válida y tener formato AAAA-MM-DD.",
                    error
            );
        }

        // Prioridad ya no participa en el guardado.
        String sql =
                "INSERT INTO tareas "
                + "(usuario_id, curso_id, nombre, "
                + "fecha_entrega, completada) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (
            Connection conexion = bd.abrir();
            PreparedStatement consulta = conexion.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            )
        ) {
            consulta.setLong(1, usuarioId);
            consulta.setLong(2, tarea.getCursoId());
            consulta.setString(3, nombre);
            consulta.setDate(4, Date.valueOf(fecha));
            consulta.setBoolean(5, tarea.isCompletada());

            consulta.executeUpdate();

            try (ResultSet claves = consulta.getGeneratedKeys()) {
                if (claves.next()) {
                    long id = claves.getLong(1);
                    tarea.setId(id);
                    return id;
                }
            }

            throw new SQLException(
                    "MySQL no devolvió el ID de la tarea."
            );

        } catch (SQLException error) {
            throw new IllegalStateException(
                    "No se pudo guardar la tarea en MySQL.",
                    error
            );
        }
    }

    /**
     * Recupera únicamente las tareas del usuario indicado.
     * Devuelve primero las pendientes, ordenadas por fecha.
     */
    public List<Tarea> consultarPorUsuario(long usuarioId) {
        validarId(usuarioId, "usuario");

        String sql =
                "SELECT id, curso_id, nombre, fecha_entrega, completada "
                + "FROM tareas "
                + "WHERE usuario_id = ? "
                + "ORDER BY completada, fecha_entrega, id";

        List<Tarea> tareas = new ArrayList<>();

        try (
            Connection conexion = bd.abrir();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setLong(1, usuarioId);

            try (ResultSet resultado = consulta.executeQuery()) {
                while (resultado.next()) {
                    Tarea tarea = new Tarea(
                            resultado.getString("nombre"),
                            resultado.getDate("fecha_entrega")
                                    .toLocalDate()
                                    .toString()
                    );

                    tarea.setId(resultado.getLong("id"));

                    // getLong devuelve 0 para SQL NULL.
                    // wasNull permite conservar la ausencia del curso.
                    long cursoId = resultado.getLong("curso_id");

                    if (!resultado.wasNull()) {
                        tarea.setCursoId(cursoId);
                    }

                    tarea.setCompletada(
                            resultado.getBoolean("completada")
                    );

                    tareas.add(tarea);
                }
            }

            return tareas;

        } catch (SQLException error) {
            throw new IllegalStateException(
                    "No se pudieron consultar las tareas.",
                    error
            );
        }
    }

    /**
     * Completa una tarea pendiente del usuario indicado.
     *
     * Devuelve true si actualizó una fila.
     * Devuelve false si la tarea no existe para ese usuario
     * o si ya estaba completada.
     */
    public boolean marcarCompletada(long usuarioId, long tareaId) {
        validarId(usuarioId, "usuario");
        validarId(tareaId, "tarea");

        String sql =
                "UPDATE tareas "
                + "SET completada = TRUE "
                + "WHERE id = ? "
                + "AND usuario_id = ? "
                + "AND completada = FALSE";

        try (
            Connection conexion = bd.abrir();
            PreparedStatement consulta =
                    conexion.prepareStatement(sql)
        ) {
            consulta.setLong(1, tareaId);
            consulta.setLong(2, usuarioId);

            return consulta.executeUpdate() == 1;

        } catch (SQLException error) {
            throw new IllegalStateException(
                    "No se pudo completar la tarea.",
                    error
            );
        }
    }

    private void validarId(long id, String campo) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID de " + campo + " debe ser mayor que cero."
            );
        }
    }
}