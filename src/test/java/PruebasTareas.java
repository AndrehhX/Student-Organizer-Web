import modulos.tareas.PersistenciaTareas;
import modulos.tareas.Tarea;
import modulos.tareas.controladorTareas;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class PruebasTareas {

    public static void main(String[] args) throws Exception {
        Path carpetaTemporal =
                Files.createTempDirectory("student-organizer-tareas-");

        try {
            Path archivo = carpetaTemporal.resolve("tareas.csv");
            PersistenciaTareas persistencia =
                    new PersistenciaTareas(archivo);
            controladorTareas controlador =
                    new controladorTareas(persistencia);

            comprobar(controlador.getListaTareas().isEmpty(),
                    "Debe iniciar sin tareas");

            controlador.registrartarea(
                    "Ensayo de historia", "2026-10-20", "alta"
            );

            comprobar(Files.exists(archivo),
                    "Debe crear el CSV al registrar una tarea");
            comprobar(controlador.getListaTareas().size() == 1,
                    "Debe conservar la tarea registrada");

            controladorTareas recuperado =
                    new controladorTareas(persistencia);

            comprobar(recuperado.getListaTareas().size() == 1,
                    "Debe recuperar la tarea del CSV");
            comprobar(recuperado.getListaTareas().get(0)
                            .getNombre().equals("Ensayo de historia"),
                    "Debe recuperar el nombre correcto");

            String contenidoAnterior = Files.readString(archivo);

            esperarRechazo(
                    () -> controlador.registrartarea(
                            "", "2026-10-20", "alta"),
                    "Debe rechazar nombre vacío"
            );
            esperarRechazo(
                    () -> controlador.registrartarea(
                            "Ensayo", "", "alta"),
                    "Debe rechazar fecha vacía"
            );
            esperarRechazo(
                    () -> controlador.registrartarea(
                            "Ensayo", "2026-10-20", null),
                    "Debe rechazar prioridad nula"
            );
            esperarRechazo(
                    () -> new Tarea("", "2026-10-20", "alta"),
                    "Tarea también debe rechazar nombre vacío"
            );

            comprobar(Files.readString(archivo).equals(contenidoAnterior),
                    "Datos incompletos no deben modificar el CSV");

            Tarea tarea = recuperado.getListaTareas().get(0);
            recuperado.marcarCompletada(tarea);

            controladorTareas comprobacion =
                    new controladorTareas(persistencia);

            comprobar(comprobacion.getListaTareas().get(0).isCompletada(),
                    "Debe guardar el estado completado");
            comprobar(comprobacion.consultarPendientes().isEmpty(),
                    "No debe mostrar tareas completadas como pendientes");

            System.out.println(
                    "PruebasTareas: todas las pruebas pasaron."
            );
        } finally {
            try (var archivos = Files.walk(carpetaTemporal)) {
                archivos.sorted(Comparator.reverseOrder())
                        .forEach(archivo -> {
                            try {
                                Files.deleteIfExists(archivo);
                            } catch (Exception error) {
                                throw new RuntimeException(error);
                            }
                        });
            }
        }
    }

    private static void comprobar(boolean resultado, String mensaje) {
        if (!resultado) {
            throw new AssertionError(mensaje);
        }
    }

    private static void esperarRechazo(Runnable accion, String mensaje) {
        try {
            accion.run();
        } catch (IllegalArgumentException esperado) {
            return;
        }
        throw new AssertionError(mensaje);
    }
}