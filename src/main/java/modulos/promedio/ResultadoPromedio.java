package modulos.promedio;

import java.math.BigDecimal;


public class ResultadoPromedio {


    // ATRIBUTOS


    private final String idCurso;
    private final String nombreCurso;
    private final BigDecimal promedio;
    private final int cantidadCalificaciones;


    // CONSTRUCTOR


    public ResultadoPromedio(
            String idCurso,
            String nombreCurso,
            BigDecimal promedio,
            int cantidadCalificaciones) {

        if (idCurso == null || idCurso.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El ID de curso es obligatorio."
            );
        }

        if (nombreCurso == null || nombreCurso.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El nombre del curso es obligatorio."
            );
        }

        if (promedio == null) {
            throw new IllegalArgumentException(
                "El promedio es obligatorio."
            );
        }

        if (cantidadCalificaciones <= 0) {
            throw new IllegalArgumentException(
                "Un promedio requiere al menos una calificacion."
            );
        }

        this.idCurso = idCurso.trim();
        this.nombreCurso = nombreCurso.trim();
        this.promedio = promedio;
        this.cantidadCalificaciones = cantidadCalificaciones;
    }

    // GETTERS
  

    public String getIdCurso() {
        return idCurso;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public BigDecimal getPromedio() {
        return promedio;
    }

    public int getCantidadCalificaciones() {
        return cantidadCalificaciones;
    }

   
    // REPRESENTACION DEL OBJETO
    

    @Override
    public String toString() {
        return nombreCurso
            + ": "
            + promedio.toPlainString()
            + " ("
            + cantidadCalificaciones
            + " calificaciones)";
    }
}