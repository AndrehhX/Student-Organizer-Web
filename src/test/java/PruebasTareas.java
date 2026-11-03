import modulos.tareas.Tarea;

public class PruebasTareas {

    public static void main(String[] args) {
        Tarea tarea = new Tarea(
                "Ensayo de historia",
                "2026-10-20",
                1
        );

        comprobar(
                tarea.getNombre().equals("Ensayo de historia"),
                "Debe guardar el nombre"
        );

        comprobar(
                tarea.getfechaEntrega().equals("2026-10-20"),
                "Debe guardar la fecha"
        );

        comprobar(
                tarea.getCursoId() == 1,
                "Debe guardar el curso"
        );

        tarea.setPrioridad("alta");

        comprobar(
                tarea.getPrioridad().equals("alta"),
                "Debe guardar la prioridad"
        );

        comprobar(
                !tarea.isCompletada(),
                "La tarea debe iniciar pendiente"
        );

        tarea.marcarComoCompletada();

        comprobar(
                tarea.isCompletada(),
                "La tarea debe marcarse como completada"
        );

        System.out.println(
                "PruebasTareas: todas las pruebas pasaron."
        );
    }

    private static void comprobar(
            boolean resultado,
            String mensaje
    ) {
        if (!resultado) {
            throw new AssertionError(mensaje);
        }
    }
}
