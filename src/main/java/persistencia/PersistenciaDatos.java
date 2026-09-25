package persistencia;

import modelo.Usuario;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class PersistenciaDatos {
    private final Path rutaUsuarios;

    public PersistenciaDatos() {
        this(Paths.get("data", "usuarios.csv"));
    }

    public PersistenciaDatos(Path rutaUsuarios) {
        this.rutaUsuarios = rutaUsuarios;
    }

    public List<Usuario> leerUsuarios() {
        prepararArchivo();
        List<Usuario> usuarios = new ArrayList<>();

        try {
            for (String linea : Files.readAllLines(rutaUsuarios, StandardCharsets.UTF_8)) {
                String[] partes = linea.split(";", -1);
                if (partes.length == 3 && !partes[0].trim().isEmpty()) {
                    usuarios.add(Usuario.desdeArchivo(partes[0], partes[1], partes[2]));
                }
            }
            return usuarios;
        } catch (IOException error) {
            throw new IllegalStateException("No se pudieron leer los usuarios", error);
        }
    }

    public void guardarUsuario(Usuario usuario) {
        prepararArchivo();
        String linea = usuario.getNombreUsuario() + ";"
                + usuario.getContrasena() + ";"
                + usuario.getCarne() + System.lineSeparator();

        try {
            Files.writeString(rutaUsuarios, linea, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException error) {
            throw new IllegalStateException("No se pudo guardar el usuario", error);
        }
    }

    private void prepararArchivo() {
        try {
            Path carpeta = rutaUsuarios.getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }
            if (Files.notExists(rutaUsuarios)) {
                Files.createFile(rutaUsuarios);
            }
        } catch (IOException error) {
            throw new IllegalStateException("No se pudo preparar el archivo de usuarios", error);
        }
    }
}
