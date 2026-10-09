package modulos.tareas;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class PersistenciaTareas {

    private final Path rutaArchivo;

    public PersistenciaTareas() {
        this(Paths.get("datos", "tareas.csv"));
    }

    public PersistenciaTareas(Path rutaArchivo) {
        this.rutaArchivo = rutaArchivo.toAbsolutePath();
    }

    public void guardarTareas(List<Tarea> tareas) {
        if (tareas == null) {
            throw new IllegalArgumentException("La lista no puede ser nula.");
        }

        List<String> lineas = new ArrayList<>();

        for (Tarea tarea : tareas) {
            if (tarea == null) {
                throw new IllegalArgumentException("La tarea no puede ser nula.");
            }

            String nombre = validarCampo(tarea.getNombre());
            String fechaEntrega = validarCampo(tarea.getfechaEntrega());
            String prioridad = validarCampo(tarea.getPrioridad());

            lineas.add(nombre + ";" + fechaEntrega + ";"
                    + prioridad + ";" + tarea.isCompletada());
        }

        Path temporal = null;

        try {
            Files.createDirectories(rutaArchivo.getParent());

            temporal = Files.createTempFile(
                    rutaArchivo.getParent(), "tareas-", ".tmp"
            );

            Files.write(temporal, lineas, StandardCharsets.UTF_8);
            Files.move(
                    temporal,
                    rutaArchivo,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException error) {
            throw new IllegalStateException(
                    "No se pudieron guardar las tareas.", error
            );
        } finally {
            if (temporal != null) {
                try {
                    Files.deleteIfExists(temporal);
                } catch (IOException ignorado) {
                    // Limpieza del archivo temporal.
                }
            }
        }
    }

    public List<Tarea> cargarTareas() {
        List<Tarea> tareas = new ArrayList<>();

        if (Files.notExists(rutaArchivo)) {
            return tareas;
        }

        try {
            List<String> lineas = Files.readAllLines(
                    rutaArchivo, StandardCharsets.UTF_8
            );

            int numeroLinea = 0;

            for (String linea : lineas) {
                numeroLinea++;
                String[] campos = linea.split(";", -1);

                try {
                    if (campos.length != 4) {
                        throw new IllegalArgumentException(
                                "Se esperan cuatro campos."
                        );
                    }

                    if (!campos[3].equals("true")
                            && !campos[3].equals("false")) {
                        throw new IllegalArgumentException(
                                "El estado debe ser true o false."
                        );
                    }

                    Tarea tarea = new Tarea(
                            validarCampo(campos[0]),
                            validarCampo(campos[1]),
                            validarCampo(campos[2])
                    );

                    tarea.setCompletada(Boolean.parseBoolean(campos[3]));
                    tareas.add(tarea);
                } catch (IllegalArgumentException error) {
                    throw new IllegalStateException(
                            "Datos inválidos en la línea " + numeroLinea,
                            error
                    );
                }
            }

            return tareas;
        } catch (IOException error) {
            throw new IllegalStateException(
                    "No se pudieron cargar las tareas.", error
            );
        }
    }

    private String validarCampo(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Los datos de la tarea no pueden estar vacíos."
            );
        }

        if (valor.contains(";") || valor.contains("\"")
                || valor.contains("\n") || valor.contains("\r")) {
            throw new IllegalArgumentException(
                    "No uses punto y coma, comillas dobles ni saltos de línea."
            );
        }

        return valor.trim();
    }
}