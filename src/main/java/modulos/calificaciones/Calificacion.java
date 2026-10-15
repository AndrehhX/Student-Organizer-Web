
package modulos.calificaciones;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * RF03 - Registrar Calificaciones.
 *
 * Representa una calificacion academica de un estudiante.
 *
 * Responsabilidades:
 * - Almacenar los datos de una calificacion.
 * - Validar los datos recibidos.
 * - Proteger los identificadores.
 * - Permitir consultar y actualizar los datos permitidos.
 *
 * No realiza operaciones de base de datos ni calcula promedios.
 */
public class Calificacion {


    // ATRIBUTOS
    // =====================================================

    private final String idCalificacion;
    private final String idUsuario;

    private String idCurso;
    private String nombreCurso;
    private String nombreActividad;

    private BigDecimal notaObtenida;
    private BigDecimal notaMaxima;

    private LocalDate fecha;

    // CONSTRUCTOR NUEVAS CALIFICACIONES
    // ======================

    public Calificacion(
            String idUsuario,
            String idCurso,
            String nombreCurso,
            String nombreActividad,
            BigDecimal notaObtenida,
            BigDecimal notaMaxima,
            LocalDate fecha) {

        this(
            UUID.randomUUID().toString(),
            idUsuario,
            idCurso,
            nombreCurso,
            nombreActividad,
            notaObtenida,
            notaMaxima,
            fecha
        );
    }

    // =====
    // CONSTRUCTOR CALIFICACIONES EXISTENTES
    // =====================================

    public Calificacion(
            String idCalificacion,
            String idUsuario,
            String idCurso,
            String nombreCurso,
            String nombreActividad,
            BigDecimal notaObtenida,
            BigDecimal notaMaxima,
            LocalDate fecha) {

        this.idCalificacion = validarTexto(
            idCalificacion, "ID de calificacion"
        );

        this.idUsuario = validarTexto(
            idUsuario, "ID de usuario"
        );

        setIdCurso(idCurso);
        setNombreCurso(nombreCurso);
        setNombreActividad(nombreActividad);
        setCalificacion(notaObtenida, notaMaxima);
        setFecha(fecha);
    }

    
    // VALIDACION
    // =============
   
    private static String validarTexto(
            String valor, String campo) {

        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El campo " + campo + " es obligatorio."
            );
        }

        return valor.trim();
    }

    /**
     * Valida la calificacion y el punteo maximo.
     */
    public void setCalificacion(
            BigDecimal obtenida,
            BigDecimal maxima) {

        if (obtenida == null) {
            throw new IllegalArgumentException(
                "La nota obtenida es obligatoria."
            );
        }

        if (maxima == null) {
            throw new IllegalArgumentException(
                "El punteo maximo es obligatorio."
            );
        }

        if (maxima.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "El punteo maximo debe ser mayor que cero."
            );
        }

        if (obtenida.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                "La nota obtenida no puede ser negativa."
            );
        }

        if (obtenida.compareTo(maxima) > 0) {
            throw new IllegalArgumentException(
                "La nota obtenida no puede superar "
                + "el punteo maximo."
            );
        }

        this.notaObtenida = obtenida;
        this.notaMaxima = maxima;
    }

  
    // GETTERS
    // =============

    public String getIdCalificacion() {
        return idCalificacion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getIdCurso() {
        return idCurso;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public String getNombreActividad() {
        return nombreActividad;
    }

    public BigDecimal getNotaObtenida() {
        return notaObtenida;
    }

    public BigDecimal getNotaMaxima() {
        return notaMaxima;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    // ====
    // SETTERS Y SUS  VALIDACIONES
    // ===============

    public void setIdCurso(String idCurso) {
        this.idCurso = validarTexto(
            idCurso, "ID de curso"
        );
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = validarTexto(
            nombreCurso, "Nombre del curso"
        );
    }

    public void setNombreActividad(
            String nombreActividad) {

        this.nombreActividad = validarTexto(
            nombreActividad, "Nombre de actividad"
        );
    }

    public void setFecha(LocalDate fecha) {

        if (fecha == null) {
            throw new IllegalArgumentException(
                "La fecha es obligatoria."
            );
        }

        this.fecha = fecha;
    }

    // =======
    // REPRESE. DEL OBJETO
    // =========

    @Override
    public String toString() {
        return nombreCurso
            + " - "
            + nombreActividad
            + ": "
            + notaObtenida.toPlainString()
            + "/"
            + notaMaxima.toPlainString();
    }
}
