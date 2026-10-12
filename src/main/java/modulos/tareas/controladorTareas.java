package modulos.tareas;

import java.util.ArrayList;
import java.util.List;

public class controladorTareas {

    private List<Tarea> listaTareas;
    private final PersistenciaTareas persistencia;

    public controladorTareas() {
        this(new PersistenciaTareas());
    }

    public controladorTareas(PersistenciaTareas persistencia) {
        this.persistencia = persistencia;
        this.listaTareas = persistencia.cargarTareas();
    }


    


    public void registrartarea(String nombre, String fechaEntrega,
                               String prioridad) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre de la tarea no puede estar vacío."
            );
        }

        if (fechaEntrega == null || fechaEntrega.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La fecha de entrega no puede estar vacía."
            );
        }

        if (prioridad == null || prioridad.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La prioridad es obligatoria."
            );
        }

        Tarea nuevaTarea = new Tarea(
                nombre.trim(),
                fechaEntrega.trim(),
                prioridad.trim()
        );

        List<Tarea> actualizadas = new ArrayList<>(listaTareas);
        actualizadas.add(nuevaTarea);

        persistencia.guardarTareas(actualizadas);
        this.listaTareas = actualizadas;
    }

    public List<Tarea> consultarPendientes() {
        List<Tarea> pendientes = new ArrayList<>();

        for (Tarea tarea : listaTareas) {
            if (!tarea.isCompletada()) {
                pendientes.add(tarea);
            }
        }

        return pendientes;
    }

    public void marcarCompletada(Tarea tarea) {
        if (tarea == null || !listaTareas.contains(tarea)) {
            throw new IllegalArgumentException(
                    "Selecciona una tarea registrada."
            );
        }

        boolean estadoAnterior = tarea.isCompletada();
        tarea.marcarComoCompletada();

        try {
            persistencia.guardarTareas(listaTareas);
        } catch (RuntimeException error) {
            tarea.setCompletada(estadoAnterior);
            throw error;
        }
    }

    public List<Tarea> getListaTareas() {
        return new ArrayList<>(listaTareas);
    }

    public void setListaTareas(List<Tarea> listaTareas) {
        if (listaTareas == null) {
            throw new IllegalArgumentException(
                    "La lista de tareas no puede ser nula."
            );
        }

        for (Tarea tarea : listaTareas) {
            if (tarea == null) {
                throw new IllegalArgumentException(
                        "La lista no puede contener tareas nulas."
                );
            }
        }

        List<Tarea> nuevas = new ArrayList<>(listaTareas);
        persistencia.guardarTareas(nuevas);
        this.listaTareas = nuevas;
    }

    @Override
    public String toString() {
        return "controladorTareas{" +
                "listaTareas=" + listaTareas +
                '}';
    }
}