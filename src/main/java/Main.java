import controlador.ControladorLogin;
import persistencia.ServiciosMySQL;
import servidor.ServidorWeb;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        ServiciosMySQL servicios = ServiciosMySQL.desdeEntorno();

        ControladorLogin login = new ControladorLogin(
                servicios.getUsuarios()
        );

        ServidorWeb servidor = new ServidorWeb(
                login,
                Paths.get("src", "main", "resources", "public"),
                8080,
                servicios
        );

        servidor.iniciar();
        System.out.println(
                "Student Organizer está disponible en http://localhost:8080"
        );
    }
}