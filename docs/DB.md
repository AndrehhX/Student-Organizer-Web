# RF07 — Gestión de tareas

## Componentes

- `Tarea.java`: representa los datos y el estado de una tarea.
- `controladorTareas.java`: registra, consulta y completa tareas usando CSV.
- `PersistenciaTareas.java`: guarda y carga tareas en CSV.
- `ControladorTareasMySQL.java`: gestiona tareas y valida que el curso pertenezca al usuario.
- `PersistenciaTareasMySQL.java`: guarda, consulta y completa tareas en MySQL.

## Dificultades

Adaptar prioridades a cursos, mantener compatibilidad con CSV y validar usuarios y cursos.

## Estado actual

Ambas versiones están implementadas. Falta completar la integración del servidor con MySQL.