package modulos.Cursos;

public class Curso {

    private final long idCurso;
    private final long usuarioId;
    private final String nombreCurso;

    public Curso(long idCurso, long usuarioId, String nombreCurso) {
        if (idCurso <= 0 || usuarioId <= 0) {
            throw new IllegalArgumentException(
                    "Los identificadores deben ser mayores que cero."
            );
        }

        if (nombreCurso == null || nombreCurso.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del curso es obligatorio."
            );
        }

        this.idCurso = idCurso;
        this.usuarioId = usuarioId;
        this.nombreCurso = nombreCurso.trim();
    }

    public long getIdCurso() {
        return idCurso;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }
}