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

public class PruebasServidorDashboard {

    public static void main(String[] args) throws Exception {
        Path carpetaTemporal = Files.createTempDirectory("student-organizer-dashboard-");
        int puerto = obtenerPuertoDisponible();
        ControladorLogin controladorLogin = new ControladorLogin(
                new PersistenciaDatos(carpetaTemporal.resolve("usuarios.csv")));
        ServidorWeb servidor = new ServidorWeb(
                controladorLogin,
                Path.of("src", "main", "resources", "public"),
                puerto);

        try {
            servidor.iniciar();
            HttpClient cliente = HttpClient.newHttpClient();

            HttpResponse<String> login = enviar(cliente, puerto, "POST",
                    "/api/login", "usuario=demo&contrasena=demo123");
            comprobar(login.statusCode() == 200,
                    "El login debe responder correctamente");
            comprobar(controladorLogin.getUsuarioActual() != null,
                    "El login debe dejar un usuario activo");

            HttpResponse<String> cierreSesion = enviar(cliente, puerto, "POST",
                    "/api/logout", "");
            comprobar(cierreSesion.statusCode() == 200,
                    "Cerrar sesión debe responder correctamente");
            comprobar(controladorLogin.getUsuarioActual() == null,
                    "Cerrar sesión debe borrar el usuario activo");

            System.out.println(
                    "PruebasServidorDashboard: todas las pruebas pasaron.");
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
