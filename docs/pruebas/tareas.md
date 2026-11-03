# Pruebas de tareas

## Prueba del modelo

`src/test/java/PruebasTareas.java` revisa que `Tarea` conserve nombre, curso, fecha y prioridad, y que cambie de pendiente a completada. Esta prueba no conecta con MySQL ni verifica que los datos queden guardados.

Con las clases principales ya compiladas, se puede ejecutar así desde PowerShell:

```powershell
javac -encoding UTF-8 -cp "out;lib/*" -d out-test src/test/java/PruebasTareas.java
java -cp "out-test;out;lib/*" PruebasTareas
```

Resultado esperado:

```text
PruebasTareas: todas las pruebas pasaron.
```

## Prueba con MySQL

`src/test/java/PruebaTareasMySQL.java` verifica el guardado y la consulta real. Requiere MySQL, Connector/J y `DB_PASSWORD`; agrega datos y no los elimina al terminar. Ejecútala únicamente en una base de pruebas o coordina la limpieza con el equipo.
