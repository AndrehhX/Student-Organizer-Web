package controlador;

import modelo.Usuario;
import persistencia.RepositorioUsuariosMySQL;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ControladorLogin {

    private static final long DURACION_MS = 30 * 60 * 1000L;

    private final RepositorioUsuariosMySQL usuarios;
    private final Map<String, Sesion> sesiones =
            new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    private static class Sesion {
        final Usuario usuario;
        volatile long ultimaActividad;

        Sesion(Usuario usuario) {
            this.usuario = usuario;
            this.ultimaActividad = System.currentTimeMillis();
        }
    }

    public ControladorLogin(RepositorioUsuariosMySQL usuarios) {
        if (usuarios == null) {
            throw new IllegalArgumentException(
                    "El repositorio de usuarios es obligatorio."
            );
        }

        this.usuarios = usuarios;
    }

    public Optional<String> iniciarSesion(
            String nombre,
            String contrasena
    ) {
        Optional<Usuario> usuario = usuarios
                .autenticar(nombre, contrasena)
                .flatMap(usuarios::buscarPorId);

        if (usuario.isEmpty()) {
            return Optional.empty();
        }

        long ahora = System.currentTimeMillis();

        sesiones.entrySet().removeIf(
                entrada -> ahora - entrada.getValue().ultimaActividad
                        >= DURACION_MS
        );

        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        sesiones.put(token, new Sesion(usuario.get()));

        return Optional.of(token);
    }

    public Usuario getUsuarioActual(String token) {
        if (token == null) {
            return null;
        }

        Sesion sesion = sesiones.get(token);

        if (sesion == null) {
            return null;
        }

        long ahora = System.currentTimeMillis();

        if (ahora - sesion.ultimaActividad >= DURACION_MS) {
            sesiones.remove(token, sesion);
            return null;
        }

        sesion.ultimaActividad = ahora;
        return sesion.usuario;
    }

    public void cerrarSesion(String token) {
        if (token != null) {
            sesiones.remove(token);
        }
    }
}