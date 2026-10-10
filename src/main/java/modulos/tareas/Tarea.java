package modulos.tareas;

public class Tarea {
    private String nombre;
    private String fechaEntrega;
    private String prioridad;
    private boolean completada;
    


    public Tarea(String nombre, String fechaEntrega, String prioridad) {
        setNombre(nombre);
        setfechaEntrega(fechaEntrega);
        setPrioridad(prioridad);
        this.completada = false;
    }


    public static String validarTexto(String texto, String campo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        return texto;
    }

    public void marcarComoCompletada() {
        this.completada = true;
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
        this.fechaEntrega = validarTexto(fechaEntrega, "Fecha de entrega");
    }

    public String getPrioridad() {
        return prioridad;
    }
    public void setPrioridad(String prioridad) {
        this.prioridad = validarTexto(prioridad, "Prioridad");
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }


    @Override 
    public String toString() {
        return "Tarea{" +
                "nombre='" + nombre + '\'' +
                ", fechaEntrega='" + fechaEntrega + '\'' +
                ", prioridad='" + prioridad + '\'' +
                ", completada=" + completada +
                '}';
    }


}


