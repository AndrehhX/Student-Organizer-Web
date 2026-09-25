package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Seguridad {
    private Seguridad() {
    }

    public static String codificar(String texto) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] resultado = sha256.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder codigo = new StringBuilder();

            for (byte parte : resultado) {
                codigo.append(String.format("%02x", parte));
            }

            return codigo.toString();
        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException("No se pudo codificar la contraseña", error);
        }
    }
}
