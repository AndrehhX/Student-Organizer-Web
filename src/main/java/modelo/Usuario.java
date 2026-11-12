package modelo;

import util.Seguridad;

import java.util.Locale;

public class Usuario {
    private String nombreUsuario;
    private String correo;
    private String contrasena;
    private String carne;

    public Usuario(
            String nombreUsuario,
            String correo,
            String contrasena,
            String carne
    ) {
        this.nombreUsuario = nombreUsuario;
        this.correo = correo == null
                ? null
                : correo.trim().toLowerCase(Locale.ROOT);
        this.contrasena = Seguridad.codificar(contrasena);
        this.carne = carne;
    }

    private Usuario(
            String nombreUsuario,
            String correo,
            String contrasenaCodificada,
            String carne,
            boolean contrasenaYaCodificada
    ) {
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.contrasena = contrasenaCodificada;
        this.carne = carne;
    }

    public static Usuario desdeArchivo(String nombreUsuario,
                                       String correo,
                                       String contrasenaCodificada,
                                       String carne) {
        return new Usuario(
                nombreUsuario,
                correo,
                contrasenaCodificada,
                carne,
                true
        );
    }

    public boolean autenticar(String nombreUsuario, String contrasena) {
        return this.nombreUsuario.equals(nombreUsuario.trim())
                && this.contrasena.equals(Seguridad.codificar(contrasena));
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getCorreo() {
        return correo;
    }

    public String getCarne() {
        return carne;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = Seguridad.codificar(contrasena);
    }

    public void setCorreo(String correo) {
        this.correo = correo == null
                ? null
                : correo.trim().toLowerCase(Locale.ROOT);
    }

    public void setCarne(String carne) {
        this.carne = carne;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombreUsuario='" + nombreUsuario + '\'' +
                ", carne='" + carne + '\'' +
                '}';
    }
}
