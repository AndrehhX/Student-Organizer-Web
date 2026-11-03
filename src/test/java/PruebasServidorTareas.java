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

public class PruebasServidorTareas {

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

            HttpResponse<String> lista = enviar(
                    cliente,
                    puerto,
                    "GET",
                    "/api/tareas",
                    ""
            );

            comprobar(
                    lista.statusCode() == 401,
                    "Sin iniciar sesión debe responder 401."
            );

            HttpResponse<String> completar = enviar(
                    cliente,
                    puerto,
                    "POST",
                    "/api/tareas/completar",
                    "indice=1"
            );

            comprobar(
                    completar.statusCode() == 401,
                    "Sin iniciar sesión no debe completar tareas."
            );

            System.out.println(
                    "PruebasServidorTareas: todas las pruebas pasaron."
            );
        } finally {
            servidor.detener();
        }
    }

    private static HttpResponse<String> enviar(
            HttpClient cliente,
            int puerto,
            String metodo,
            String ruta,
            String cuerpo
    ) throws IOException, InterruptedException {

        HttpRequest.Builder constructor =
                HttpRequest.newBuilder()
                        .uri(URI.create(
                                "http://localhost:"
                                        + puerto
                                        + ruta
                        ));

        if (metodo.equals("POST")) {
            constructor.header(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
            );

            constructor.POST(
                    HttpRequest.BodyPublishers.ofString(cuerpo)
            );
        } else {
            constructor.GET();
        }

        return cliente.send(
                constructor.build(),
                HttpResponse.BodyHandlers.ofString()
        );
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