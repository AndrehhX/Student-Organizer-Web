package persistencia;

import modulos.Cursos.RepositorioCursosMySQL;
import modulos.calificaciones.ControladorCalificaciones;
import modulos.calificaciones.RepositorioCalificacionesMySQL;
import modulos.promedio.ControladorPromedios;
import modulos.tareas.ControladorTareasMySQL;

/**
 * Prepara los componentes de la aplicación que utilizan MySQL.
 *
 * Responsabilidad:
 * - Centralizar la configuración de conexión.
 * - Construir los repositorios y controladores.
 * - Compartir el repositorio de calificaciones con promedio.
 *
 * No inicia el servidor ni modifica los formularios.
 * No almacena un usuario actual: el backend debe proporcionar
 * el ID del usuario autenticado en cada operación.
 */
public final class ServiciosMySQL {

    private final RepositorioUsuariosMySQL usuarios;
    private final RepositorioCursosMySQL cursos;
    private final ControladorTareasMySQL tareas;
    private final RepositorioCalificacionesMySQL repositorioCalificaciones;
    private final ControladorCalificaciones calificaciones;
    private final ControladorPromedios promedios;

    /**
     * Construye los servicios con una configuración común.
     * Cada repositorio abre y cierra sus conexiones al operar.
     */
    public ServiciosMySQL(ConexionBD bd) {
        if (bd == null) {
            throw new IllegalArgumentException(
                    "La configuración de MySQL es obligatoria."
            );
        }

        usuarios = new RepositorioUsuariosMySQL(bd);
        cursos = new RepositorioCursosMySQL(bd);
        tareas = new ControladorTareasMySQL(bd);

        repositorioCalificaciones =
                new RepositorioCalificacionesMySQL(bd);

        calificaciones =
                new ControladorCalificaciones(repositorioCalificaciones);

        promedios =
                new ControladorPromedios(repositorioCalificaciones);
    }

    /**
     * Lee la configuración del entorno donde se ejecuta Java.
     *
     * DB_PASSWORD: obligatoria.
     * DB_HOST: opcional; por defecto 127.0.0.1.
     *
     * Crear los servicios no inserta datos ni crea tablas.
     */
    public static ServiciosMySQL desdeEntorno() {
        String clave = System.getenv("DB_PASSWORD");

        if (clave == null || clave.isBlank()) {
            throw new IllegalStateException(
                    "Falta configurar DB_PASSWORD."
            );
        }

        String host = System.getenv("DB_HOST");

        if (host == null || host.isBlank()) {
            host = "127.0.0.1";
        }

        ConexionBD bd = new ConexionBD(
                "jdbc:mysql://" + host.trim()
                        + ":3307/student_organizer",
                "organizer_app",
                clave
        );

        return new ServiciosMySQL(bd);
    }

    public RepositorioUsuariosMySQL getUsuarios() {
        return usuarios;
    }

    public RepositorioCursosMySQL getCursos() {
        return cursos;
    }

    public ControladorTareasMySQL getTareas() {
        return tareas;
    }

    public ControladorCalificaciones getCalificaciones() {
        return calificaciones;
    }

    public RepositorioCalificacionesMySQL getRepositorioCalificaciones() {
        return repositorioCalificaciones;
    }

    public ControladorPromedios getPromedios() {
        return promedios;
    }
}
