# Base de datos y persistencia MySQL

## Componentes

- **MySQL:** almacena usuarios, cursos, tareas y calificaciones.
- **Docker Desktop:** ejecuta MySQL localmente.
- **DBeaver:** permite administrar y consultar la base.
- **JDBC y Connector/J:** conectan Java con MySQL.
- `ConexionBD.java`: crea las conexiones.
- `RepositorioUsuariosMySQL.java`: guarda usuarios y valida credenciales.
- `RepositorioCursosMySQL.java`: guarda y consulta cursos por usuario.
- `PersistenciaTareasMySQL.java`: guarda, consulta y completa tareas.
- `RepositorioCalificacionesMySQL.java`: guarda y consulta calificaciones.
- `ControladorPromedios`: calcula promedios con las notas guardadas.
- `ServiciosMySQL.java`: reúne los repositorios y controladores.
- **Archivos SQL:** crean y actualizan las tablas y relaciones.

## Dificultades

Configurar Docker y JDBC, aplicar migraciones, validar relaciones e integrar MySQL con el servidor.

## Estado actual

La base y los repositorios están implementados y probados. Falta verificar que el servidor use MySQL en todos los formularios.