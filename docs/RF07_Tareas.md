# RF07 Gestión de tareas

## Qué hace

En el dashboard, cada estudiante puede registrar una tarea con nombre, curso, fecha de entrega y prioridad. También puede consultar sus tareas y marcar una como completada.

## Recorrido

1. `tareas.js` recoge el formulario y envía la acción al servidor.
2. `ServidorWeb` procesa las rutas `/api/tareas` y `/api/tareas/completar`.
3. `ControladorTareasMySQL` valida que el curso pertenezca al usuario.
4. `PersistenciaTareasMySQL` inserta o consulta filas en la tabla `tareas`.
5. La pantalla actualiza la lista y el estado de la tarea.

Cada consulta queda limitada al usuario con sesión activa. Las tareas nuevas deben tener un curso, una fecha válida y un nombre de hasta 150 caracteres.

## Pruebas

`PruebasTareas` cubre los datos y el estado de una tarea sin conectar con MySQL. `PruebaTareasMySQL` valida el guardado y la consulta real; requiere MySQL y agrega filas, por lo que se debe ejecutar en una base de pruebas.

## Aporte de Luis

El RF07 cubre el registro, consulta y finalización de tareas. El módulo usa MySQL y comparte con el resto del proyecto la sesión del usuario y la relación con cursos.
