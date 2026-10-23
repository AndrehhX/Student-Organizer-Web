import controlador.ControladorLogin;
import persistencia.ServiciosMySQL;
import servidor.ServidorWeb;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        ControladorLogin controladorLogin = new ControladorLogin();
        ServiciosMySQL serviciosMySQL = null;
        String claveBase = System.getenv("DB_PASSWORD");

        if (claveBase != null && !claveBase.isBlank()) {
            serviciosMySQL = ServiciosMySQL.desdeEntorno();
        }

        ServidorWeb servidor = serviciosMySQL == null
                ? new ServidorWeb(
                        controladorLogin,
                        Paths.get("src", "main", "resources", "public"),
                        8080
                )
                : new ServidorWeb(
                        controladorLogin,
                        Paths.get("src", "main", "resources", "public"),
                        8080,
                        serviciosMySQL
                );

        servidor.iniciar();
        System.out.println("Student Organizer está disponible en http://localhost:8080");
    }
}
