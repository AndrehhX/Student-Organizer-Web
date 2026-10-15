package modulos.promedio;

import modulos.calificaciones.Calificacion;
import modulos.calificaciones.RepositorioCalificaciones;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public class ControladorPromedios {

 
    // ATRIBUTOS
  

    private final RepositorioCalificaciones repositorio;
    private final CalculadoraPromedios calculadora;

   
    // CONSTRUCTOR
   

  
    public ControladorPromedios(RepositorioCalificaciones repositorio) {

        if (repositorio == null) {
            throw new IllegalArgumentException(
                "El repositorio de calificaciones es obligatorio."
            );
        }

        this.repositorio = repositorio;
        this.calculadora = new CalculadoraPromedios();
    }


    // PROMEDIO GENERAL DEL ESTUDIANTE


    /**
     * @return Optional vacio si el estudiante no tiene calificaciones.
     */
    public Optional<BigDecimal> obtenerPromedioGeneral(String idUsuario) {

        List<Calificacion> calificaciones =
            consultarPorUsuario(idUsuario);

        return calculadora.calcularPromedioGeneral(calificaciones);
    }

    // PROMEDIOS POR CURSO DEL ESTUDIANTE

    /**
     * @return lista vacia si el estudiante no tiene calificaciones.
     */
    public List<ResultadoPromedio> obtenerPromediosPorCurso(
            String idUsuario) {

        List<Calificacion> calificaciones =
            consultarPorUsuario(idUsuario);

        return calculadora.calcularPromediosPorCurso(calificaciones);
    }


    // PROMEDIO DE UN CURSO ESPECIFICO

    /**
     * @return Optional vacio si el curso no tiene calificaciones.
     */
    public Optional<BigDecimal> obtenerPromedioCurso(
            String idUsuario,
            String idCurso) {

        String usuarioValidado = validarIdentificador(
            idUsuario, "ID de usuario"
        );

        String cursoValidado = validarIdentificador(
            idCurso, "ID de curso"
        );

        List<Calificacion> calificaciones =
            repositorio.listarPorCurso(usuarioValidado, cursoValidado);

        verificarRespuesta(calificaciones);

        return calculadora.calcularPromedio(calificaciones);
    }


    // CONSULTA AUXILIAR
  

    private List<Calificacion> consultarPorUsuario(String idUsuario) {

        String usuarioValidado = validarIdentificador(
            idUsuario, "ID de usuario"
        );

        List<Calificacion> calificaciones =
            repositorio.listarPorUsuario(usuarioValidado);

        verificarRespuesta(calificaciones);

        return calificaciones;
    }


    // VALIDACIONES AUXILIARES


    private static String validarIdentificador(
            String valor, String campo) {

        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "El " + campo + " es obligatorio."
            );
        }

        return valor.trim();
    }


    private static void verificarRespuesta(
            List<Calificacion> calificaciones) {

        if (calificaciones == null) {
            throw new IllegalStateException(
                "El repositorio de calificaciones no devolvio resultados."
            );
        }
    }
}