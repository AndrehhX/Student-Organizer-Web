# RF03 Registrar calificaciones

## Qué hace

Desde **Notas**, el estudiante selecciona un curso y registra el nombre de la actividad, la nota obtenida, la nota máxima y la fecha. La pantalla valida los campos y envía la solicitud a Java. Las calificaciones se guardan en MySQL y se muestran en una tabla.

## Recorrido

1. `calificaciones.js` carga los cursos disponibles y recoge el formulario.
2. `ServidorWeb` recibe la solicitud en `/api/calificaciones`.
3. `ControladorCalificaciones` crea y valida el objeto `Calificacion`.
4. `RepositorioCalificacionesMySQL` guarda o consulta las notas, asociadas al usuario y al curso.
5. La pantalla actualiza la tabla con la respuesta del servidor.

Las notas deben ser mayores o iguales a cero y no pueden superar la nota máxima. El curso debe pertenecer al usuario con sesión activa.

## Relación con el promedio

`ControladorPromedios` consulta el mismo repositorio. Calcula los promedios por curso y el promedio general, que luego se muestra en el dashboard. El panel de ranking ordena los cursos del usuario por su promedio; no compara calificaciones de distintos estudiantes.

## Pruebas

Las pruebas unitarias cubren calificaciones válidas, valores fuera de rango y cálculos relacionados. Las pruebas `PruebaCalificacionesMySQL` requieren una base disponible y pueden insertar registros; ejecútalas en una base de pruebas.

## Aporte de Fabricio

El RF03 corresponde al registro y consulta de calificaciones. La clase `Calificacion`, el controlador, la persistencia MySQL y el formulario de Notas participan en ese recorrido.
