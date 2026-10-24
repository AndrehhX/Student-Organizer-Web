package modulos.tareas;

//representa una tarea
public class Tarea {

    private long id;
    private Long cursoId;
    private String nombre;
    private String fechaEntrega;
    private boolean completada;
      // se volvio a agregar prioridad de manera permanente, ahora tambien se registra en MySQL
    private String prioridad = "media";

    /**
     * Construye una tarea nueva asociada a un curso.
     */
    public Tarea(String nombre, String fechaEntrega, long cursoId) {
        this(nombre, fechaEntrega);
        setCursoId(cursoId);
    }

    /**
     * Permite reconstruir tareas antiguas que aún no tienen curso.
     * La persistencia exige un curso al guardar una tarea nueva.
     */
    public Tarea(String nombre, String fechaEntrega) {
        setNombre(nombre);
        setfechaEntrega(fechaEntrega);
        this.completada = false;
    }

    /**
     * Constructor temporal para el código que todavía usa prioridad.
     */
    @Deprecated
    public Tarea(
            String nombre,
            String fechaEntrega,
            String prioridad
    ) {
        this(nombre, fechaEntrega);
        setPrioridad(prioridad);
    }

    public static String validarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    campo + " no puede estar vacío."
            );
        }

        return texto.trim();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID de la tarea debe ser mayor que cero."
            );
        }

        this.id = id;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        if (cursoId != null && cursoId <= 0) {
            throw new IllegalArgumentException(
                    "El ID del curso debe ser mayor que cero."
            );
        }

        this.cursoId = cursoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = validarTexto(nombre, "Nombre de la tarea");
    }


    public String getfechaEntrega() {
        return fechaEntrega;
    }

    public void setfechaEntrega(String fechaEntrega) {
        this.fechaEntrega = validarTexto(
                fechaEntrega,
                "Fecha de entrega"
        );
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public void marcarComoCompletada() {
        this.completada = true;
    }

    
    public String getPrioridad() {
        return prioridad;
    }

    
    public void setPrioridad(String prioridad) {
        String valor = validarTexto(prioridad, "Prioridad").toLowerCase(java.util.Locale.ROOT);


        if (!valor.equals("alta") && !valor.equals("media") && !valor.equals("baja")) {
            throw new IllegalArgumentException("La prioridad debe ser alta, media o baja");
        }

        this.prioridad = valor;
    }

//Agregue el coso de prioridad otravez, ahora si nitido




    @Override
    public String toString() {
        return "Tarea{"
                + "id=" + id
                + ", cursoId=" + cursoId
                + ", nombre='" + nombre + '\''
                + ", fechaEntrega='" + fechaEntrega + '\''
                + ", completada=" + completada
                + '}';
    }
}