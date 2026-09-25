package modelo;

import util.Seguridad;

public class Usuario {
    private String nombreUsuario;
    private String contrasena;
    private String carne;

    public Usuario(String nombreUsuario, String contrasena, String carne) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = Seguridad.codificar(contrasena);
        this.carne = carne;
    }

    private Usuario(String nombreUsuario, String contrasenaCodificada,
                     String carne, boolean contrasenaYaCodificada) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasenaCodificada;
        this.carne = carne;
    }

    public static Usuario desdeArchivo(String nombreUsuario,
                                       String contrasenaCodificada,
                                       String carne) {
        return new Usuario(nombreUsuario, contrasenaCodificada, carne, true);
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

    public String getCarne() {
        return carne;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = Seguridad.codificar(contrasena);
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
