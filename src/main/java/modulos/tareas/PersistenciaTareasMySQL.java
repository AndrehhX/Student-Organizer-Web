package modulos.tareas;

import persistencia.ConexionBD;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class PersistenciaTareasMySQL {
    private final ConexionBD bd;

    public PersistenciaTareasMySQL(ConexionBD bd) {
        this.bd = bd;
    }

    public long guardarTarea(long usuarioId, Tarea tarea) {
        String sql =
                "INSERT INTO tareas "
                + "(usuario_id, nombre, fecha_entrega, prioridad, completada) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = bd.abrir();
             PreparedStatement consulta = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            consulta.setLong(1, usuarioId);
            consulta.setString(2, tarea.getNombre());
            consulta.setDate(3, Date.valueOf(
                    LocalDate.parse(tarea.getfechaEntrega())));
            consulta.setString(4, tarea.getPrioridad());
            consulta.setBoolean(5, tarea.isCompletada());

            consulta.executeUpdate();

            try (ResultSet claves = consulta.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getLong(1);
                }
            }

            throw new SQLException("MySQL no devolvió el ID de la tarea.");

        } catch (SQLException error) {
            throw new IllegalStateException(
                    "No se pudo guardar la tarea en MySQL.", error);
        }
    }
}