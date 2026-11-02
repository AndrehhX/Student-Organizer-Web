import modulos.Cursos.Curso;
import modulos.Cursos.RepositorioCursosMySQL;
import modulos.tareas.PersistenciaTareasMySQL;
import modulos.tareas.Tarea;
import persistencia.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class PruebaTareasMySQL {
    public static void main(String[] args) throws Exception {
        String clave = System.getenv("DB_PASSWORD");

        if (clave == null || clave.isBlank()) {
            throw new IllegalStateException("Falta configurar DB_PASSWORD.");
        }

        ConexionBD bd = new ConexionBD(
                "jdbc:mysql://127.0.0.1:3307/student_organizer",
                "organizer_app",
                clave
        );

        long usuarioId;

        String sql =
                "SELECT id FROM usuarios "
                + "WHERE carne = 'PRUEBA' ORDER BY id DESC LIMIT 1";

        try (Connection conexion = bd.abrir();
             PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet resultado = consulta.executeQuery()) {

            if (!resultado.next()) {
                throw new IllegalStateException(
                        "No se encontró el usuario de prueba.");
            }

            usuarioId = resultado.getLong("id");
        }

        // Las tareas ahora pertenecen a un curso del usuario.
        Curso curso = new RepositorioCursosMySQL(bd).guardar(
                usuarioId,
                "Curso prueba tareas " + System.currentTimeMillis()
        );

        Tarea tarea = new Tarea(
                "Tarea creada desde Java",
                "2026-10-30",
                curso.getIdCurso()
        );

        PersistenciaTareasMySQL persistencia =
                new PersistenciaTareasMySQL(bd);

        long tareaId = persistencia.guardarTarea(usuarioId, tarea);

        comprobar(tareaId > 0,
                "Debe devolver el ID de la tarea guardada.");

        List<Tarea> tareas = persistencia.consultarPorUsuario(usuarioId);
        Tarea guardada = tareas.stream()
                .filter(t -> t.getId() == tareaId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "La tarea guardada debe aparecer al consultarla."
                ));

        comprobar(
                guardada.getNombre().equals("Tarea creada desde Java"),
                "Debe conservar el nombre de la tarea."
        );
        comprobar(
                guardada.getCursoId() == curso.getIdCurso(),
                "Debe conservar el curso de la tarea."
        );
        comprobar(
                !guardada.isCompletada(),
                "La tarea debe iniciar pendiente."
        );

        comprobar(
                persistencia.marcarCompletada(usuarioId, tareaId),
                "Debe poder marcar la tarea como completada."
        );

        Tarea completada = persistencia.consultarPorUsuario(usuarioId)
                .stream()
                .filter(t -> t.getId() == tareaId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "La tarea completada debe seguir existiendo."
                ));

        comprobar(
                completada.isCompletada(),
                "La tarea debe quedar completada en MySQL."
        );

        System.out.println("Tarea guardada en MySQL. ID: " + tareaId);
        System.out.println("Usuario propietario. ID: " + usuarioId);
        System.out.println("Curso. ID: " + curso.getIdCurso());
    }

    private static void comprobar(
            boolean condicion,
            String mensaje
    ) {
        if (!condicion) {
            throw new IllegalStateException("FALLÓ: " + mensaje);
        }
    }
}
