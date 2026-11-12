
import modulos.calificaciones.*;
import modulos.promedio.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PruebasPromedios {

    public static void main(String[] args) {

        RepositorioCalificacionesMemoria repositorio =
                new RepositorioCalificacionesMemoria();

        ControladorCalificaciones calificaciones =
                new ControladorCalificaciones(repositorio);

        ControladorPromedios promedios =
                new ControladorPromedios(repositorio);

        CalculadoraPromedios calculadora = new CalculadoraPromedios();

        // 1. Estudiante sin calificaciones: no hay promedio
        comprobar(promedios.obtenerPromedioGeneral("usuario001").isEmpty(),
            "Sin notas no debe existir promedio");

        // 2. Lista vacia en la calculadora
        comprobar(calculadora.calcularPromedio(new ArrayList<>()).isEmpty(),
            "Una lista vacia no debe tener promedio");

        // Registrar notas con el controlador de Fabricio
        registrar(calificaciones, "usuario001", "curso001", "Matematica", "12", "15");
        registrar(calificaciones, "usuario001", "curso001", "Matematica", "5", "5");
        registrar(calificaciones, "usuario001", "curso002", "Quimica", "9", "10");
        registrar(calificaciones, "usuario002", "curso002", "Quimica", "5", "10");

        // 3. Promedio de un curso con varias notas: (12 + 5) / (15 + 5) = 85
        //    (si se promediaran porcentajes daria 90: esto prueba la formula)
        comprobar(igual(promedios.obtenerPromedioCurso(
            "usuario001", "curso001").get(), "85.00"),
            "Promedio de Matematica incorrecto");

        // 4. Promedio general: (85 + 90) / 2 = 87.5
        comprobar(igual(promedios.obtenerPromedioGeneral(
            "usuario001").get(), "87.50"),
            "Promedio general incorrecto");

        // 5. Promedios por curso
        comprobar(promedios.obtenerPromediosPorCurso(
            "usuario001").size() == 2,
            "Deben existir 2 cursos");

        // 6. Cada estudiante ve solo sus notas
        comprobar(igual(promedios.obtenerPromedioGeneral(
            "usuario002").get(), "50.00"),
            "Se mezclaron notas de otro estudiante");

        // 7. Curso sin calificaciones
        comprobar(promedios.obtenerPromedioCurso(
            "usuario001", "curso999").isEmpty(),
            "Un curso sin notas no debe tener promedio");

        // 8. Redondeo a 2 decimales: 2 / 3 = 66.67
        List<Calificacion> lista = new ArrayList<>();
        lista.add(nueva("Matematica", "2", "3"));
        comprobar(igual(calculadora.calcularPromedio(lista).get(), "66.67"),
            "Redondeo incorrecto");

        // 9. Rechazar usuario vacio
        try {
            promedios.obtenerPromedioGeneral("");
            throw new AssertionError("Se acepto un usuario vacio");
        } catch (IllegalArgumentException esperado) {
            // Validacion correcta
        }

        // 10. Punteos maximos distintos: (5 + 15) / (5 + 30) = 57.14
        //     (promediando porcentajes daria 75: el parcial de 30 pesa mas)
        List<Calificacion> distintos = new ArrayList<>();
        distintos.add(nueva("Matematica", "5", "5"));
        distintos.add(nueva("Matematica", "15", "30"));
        comprobar(igual(calculadora.calcularPromedio(distintos).get(), "57.14"),
            "El punteo maximo debe funcionar como peso");

        // 11. Redondeo HALF_UP: 1 / 32 = 3.125 -> 3.13
        List<Calificacion> mitad = new ArrayList<>();
        mitad.add(nueva("Matematica", "1", "32"));
        comprobar(igual(calculadora.calcularPromedio(mitad).get(), "3.13"),
            "El redondeo debe ser HALF_UP");

        // 12. El calculo no modifica las calificaciones originales
        Calificacion original = nueva("Matematica", "12", "15");
        List<Calificacion> unaNota = new ArrayList<>();
        unaNota.add(original);
        calculadora.calcularPromedioGeneral(unaNota);
        comprobar(unaNota.size() == 1 && unaNota.get(0) == original
            && igual(original.getNotaObtenida(), "12")
            && igual(original.getNotaMaxima(), "15"),
            "El calculo no debe modificar las notas");

        // 13. Una nota nueva se refleja sin guardar copias: (9 + 7) / 20 = 80
        registrar(calificaciones, "usuario001", "curso002", "Quimica", "7", "10");
        comprobar(igual(promedios.obtenerPromedioCurso(
            "usuario001", "curso002").get(), "80.00"),
            "El promedio debe actualizarse con notas nuevas");

        // 14. Rechazar una lista nula en la calculadora
        try {
            calculadora.calcularPromedio(null);
            throw new AssertionError("Se acepto una lista nula");
        } catch (IllegalArgumentException esperado) {
            // Validacion correcta
        }

        // 15. Error claro si el repositorio devuelve null
        RepositorioCalificaciones defectuoso = new RepositorioCalificaciones() {
            public void guardar(Calificacion c) { }
            public List<Calificacion> listarPorUsuario(String u) { return null; }
            public List<Calificacion> listarPorCurso(String u, String c) { return null; }
            public Optional<Calificacion> buscarPorId(String id) { return Optional.empty(); }
        };
        try {
            new ControladorPromedios(defectuoso).obtenerPromedioGeneral("usuario001");
            throw new AssertionError("Se acepto una respuesta nula del repositorio");
        } catch (IllegalStateException esperado) {
            // Validacion correcta
        }

        System.out.println(
            "PruebasPromedios: todas las pruebas pasaron."
        );
    }

    private static void registrar(ControladorCalificaciones controlador,
            String usuario, String idCurso, String curso,
            String obtenida, String maxima) {

        controlador.registrarCalificacion(
            usuario, idCurso, curso, "Actividad",
            new BigDecimal(obtenida), new BigDecimal(maxima),
            LocalDate.now()
        );
    }

    private static Calificacion nueva(String curso,
            String obtenida, String maxima) {

        return new Calificacion(
            "usuario001", "curso001", curso, "Actividad",
            new BigDecimal(obtenida), new BigDecimal(maxima),
            LocalDate.now()
        );
    }

    private static boolean igual(BigDecimal valor, String esperado) {
        return valor.compareTo(new BigDecimal(esperado)) == 0;
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}