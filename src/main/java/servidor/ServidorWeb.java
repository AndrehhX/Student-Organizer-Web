package servidor;
import modelo.Usuario;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import controlador.ControladorLogin;
import modulos.calificaciones.Calificacion;
import modulos.calificaciones.ControladorCalificaciones;
import modulos.calificaciones.RepositorioCalificacionesMemoria;
import modulos.tareas.Tarea;
import modulos.tareas.controladorTareas;
import java.util.Locale;
import java.util.UUID;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServidorWeb {
    private final ControladorLogin controladorLogin;
    private final Path carpetaPublica;
    private final int puerto;
    private final controladorTareas controladorTareas;
    private final ControladorCalificaciones controladorCalificaciones;
    private HttpServer servidor;

    public ServidorWeb(ControladorLogin controladorLogin, Path carpetaPublica, int puerto) {
        this(controladorLogin, carpetaPublica, puerto, new controladorTareas());
    }

    public ServidorWeb(ControladorLogin controladorLogin, Path carpetaPublica,
                       int puerto, controladorTareas controladorTareas) {
        this.controladorLogin = controladorLogin;
        this.carpetaPublica = carpetaPublica.toAbsolutePath().normalize();
        this.puerto = puerto;
        this.controladorTareas = controladorTareas;
        this.controladorCalificaciones =
        new ControladorCalificaciones(
            new RepositorioCalificacionesMemoria()
        );
    }


    // =====================================================
    // INICIAR SERVIDOR WEB
    // =====================================================


    public void iniciar() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress(puerto), 0);

        // LOGIN
        servidor.createContext("/api/login", this::manejarLogin);
        servidor.createContext("/api/logout", this::manejarLogout);

        // TAREAS
        servidor.createContext("/api/tareas/completar", this::completarTarea);
        servidor.createContext("/api/tareas", this::manejarTareas);

        // RF03 - Calificaciones
        servidor.createContext(
            "/api/calificaciones",
            this::manejarCalificaciones
        );

        // RF03 - Cursos generados desde Calificaciones
        servidor.createContext(
            "/api/cursos",
            this::manejarCursos
        );
        servidor.createContext("/", this::servirArchivo);

        servidor.start();
    }


    // =====================================================
    // RF03 - CONSULTAR CURSOS DESDE CALIFICACIONES
    // =====================================================

    private void manejarCursos(HttpExchange intercambio)
            throws IOException {

        if (!"GET".equalsIgnoreCase(
                intercambio.getRequestMethod())) {

            responder(intercambio, 405,
                "{\"mensaje\":\"Método no permitido.\"}");
            return;
        }

        Usuario usuario = controladorLogin.getUsuarioActual();

        if (usuario == null) {
            responder(intercambio, 401,
                "{\"mensaje\":\"Debes iniciar sesión.\"}");
            return;
        }

        String idUsuario = usuario.getCarne();

        List<Calificacion> calificaciones =
            controladorCalificaciones.listarCalificaciones(
                idUsuario
            );

        // Evitar cursos duplicados
        Map<String, String> cursos =
            new java.util.LinkedHashMap<>();

        for (Calificacion calificacion : calificaciones) {
            cursos.putIfAbsent(
                calificacion.getIdCurso(),
                calificacion.getNombreCurso()
            );
        }

        StringBuilder json = new StringBuilder("[");

        boolean primero = true;

        for (Map.Entry<String, String> curso : cursos.entrySet()) {

            if (!primero) {
                json.append(",");
            }

            json.append("{")
                .append("\"idCurso\":\"")
                .append(escaparJson(curso.getKey()))
                .append("\",\"nombreCurso\":\"")
                .append(escaparJson(curso.getValue()))
                .append("\"}");

            primero = false;
        }

        json.append("]");

        responder(intercambio, 200, json.toString());
    }


    // =====================================================
    // RF03 - REGISTRAR Y CONSULTAR CALIFICACIONES
    // =====================================================

    private void manejarCalificaciones(HttpExchange intercambio)
            throws IOException {

        String metodo = intercambio.getRequestMethod();

        if (!"GET".equalsIgnoreCase(metodo)
                && !"POST".equalsIgnoreCase(metodo)) {

            responder(intercambio, 405,
                "{\"mensaje\":\"Método no permitido.\"}");
            return;
        }

        // Identificacion temporal para pruebas locales.
        // Pendiente: sesiones independientes por navegador.
        Usuario usuario = controladorLogin.getUsuarioActual();

        if (usuario == null) {
            responder(intercambio, 401,
                "{\"mensaje\":\"Debes iniciar sesión.\"}");
            return;
        }

        String idUsuario = usuario.getCarne();

        // =================================================
        // CONSULTAR CALIFICACIONES
        // =================================================

        if ("GET".equalsIgnoreCase(metodo)) {

            List<Calificacion> calificaciones =
                controladorCalificaciones.listarCalificaciones(
                    idUsuario
                );

            responder(intercambio, 200,
                convertirCalificacionesAJson(calificaciones));

            return;
        }

        // =================================================
        // REGISTRAR CALIFICACION
        // =================================================

        try {
            String tipo = intercambio.getRequestHeaders()
                .getFirst("Content-Type");

            if (tipo == null || !tipo.toLowerCase()
                    .startsWith("application/x-www-form-urlencoded")) {

                responder(intercambio, 415,
                    "{\"mensaje\":\"Formato no admitido.\"}");
                return;
            }

            Map<String, String> datos =
                leerFormulario(leerCuerpo(intercambio));

            String nombreCurso = datos.get("nombreCurso");
            String nombreActividad = datos.get("nombreActividad");

            if (nombreCurso == null || nombreActividad == null
                    || datos.get("notaObtenida") == null
                    || datos.get("notaMaxima") == null
                    || datos.get("fecha") == null) {
                responder(intercambio, 400,
                    "{\"exitoso\":false,\"mensaje\":\"Faltan datos de la calificación.\"}");
                return;
            }

            // Normalizar el nombre del curso
            nombreCurso = nombreCurso.trim().replaceAll("\\s+", " ");

            // Generar un identificador estable por usuario y curso
            String claveCurso = idUsuario + "\u0000"
                    + nombreCurso.toLowerCase(Locale.ROOT);

            String idCurso = UUID.nameUUIDFromBytes(
                claveCurso.getBytes(StandardCharsets.UTF_8)
            ).toString();


            BigDecimal notaObtenida =
                new BigDecimal(datos.get("notaObtenida"));

            BigDecimal notaMaxima =
                new BigDecimal(datos.get("notaMaxima"));

            LocalDate fecha =
                LocalDate.parse(datos.get("fecha"));

            Calificacion registrada =
                controladorCalificaciones.registrarCalificacion(
                    idUsuario,
                    idCurso,
                    nombreCurso,
                    nombreActividad,
                    notaObtenida,
                    notaMaxima,
                    fecha
                );

            responder(intercambio, 201,
                "{\"exitoso\":true,\"mensaje\":\"Calificación registrada.\","
                + "\"idCalificacion\":\""
                + escaparJson(registrada.getIdCalificacion())
                + "\"}");

        } catch (IllegalArgumentException error) {

            responder(intercambio, 400,
                "{\"exitoso\":false,\"mensaje\":\"Datos de calificación inválidos.\"}");

        } catch (java.time.DateTimeException error) {

            responder(intercambio, 400,
                "{\"exitoso\":false,\"mensaje\":\"Fecha inválida.\"}");

        } catch (IllegalStateException error) {

            responder(intercambio, 409,
                "{\"exitoso\":false,\"mensaje\":\"La calificación ya existe.\"}");
        }
    }

    // =====================================================
    // RF03 - CONVERTIR CALIFICACIONES A JSON
    // =====================================================

    private String convertirCalificacionesAJson(
            List<Calificacion> calificaciones) {

        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < calificaciones.size(); i++) {

            if (i > 0) {
                json.append(",");
            }

            Calificacion c = calificaciones.get(i);

            json.append("{")
                .append("\"idCalificacion\":\"")
                .append(escaparJson(c.getIdCalificacion()))
                .append("\",\"idCurso\":\"")
                .append(escaparJson(c.getIdCurso()))
                .append("\",\"nombreCurso\":\"")
                .append(escaparJson(c.getNombreCurso()))
                .append("\",\"nombreActividad\":\"")
                .append(escaparJson(c.getNombreActividad()))
                .append("\",\"notaObtenida\":")
                .append(c.getNotaObtenida().toPlainString())
                .append(",\"notaMaxima\":")
                .append(c.getNotaMaxima().toPlainString())
                .append(",\"fecha\":\"")
                .append(c.getFecha())
                .append("\"}");
        }

        return json.append("]").toString();
    }

    // =====================================================
    // DETENER SERVIDOR
    // =====================================================

    public void detener() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    private void manejarLogin(HttpExchange intercambio) throws IOException {
        if (!"POST".equalsIgnoreCase(intercambio.getRequestMethod())) {
            responder(intercambio, 405, "{\"mensaje\":\"Método no permitido.\"}");
            return;
        }

        String cuerpo = leerCuerpo(intercambio);
        Map<String, String> datos = leerFormulario(cuerpo);
        String usuario = datos.get("usuario");
        String contrasena = datos.get("contrasena");

        if (usuario == null || contrasena == null) {
            responder(intercambio, 400,
                    "{\"exitoso\":false,\"mensaje\":\"Faltan datos del formulario.\"}");
            return;
        }

        boolean accesoCorrecto = controladorLogin.iniciarSesion(usuario, contrasena);
        String mensaje = accesoCorrecto
                ? "Inicio de sesión correcto."
                : "Usuario o contraseña incorrectos.";
        responder(intercambio, 200, "{\"exitoso\":" + accesoCorrecto
                + ",\"mensaje\":\"" + escaparJson(mensaje) + "\"}");
    }

    private void manejarLogout(HttpExchange intercambio) throws IOException {
        if (!"POST".equalsIgnoreCase(intercambio.getRequestMethod())) {
            responder(intercambio, 405,
                    "{\"mensaje\":\"MÃ©todo no permitido.\"}");
            return;
        }

        controladorLogin.cerrarSesion();
        responder(intercambio, 200,
                "{\"exitoso\":true,\"mensaje\":\"SesiÃ³n cerrada.\"}");
    }

    private void manejarTareas(HttpExchange intercambio) throws IOException {
        if ("GET".equalsIgnoreCase(intercambio.getRequestMethod())) {
            responder(intercambio, 200, convertirTareasAJson());
            return;
        }

        if (!"POST".equalsIgnoreCase(intercambio.getRequestMethod())) {
            responder(intercambio, 405,
                    "{\"mensaje\":\"Método no permitido.\"}");
            return;
        }

        Map<String, String> datos = leerFormulario(leerCuerpo(intercambio));
        try {
            controladorTareas.registrartarea(
                    datos.get("nombre"),
                    datos.get("fechaEntrega"),
                    datos.get("prioridad")
            );
            responder(intercambio, 201,
                    "{\"exitoso\":true,\"mensaje\":\"Tarea creada.\"}");
        } catch (IllegalArgumentException error) {
            responder(intercambio, 400,
                    "{\"exitoso\":false,\"mensaje\":\""
                            + escaparJson(error.getMessage()) + "\"}");
        }
    }

    private void completarTarea(HttpExchange intercambio) throws IOException {
        if (!"POST".equalsIgnoreCase(intercambio.getRequestMethod())) {
            responder(intercambio, 405,
                    "{\"mensaje\":\"Método no permitido.\"}");
            return;
        }

        Map<String, String> datos = leerFormulario(leerCuerpo(intercambio));
        try {
            int indice = Integer.parseInt(datos.get("indice"));
            List<Tarea> tareas = controladorTareas.getListaTareas();
            if (indice < 0 || indice >= tareas.size()) {
                throw new IllegalArgumentException("La tarea no existe.");
            }

            controladorTareas.marcarCompletada(tareas.get(indice));
            responder(intercambio, 200,
                    "{\"exitoso\":true,\"mensaje\":\"Tarea completada.\"}");
        } catch (IllegalArgumentException error) {
            responder(intercambio, 400,
                    "{\"exitoso\":false,\"mensaje\":\""
                            + escaparJson(error.getMessage()) + "\"}");
        }
    }

    private String convertirTareasAJson() {
        StringBuilder json = new StringBuilder("[");
        List<Tarea> tareas = controladorTareas.getListaTareas();

        for (int indice = 0; indice < tareas.size(); indice++) {
            if (indice > 0) {
                json.append(',');
            }

            Tarea tarea = tareas.get(indice);
            json.append("{\"indice\":").append(indice)
                    .append(",\"nombre\":\"")
                    .append(escaparJson(tarea.getNombre()))
                    .append("\",\"fechaEntrega\":\"")
                    .append(escaparJson(tarea.getfechaEntrega()))
                    .append("\",\"prioridad\":\"")
                    .append(escaparJson(tarea.getPrioridad()))
                    .append("\",\"completada\":")
                    .append(tarea.isCompletada())
                    .append('}');
        }

        return json.append(']').toString();
    }

    private void servirArchivo(HttpExchange intercambio) throws IOException {
        if (!"GET".equalsIgnoreCase(intercambio.getRequestMethod())) {
            responder(intercambio, 405, "Método no permitido.");
            return;
        }

        String rutaSolicitada = intercambio.getRequestURI().getPath();
        if (rutaSolicitada.equals("/")) {
            rutaSolicitada = "/index.html";
        }

        Path archivo = carpetaPublica.resolve(rutaSolicitada.substring(1)).normalize();
        if (!archivo.startsWith(carpetaPublica) || Files.notExists(archivo)
                || Files.isDirectory(archivo)) {
            responder(intercambio, 404, "Página no encontrada.");
            return;
        }

        byte[] contenido = Files.readAllBytes(archivo);
        intercambio.getResponseHeaders().set("Content-Type", tipoContenido(archivo));
        intercambio.sendResponseHeaders(200, contenido.length);
        intercambio.getResponseBody().write(contenido);
        intercambio.close();
    }

    private String leerCuerpo(HttpExchange intercambio) throws IOException {
        try (InputStream entrada = intercambio.getRequestBody()) {
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private Map<String, String> leerFormulario(String cuerpo) {
        Map<String, String> datos = new HashMap<>();
        if (cuerpo.isBlank()) {
            return datos;
        }

        for (String par : cuerpo.split("&")) {
            String[] partes = par.split("=", 2);
            if (partes.length == 2) {
                datos.put(decodificar(partes[0]), decodificar(partes[1]));
            }
        }
        return datos;
    }

    private String decodificar(String valor) {
        return URLDecoder.decode(valor, StandardCharsets.UTF_8);
    }

    private String tipoContenido(Path archivo) {
        String nombre = archivo.getFileName().toString();
        if (nombre.endsWith(".html")) return "text/html; charset=UTF-8";
        if (nombre.endsWith(".css")) return "text/css; charset=UTF-8";
        if (nombre.endsWith(".js")) return "text/javascript; charset=UTF-8";
        return "text/plain; charset=UTF-8";
    }

    private void responder(HttpExchange intercambio, int estado, String cuerpo)
            throws IOException {
        byte[] contenido = cuerpo.getBytes(StandardCharsets.UTF_8);
        intercambio.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        intercambio.sendResponseHeaders(estado, contenido.length);
        intercambio.getResponseBody().write(contenido);
        intercambio.close();
    }

    private String escaparJson(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
