package modulos.promedio;

import modulos.calificaciones.Calificacion;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class CalculadoraPromedios {

    // CONFIGURACION DEL CALCULO


    public static final int ESCALA = 2;
    public static final RoundingMode REDONDEO = RoundingMode.HALF_UP;

    private static final BigDecimal CIEN = new BigDecimal("100");

   
    // PROMEDIO DE UN CONJUNTO DE CALIFICACIONES
 
    /**
     *
     * @return Optional vacio si no hay calificaciones.
     */
    public Optional<BigDecimal> calcularPromedio(
            List<Calificacion> calificaciones) {

        validarLista(calificaciones);

        if (calificaciones.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal sumaObtenida = BigDecimal.ZERO;
        BigDecimal sumaMaxima = BigDecimal.ZERO;

        for (Calificacion c : calificaciones) {
            sumaObtenida = sumaObtenida.add(c.getNotaObtenida());
            sumaMaxima = sumaMaxima.add(c.getNotaMaxima());
        }

        // Calificacion ya garantiza notaMaxima > 0,
        // pero se protege la division de todas formas.
        if (sumaMaxima.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        }

        BigDecimal promedio = sumaObtenida
            .multiply(CIEN)
            .divide(sumaMaxima, ESCALA, REDONDEO);

        return Optional.of(promedio);
    }


    // PROMEDIOS POR CURSO


    /**
     * Agrupa las calificaciones por idCurso y calcula
     * el promedio de cada curso.
     *
     * @return lista vacia si no hay calificaciones.
     */
    public List<ResultadoPromedio> calcularPromediosPorCurso(
            List<Calificacion> calificaciones) {

        validarLista(calificaciones);

        // LinkedHashMap conserva el orden en que llegaron los cursos.
        Map<String, List<Calificacion>> porCurso = new LinkedHashMap<>();

        for (Calificacion c : calificaciones) {
            porCurso
                .computeIfAbsent(c.getIdCurso(), id -> new ArrayList<>())
                .add(c);
        }

        List<ResultadoPromedio> resultados = new ArrayList<>();

        for (Map.Entry<String, List<Calificacion>> curso
                : porCurso.entrySet()) {

            List<Calificacion> delCurso = curso.getValue();

            // Nunca esta vacia: cada grupo tiene al menos una nota.
            BigDecimal promedio = calcularPromedio(delCurso).orElseThrow();

            resultados.add(new ResultadoPromedio(
                curso.getKey(),
                delCurso.get(0).getNombreCurso(),
                promedio,
                delCurso.size()
            ));
        }

        return resultados;
    }


    // PROMEDIO GENERAL


    /**
     * Promedio simple de los promedios por curso
     * (los mismos valores redondeados que ve el estudiante).
     *
     * @return Optional vacio si no hay calificaciones.
     */
    public Optional<BigDecimal> calcularPromedioGeneral(
            List<Calificacion> calificaciones) {

        List<ResultadoPromedio> porCurso =
            calcularPromediosPorCurso(calificaciones);

        if (porCurso.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal suma = BigDecimal.ZERO;

        for (ResultadoPromedio resultado : porCurso) {
            suma = suma.add(resultado.getPromedio());
        }

        BigDecimal general = suma.divide(
            BigDecimal.valueOf(porCurso.size()),
            ESCALA,
            REDONDEO
        );

        return Optional.of(general);
    }


    // VALIDACION AUXILIAR
 

    private static void validarLista(List<Calificacion> calificaciones) {

        if (calificaciones == null) {
            throw new IllegalArgumentException(
                "La lista de calificaciones es obligatoria."
            );
        }

        for (Calificacion c : calificaciones) {
            if (c == null) {
                throw new IllegalArgumentException(
                    "La lista no puede contener calificaciones nulas."
                );
            }
        }
    }
}