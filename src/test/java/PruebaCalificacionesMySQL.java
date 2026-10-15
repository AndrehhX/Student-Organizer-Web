import modulos.Cursos.Curso;
import modulos.Cursos.RepositorioCursosMySQL;
import modulos.calificaciones.Calificacion;
import modulos.calificaciones.ControladorCalificaciones;
import modulos.calificaciones.RepositorioCalificacionesMySQL;
import persistencia.ConexionBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Prueba manual de RepositorioCalificacionesMySQL 
 * Requiere el usuario con carné PRUEBA (sql/pruebas.sql)
 * y haber ejecutado sql/002_cursos_y_calificaciones.sql.
 */
public class PruebaCalificacionesMySQL {

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

        long usuarioId = buscarUsuarioPrueba(bd);

        RepositorioCursosMySQL cursos = new RepositorioCursosMySQL(bd);
        Curso curso = cursos.guardar(
                usuarioId,
                "Curso prueba calificaciones " + System.currentTimeMillis()
        );

        ControladorCalificaciones controlador =
                new ControladorCalificaciones(
                        new RepositorioCalificacionesMySQL(bd)
                );

        String idUsuario = String.valueOf(usuarioId);
        String idCurso = String.valueOf(curso.getIdCurso());

        // 1. Guardar
        Calificacion guardada = controlador.registrarCalificacion(
                idUsuario,
                idCurso,
                curso.getNombreCurso(),
                "Parcial 1",
                new BigDecimal("85.50"),
                new BigDecimal("100"),
                LocalDate.of(2026, 10, 15)
        );
        System.out.println("Calificación guardada. ID: "
                + guardada.getIdCalificacion());

        // 2. Recuperar por usuario
        List<Calificacion> deUsuario =
                controlador.listarCalificaciones(idUsuario);
        comprobar(
                deUsuario.stream().anyMatch(c ->
                        c.getIdCalificacion()
                                .equals(guardada.getIdCalificacion())),
                "La calificación debe aparecer al listar por usuario."
        );

        // 3. Recuperar por curso (y que el nombre venga del JOIN)
        List<Calificacion> deCurso =
                controlador.listarCalificacionesPorCurso(
                        idUsuario, idCurso
                );
        comprobar(deCurso.size() == 1,
                "El curso nuevo debe tener exactamente 1 calificación.");
        comprobar(
                deCurso.get(0).getNombreCurso()
                        .equals(curso.getNombreCurso()),
                "El nombre del curso debe venir del JOIN."
        );
        comprobar(
                deCurso.get(0).getNotaObtenida()
                        .compareTo(new BigDecimal("85.50")) == 0,
                "La nota obtenida debe conservarse."
        );

        // 4. Buscar por ID, con el usuario correcto y con otro
        Optional<Calificacion> propia =
                controlador.buscarCalificacionPorUsuario(
                        idUsuario, guardada.getIdCalificacion()
                );
        comprobar(propia.isPresent(),
                "Debe encontrarla con el usuario dueño.");

        Optional<Calificacion> ajena =
                controlador.buscarCalificacionPorUsuario(
                        String.valueOf(usuarioId + 999999),
                        guardada.getIdCalificacion()
                );
        comprobar(ajena.isEmpty(),
                "No debe entregarla a otro usuario.");

        // 5. Curso que no existe para este usuario
        try {
            controlador.registrarCalificacion(
                    idUsuario,
                    String.valueOf(curso.getIdCurso() + 999999),
                    "Curso falso",
                    "Tarea",
                    new BigDecimal("5"),
                    new BigDecimal("10"),
                    LocalDate.of(2026, 10, 15)
            );
            throw new IllegalStateException(
                    "Debió rechazar un curso inexistente."
            );
        } catch (IllegalArgumentException esperado) {
            System.out.println("Curso inexistente rechazado: OK");
        }

        // 6. ID que no es número (por ejemplo, un carné)
        try {
            controlador.listarCalificaciones("PRUEBA");
            throw new IllegalStateException(
                    "Debió rechazar un ID de usuario no numérico."
            );
        } catch (IllegalArgumentException esperado) {
            System.out.println("ID no numérico rechazado: OK");
        }

        System.out.println("Todas las comprobaciones pasaron.");
    }

    private static long buscarUsuarioPrueba(ConexionBD bd)
            throws Exception {
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

            return resultado.getLong("id");
        }
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new IllegalStateException("FALLÓ: " + mensaje);
        }
    }
}