# Pruebas de tareas

Las pruebas de esta se ejecutan en `src/test/java/PruebasTareas.java` . usan un archivo temporal y se eliminan una vez terminadas.

| Caso | Resultado Esperado | Resultado |
| --- | --- | --- |
| Iniciar sin tareas guardadas | Obtener una lista vacia | paso |
| Registrar una tarea con nombre, fecha y prioridad | Crear el CSV y conservar la tarea | paso |
| Crear otro controlador con el mismo CSV | Recuperar la tarea y su nombre | paso |
| Registrar una tarea sin nombre | Rechazar | paso |
| Registrar una tarea sin fecha | Rechazar | paso |
| Registrar una tarea sin prioridad | Rechazar | paso |
| Crear directamente una `Tarea` sin nombre | Rechazar | paso |
| Intentar registrar datos incompletos | no modificar CSV | paso |
| Marcar una tarea como completada | Guardar su estado y quitarla de pendientes | paso |
