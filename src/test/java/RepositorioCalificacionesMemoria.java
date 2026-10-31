
package modulos.calificaciones;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * RF03 - Registrar Calificaciones.
 *
 * Implementacion temporal en memoria RAM para probar
 * el almacenamiento y consulta de calificaciones
 * sin depender de una base de datos.
 */
public class RepositorioCalificacionesMemoria
        implements RepositorioCalificaciones {

    // ======
    // ATRIBUTOS
    // ==============

    private final List<Calificacion> calificaciones;

    // ==================
    // CONSTRUCTOR
    // ==================================

    public RepositorioCalificacionesMemoria() {
        this.calificaciones = new ArrayList<>();
    }

    // ============================
    // VALIDACION AUX
    // ===============
    private static String validarIdentificador(
            String id, String campo) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El " + campo + " es obligatorio."
            );
        }

        return id.trim();
    }

    // ==========================
    // REGISTRO CALIFICACIONES
    // =================================

    @Override
    public synchronized void guardar(
            Calificacion calificacion) {

        if (calificacion == null) {
            throw new IllegalArgumentException(
                "La calificacion no puede ser nula."
            );
        }

  

        boolean existeDuplicado = calificaciones.stream()
            .anyMatch(c ->
                c.getIdCalificacion().equals(
                    calificacion.getIdCalificacion()
                )
            );

        if (existeDuplicado) {
            throw new IllegalStateException(
                "Ya existe una calificacion con el ID: "
                + calificacion.getIdCalificacion()
            );
        }

        // Guardar la calificacion validada.
        calificaciones.add(calificacion);
    }

    // =========================
    // CONSULTAS Y LISTADOS
    // ==================================


    @Override
    public synchronized List<Calificacion> listarPorUsuario(
            String idUsuario) {

        String idUsuarioLimpio = validarIdentificador(
            idUsuario, "ID de usuario"
        );

        List<Calificacion> resultado = new ArrayList<>();

        for (Calificacion c : calificaciones) {
            if (c.getIdUsuario().equals(idUsuarioLimpio)) {
                resultado.add(c);
            }
        }

        return resultado;
    }


    @Override
    public synchronized List<Calificacion> listarPorCurso(
            String idUsuario, String idCurso) {

        String idUsuarioLimpio = validarIdentificador(
            idUsuario, "ID de usuario"
        );

        String idCursoLimpio = validarIdentificador(
            idCurso, "ID de curso"
        );

        List<Calificacion> resultado = new ArrayList<>();

        for (Calificacion c : calificaciones) {

            if (c.getIdUsuario().equals(idUsuarioLimpio)
                    && c.getIdCurso().equals(idCursoLimpio)) {

                resultado.add(c);
            }
        }

        return resultado;
    }

 
    @Override
    public synchronized Optional<Calificacion> buscarPorId(
            String idCalificacion) {

        String idCalificacionLimpio = validarIdentificador(
            idCalificacion, "ID de calificacion"
        );

        for (Calificacion c : calificaciones) {

            if (c.getIdCalificacion().equals(
                    idCalificacionLimpio)) {

                return Optional.of(c);
            }
        }

        return Optional.empty();
    }
}
