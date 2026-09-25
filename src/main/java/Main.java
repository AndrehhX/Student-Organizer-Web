import controlador.ControladorLogin;
import servidor.ServidorWeb;

import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        ControladorLogin controladorLogin = new ControladorLogin();
        ServidorWeb servidor = new ServidorWeb(
                controladorLogin,
                Paths.get("src", "main", "resources", "public"),
                8080
        );

        servidor.iniciar();
        System.out.println("Student Organizer está disponible en http://localhost:8080");
    }
}
