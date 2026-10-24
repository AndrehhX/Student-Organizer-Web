package controlador;

import modelo.Usuario;
import persistencia.RepositorioUsuariosMySQL;

import java.util.Optional;

public class ControladorLogin {
    private final RepositorioUsuariosMySQL usuarios;
    private Usuario usuarioActual;

    public ControladorLogin(RepositorioUsuariosMySQL usuarios) {
        if (usuarios == null) {
            throw new IllegalArgumentException(
                    "El repositorio de usuarios es obligatorio."
            );
        }
        this.usuarios = usuarios;
    }

    public boolean iniciarSesion(String usuario, String contrasena) {
        usuarioActual = null;

        Optional<Long> id = usuarios.autenticar(usuario, contrasena);

        if (id.isEmpty()) {
            return false;
        }

        usuarioActual = usuarios.buscarPorId(id.get()).orElse(null);
        return usuarioActual != null;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
}