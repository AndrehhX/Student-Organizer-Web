package controlador;

import modelo.Usuario;
import persistencia.PersistenciaDatos;

import java.util.List;

public class ControladorLogin {
    private final PersistenciaDatos persistenciaDatos;
    private Usuario usuarioActual;

    public ControladorLogin() {
        this(new PersistenciaDatos());
    }

    public ControladorLogin(PersistenciaDatos persistenciaDatos) {
        this.persistenciaDatos = persistenciaDatos;
        prepararCuentaDemostracion();
    }

    public boolean iniciarSesion(String usuario, String contrasena) {
        if (usuario == null || usuario.trim().isEmpty()
                || contrasena == null || contrasena.isEmpty()) {
            usuarioActual = null;
            return false;
        }

        List<Usuario> usuarios = persistenciaDatos.leerUsuarios();
        for (Usuario usuarioGuardado : usuarios) {
            if (usuarioGuardado.autenticar(usuario, contrasena)) {
                usuarioActual = usuarioGuardado;
                return true;
            }
        }

        usuarioActual = null;
        return false;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    private void prepararCuentaDemostracion() {
        if (persistenciaDatos.leerUsuarios().isEmpty()) {
            persistenciaDatos.guardarUsuario(new Usuario("demo", "demo123", "0000"));
        }
    }
}
