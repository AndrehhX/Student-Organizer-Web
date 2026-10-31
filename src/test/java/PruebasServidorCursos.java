import controlador.ControladorLogin;
import persistencia.ConexionBD;
import persistencia.ServiciosMySQL;
import servidor.ServidorWeb;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

public class PruebasServidorCursos {

    public static void main(String[] args) throws Exception {
        int puerto = obtenerPuertoDisponible();

        ConexionBD bd = new ConexionBD(
                "jdbc:mysql://127.0.0.1:3307/student_organizer",
                "organizer_app",
                "no importa"
        );

        ServiciosMySQL servicios = new ServiciosMySQL(bd);

        ControladorLogin login =
                new ControladorLogin(servicios.getUsuarios());

        ServidorWeb servidor = new ServidorWeb(
                login,
                Path.of("src", "main", "resources", "public"),
                puerto,
                servicios
        );

        try {
            servidor.iniciar();

            HttpClient cliente = HttpClient.newHttpClient();

            HttpResponse<String> respuesta = cliente.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(
                                    "http://localhost:"
                                            + puerto
                                            + "/api/cursos"
                            ))
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );

            comprobar(
                    respuesta.statusCode() == 401,
                    "Sin iniciar sesión debe responder 401."
            );

            System.out.println(
                    "PruebasServidorCursos: todas las pruebas pasaron."
            );
        } finally {
            servidor.detener();
        }
    }

    private static int obtenerPuertoDisponible()
            throws IOException {
        try (java.net.ServerSocket servidor =
                     new java.net.ServerSocket()) {

            servidor.bind(
                    new InetSocketAddress("localhost", 0)
            );

            return servidor.getLocalPort();
        }
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