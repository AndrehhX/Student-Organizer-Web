package modulos.tareas;

import modulos.Cursos.RepositorioCursosMySQL;
import persistencia.ConexionBD;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona las tareas almacenadas en MySQL.
 *
 * No mantiene una lista compartida en memoria.
 * Cada consulta obtiene los datos de la base.
 *
 * El backend debe entregar el ID del usuario autenticado.
 * Ese identificador corresponde a usuarios.id.
 */
public class ControladorTareasMySQL {

    private final PersistenciaTareasMySQL tareas;
    private final RepositorioCursosMySQL cursos;

    public ControladorTareasMySQL(ConexionBD bd) {
        if (bd == null) {
            throw new IllegalArgumentException(
                    "La configuración de conexión es obligatoria."
            );
        }

        this.tareas = new PersistenciaTareasMySQL(bd);
        this.cursos = new RepositorioCursosMySQL(bd);
    }

    /**
     * Registra una tarea asociada a un curso del usuario.
     *
     * Solo devuelve la tarea cuando el guardado terminó correctamente.
     * El objeto devuelto contiene el ID generado por MySQL.
     */
    public Tarea registrarTarea(
            long usuarioId,
            long cursoId,
            String nombre,
            String fechaEntrega
    ) {
        // Comprueba que el curso existe para este usuario.
        // La clave foránea también protege esta relación en MySQL.
        if (!cursos.buscarPorId(usuarioId, cursoId).isPresent()) {
            throw new IllegalArgumentException(
                    "El curso no existe o no pertenece al usuario."
            );
        }

        Tarea tarea = new Tarea(
                nombre,
                fechaEntrega,
                cursoId
        );

        tareas.guardarTarea(usuarioId, tarea);

        return tarea;
    }

    /**
     * Recupera todas las tareas del usuario desde MySQL.
     *
     * Incluye tareas pendientes y completadas.
     */
    public List<Tarea> listarTareas(long usuarioId) {
        return tareas.consultarPorUsuario(usuarioId);
    }

    /**
     * Recupera únicamente las tareas pendientes del usuario.
     */
    public List<Tarea> consultarPendientes(long usuarioId) {
        List<Tarea> pendientes = new ArrayList<>();

        for (Tarea tarea : tareas.consultarPorUsuario(usuarioId)) {
            if (!tarea.isCompletada()) {
                pendientes.add(tarea);
            }
        }

        return pendientes;
    }

    /**
     * Guarda en MySQL el cambio de una tarea a completada.
     *
     * Devuelve true si cambió una tarea pendiente.
     * Devuelve false si no existe para ese usuario
     * o si ya estaba completada.
     */
    public boolean marcarCompletada(
            long usuarioId,
            long tareaId
    ) {
        return tareas.marcarCompletada(usuarioId, tareaId);
    }
}