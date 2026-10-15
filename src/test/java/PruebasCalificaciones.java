
import modulos.calificaciones.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PruebasCalificaciones {

    public static void main(String[] args) {

        RepositorioCalificacionesMemoria repositorio =
                new RepositorioCalificacionesMemoria();

        ControladorCalificaciones controlador =
                new ControladorCalificaciones(repositorio);

        // 1. Registrar una calificacion
        Calificacion nota = controlador.registrarCalificacion(
            "usuario001", "curso001", "Matematica", "Parcial 1",
            new BigDecimal("85"), new BigDecimal("100"),
            LocalDate.now()
        );

        comprobar(nota.getNotaObtenida().compareTo(
            new BigDecimal("85")) == 0, "Nota incorrecta");

        // 2. Verificar almacenamiento
        comprobar(controlador.listarCalificaciones(
            "usuario001").size() == 1, "No se guardo la nota");

        // 3. Consultar por curso
        comprobar(controlador.listarCalificacionesPorCurso(
            "usuario001", "curso001").size() == 1,
            "Error al consultar por curso");

        // 4. Buscar por identificador
        comprobar(controlador.buscarCalificacion(
            nota.getIdCalificacion()).isPresent(),
            "No se encontro la calificacion");

        // 5. Verificar propietario
        comprobar(controlador.buscarCalificacionPorUsuario(
            "usuario002", nota.getIdCalificacion()).isEmpty(),
            "Un usuario ajeno no debe acceder a la nota");

        // 6. Rechazar calificaciones invalidas
        try {
            controlador.registrarCalificacion(
                "usuario001", "curso001", "Matematica", "Parcial 2",
                new BigDecimal("110"), new BigDecimal("100"),
                LocalDate.now()
            );
            throw new AssertionError("Se acepto una nota invalida");
        } catch (IllegalArgumentException esperado) {
            // Validacion correcta
        }

        // 7. Rechazar registros duplicados
        try {
            repositorio.guardar(nota);
            throw new AssertionError("Se acepto un registro duplicado");
        } catch (IllegalStateException esperado) {
            // Duplicado rechazado correctamente
        }

        System.out.println(
            "PruebasCalificaciones: todas las pruebas pasaron."
        );
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }
}
