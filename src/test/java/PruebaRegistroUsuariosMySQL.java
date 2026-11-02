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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class PruebaRegistroUsuariosMySQL {

    public static void main(String[] args) throws Exception {
        String clave = System.getenv("DB_PASSWORD");

        if (clave == null || clave.isBlank()) {
            throw new IllegalStateException(
                    "Falta configurar DB_PASSWORD."
            );
        }

        ConexionBD bd = new ConexionBD(
                "jdbc:mysql://127.0.0.1:3307/student_organizer",
                "organizer_app",
                clave
        );

        ServiciosMySQL servicios = new ServiciosMySQL(bd);
        ControladorLogin login =
                new ControladorLogin(servicios.getUsuarios());

        int puerto = obtenerPuertoDisponible();
        ServidorWeb servidor = new ServidorWeb(
                login,
                Path.of("src", "main", "resources", "public"),
                puerto,
                servicios
        );

        String nombre = "registro_" + System.currentTimeMillis();

        try {
            servidor.iniciar();
            HttpClient cliente = HttpClient.newHttpClient();

            HttpResponse<String> incompleto = enviar(
                    cliente,
                    puerto,
                    "/api/usuarios",
                    "usuario=" + codificar(nombre)
            );

            comprobar(
                    incompleto.statusCode() == 400,
                    "Debe rechazar un registro incompleto."
            );

            String datos = "usuario=" + codificar(nombre)
                    + "&contrasena=" + codificar("Registro123")
                    + "&carne=" + codificar("REGISTRO");

            HttpResponse<String> creado = enviar(
                    cliente,
                    puerto,
                    "/api/usuarios",
                    datos
            );

            comprobar(
                    creado.statusCode() == 201
                            && creado.body().contains("\"exitoso\":true"),
                    "Debe registrar un usuario nuevo."
            );

            HttpResponse<String> respuestaLogin = enviar(
                    cliente,
                    puerto,
                    "/api/login",
                    "usuario=" + codificar(nombre)
                            + "&contrasena="
                            + codificar("Registro123")
            );

            comprobar(
                    respuestaLogin.statusCode() == 200
                            && respuestaLogin.body()
                                    .contains("\"exitoso\":true")
                            && respuestaLogin.headers()
                                    .firstValue("Set-Cookie")
                                    .isPresent(),
                    "El usuario registrado debe poder iniciar sesión."
            );

            HttpResponse<String> repetido = enviar(
                    cliente,
                    puerto,
                    "/api/usuarios",
                    datos
            );

            comprobar(
                    repetido.statusCode() == 409,
                    "Debe rechazar un usuario repetido."
            );

            System.out.println(
                    "PruebaRegistroUsuariosMySQL: todas las pruebas pasaron."
            );
        } finally {
            servidor.detener();
        }
    }

    private static HttpResponse<String> enviar(
            HttpClient cliente,
            int puerto,
            String ruta,
            String datos
    ) throws IOException, InterruptedException {
        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create(
                        "http://localhost:" + puerto + ruta
                ))
                .header(
                        "Content-Type",
                        "application/x-www-form-urlencoded"
                )
                .POST(HttpRequest.BodyPublishers.ofString(datos))
                .build();

        return cliente.send(
                solicitud,
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private static String codificar(String valor) {
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }

    private static int obtenerPuertoDisponible()
            throws IOException {
        try (java.net.ServerSocket servidor =
                     new java.net.ServerSocket()) {
            servidor.bind(new InetSocketAddress("localhost", 0));
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
