
package modulos.calificaciones;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * RF03 - Registrar Calificaciones.
 *
 * Controlador encargado de administrar el registro
 * y consulta de calificaciones academicas.
 *
 * Responsabilidades:
 * - Registrar calificaciones.
 * - Validar identificadores de consulta.
 * - Consultar calificaciones por estudiante.
 * - Consultar calificaciones por curso.
 * - Buscar calificaciones por identificador.
 * - Verificar la pertenencia de una calificacion
 *   al usuario solicitado.
 *
 * No realiza conexiones a bases de datos
 * ni calcula promedios.
 */
public class ControladorCalificaciones {

    //======
    // ATRIBUTOS
    // ============
    private final RepositorioCalificaciones repositorio;

    // ===================
    // CONSTRUCTOR
    // ===================================

    public ControladorCalificaciones(
            RepositorioCalificaciones repositorio) {

        if (repositorio == null) {
            throw new IllegalArgumentException(
                "El repositorio de calificaciones es obligatorio."
            );
        }

        this.repositorio = repositorio;
    }

    // ====================
    // VALIDACIONES AUXILIARES
    // =====================================

    private static String validarIdentificador(
            String valor, String campo) {

        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El " + campo + " es obligatorio."
            );
        }

        return valor.trim();
    }

    // =======================
    // REGISTRO DE CALIFICACIONES
    // ====================


    public Calificacion registrarCalificacion(
            String idUsuario,
            String idCurso,
            String nombreCurso,
            String nombreActividad,
            BigDecimal notaObtenida,
            BigDecimal notaMaxima,
            LocalDate fecha) {

        Calificacion nuevaCalificacion = new Calificacion(
            idUsuario,
            idCurso,
            nombreCurso,
            nombreActividad,
            notaObtenida,
            notaMaxima,
            fecha
        );

        repositorio.guardar(nuevaCalificacion);

        return nuevaCalificacion;
    }

    
    // CONSULTAR CALIFICACIONES POR USUARIO
    // =====================================================

  
    public List<Calificacion> listarCalificaciones(
            String idUsuario) {

        String usuarioValidado = validarIdentificador(
            idUsuario, "ID de usuario"
        );

        return repositorio.listarPorUsuario(usuarioValidado);
    }

   
    // CONSULTAR CALIFICACIONES POR CURSO
    // =============================

    public List<Calificacion> listarCalificacionesPorCurso(
            String idUsuario,
            String idCurso) {

        String usuarioValidado = validarIdentificador(
            idUsuario, "ID de usuario"
        );

        String cursoValidado = validarIdentificador(
            idCurso, "ID de curso"
        );

        return repositorio.listarPorCurso(
            usuarioValidado,
            cursoValidado
        );
    }

    // =====================================================
    // BUSCAR CALIFICACION POR ID

    public Optional<Calificacion> buscarCalificacion(
            String idCalificacion) {

        String idValidado = validarIdentificador(
            idCalificacion, "ID de calificacion"
        );

        return repositorio.buscarPorId(idValidado);
    }

    // =====================================================
    // BUSCAR CALIFICACION POR ID Y USUARIO
    // =======

    /**
     * Busca una calificacion y comprueba que pertenezca
     * al usuario indicado.
     */
    public Optional<Calificacion> buscarCalificacionPorUsuario(
            String idUsuario,
            String idCalificacion) {

        String usuarioValidado = validarIdentificador(
            idUsuario, "ID de usuario"
        );

        String calificacionValidada = validarIdentificador(
            idCalificacion, "ID de calificacion"
        );

        return repositorio.buscarPorId(calificacionValidada)
            .filter(calificacion ->
                calificacion.getIdUsuario().equals(
                    usuarioValidado
                )
            );
    }
}
