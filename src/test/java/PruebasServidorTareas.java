import controlador.ControladorLogin;
import modulos.tareas.PersistenciaTareas;
import modulos.tareas.controladorTareas;
import persistencia.PersistenciaDatos;
import servidor.ServidorWeb;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class PruebasServidorTareas {

    public static void main(String[] args) throws Exception {
        Path carpetaTemporal = Files.createTempDirectory(
                "student-organizer-servidor-");
        int puerto = obtenerPuertoDisponible();

        PersistenciaDatos persistenciaUsuarios = new PersistenciaDatos(
                carpetaTemporal.resolve("usuarios.csv"));
        ControladorLogin controladorLogin = new ControladorLogin(
                persistenciaUsuarios);
        controladorTareas controladorTareas = new controladorTareas(
                new PersistenciaTareas(carpetaTemporal.resolve("tareas.csv")));
        ServidorWeb servidor = new ServidorWeb(
                controladorLogin,
                Path.of("src", "main", "resources", "public"),
                puerto,
                controladorTareas);

        try {
            servidor.iniciar();
            HttpClient cliente = HttpClient.newHttpClient();

            HttpResponse<String> inicial = enviar(cliente, puerto,
                    "GET", "/api/tareas", "");
            comprobar(inicial.statusCode() == 200,
                    "Debe consultar las tareas desde el servidor");
            comprobar(inicial.body().equals("[]"),
                    "Debe iniciar sin tareas");

            HttpResponse<String> creada = enviar(cliente, puerto,
                    "POST", "/api/tareas",
                    formulario(List.of(
                            new String[]{"nombre", "Repasar Java"},
                            new String[]{"fechaEntrega", "2026-10-30"},
                            new String[]{"prioridad", "alta"})));
            comprobar(creada.statusCode() == 201,
                    "Debe crear una tarea desde el formulario");

            HttpResponse<String> lista = enviar(cliente, puerto,
                    "GET", "/api/tareas", "");
            comprobar(lista.body().contains("Repasar Java"),
                    "Debe devolver la tarea creada");
            comprobar(lista.body().contains("\"completada\":false"),
                    "La tarea debe iniciar pendiente");

            HttpResponse<String> completada = enviar(cliente, puerto,
                    "POST", "/api/tareas/completar", "indice=0");
            comprobar(completada.statusCode() == 200,
                    "Debe marcar la tarea desde el servidor");

            HttpResponse<String> listaFinal = enviar(cliente, puerto,
                    "GET", "/api/tareas", "");
            comprobar(listaFinal.body().contains("\"completada\":true"),
                    "Debe devolver la tarea como completada");

            System.out.println(
                    "PruebasServidorTareas: todas las pruebas pasaron.");
        } finally {
            servidor.detener();
            try (var archivos = Files.walk(carpetaTemporal)) {
                archivos.sorted(Comparator.reverseOrder())
                        .forEach(archivo -> {
                            try {
                                Files.deleteIfExists(archivo);
                            } catch (IOException error) {
                                throw new RuntimeException(error);
                            }
                        });
            }
        }
    }

    private static HttpResponse<String> enviar(
            HttpClient cliente, int puerto, String metodo,
            String ruta, String cuerpo)
            throws IOException, InterruptedException {
        HttpRequest.Builder constructor = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + puerto + ruta));

        if ("POST".equals(metodo)) {
            constructor.header("Content-Type",
                            "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpo));
        } else {
            constructor.GET();
        }

        return cliente.send(constructor.build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private static String formulario(List<String[]> campos) {
        StringBuilder resultado = new StringBuilder();
        for (String[] campo : campos) {
            if (resultado.length() > 0) {
                resultado.append('&');
            }
            resultado.append(URLEncoder.encode(campo[0], StandardCharsets.UTF_8));
            resultado.append('=');
            resultado.append(URLEncoder.encode(campo[1], StandardCharsets.UTF_8));
        }
        return resultado.toString();
    }

    private static int obtenerPuertoDisponible() throws IOException {
        try (java.net.ServerSocket servidor = new java.net.ServerSocket()) {
            servidor.bind(new InetSocketAddress("localhost", 0));
            return servidor.getLocalPort();
        }
    }

    private static void comprobar(boolean resultado, String mensaje) {
        if (!resultado) {
            throw new AssertionError(mensaje);
        }
    }
}
