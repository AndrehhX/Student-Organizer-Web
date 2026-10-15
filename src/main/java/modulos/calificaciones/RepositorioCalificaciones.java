package modulos.calificaciones;

import java.util.List;
import java.util.Optional;

/**
 * RF03 - Registrar Calificaciones.
 * Interfaz que establece el contrato de persistencia para el modulo de calificaciones.
 */
public interface RepositorioCalificaciones {

    // ============
    // REGISTRO CALIFICACIONES
    // ======================

    void guardar(Calificacion calificacion);

    // ====
    // CONSULTAS/LISTADOS
    // ====

    List<Calificacion> listarPorUsuario(String idUsuario);

    List<Calificacion> listarPorCurso(String idUsuario, String idCurso);

    Optional<Calificacion> buscarPorId(String idCalificacion);
}