import controlador.ControladorLogin;
import persistencia.PersistenciaDatos;
import servidor.ServidorWeb;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class PruebasServidorCursos {

    public static void main(String[] args) throws Exception {
        Path carpetaTemporal = Files.createTempDirectory(
                "student-organizer-cursos-");
        int puerto = obtenerPuertoDisponible();
        ControladorLogin login = new ControladorLogin(
                new PersistenciaDatos(carpetaTemporal.resolve("usuarios.csv")));
        ServidorWeb servidor = new ServidorWeb(
                login,
                Path.of("src", "main", "resources", "public"),
                puerto);

        try {
            servidor.iniciar();
            comprobar(login.iniciarSesion("demo", "demo123"),
                    "La cuenta demo debe iniciar sesiÃ³n para probar cursos.");
            HttpClient cliente = HttpClient.newHttpClient();

            HttpResponse<String> respuesta = cliente.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(
                                    "http://localhost:" + puerto + "/api/cursos"))
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString());

            comprobar(respuesta.statusCode() == 503,
                    "Sin MySQL, cursos debe responder 503 y no fallar en 404.");
            comprobar(respuesta.body().contains("MySQL"),
                    "La respuesta debe explicar que falta configurar MySQL.");

            System.out.println(
                    "PruebasServidorCursos: todas las pruebas pasaron.");
        } finally {
            servidor.detener();
            try (var archivos = Files.walk(carpetaTemporal)) {
                archivos.sorted(java.util.Comparator.reverseOrder())
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
