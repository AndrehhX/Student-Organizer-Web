# Base de datos y persistencia MySQL

## Componentes

- **MySQL:** guarda cuentas, cursos, tareas y calificaciones.
- **Docker Desktop:** puede ejecutar MySQL en el equipo que aloja la base.
- **JDBC y Connector/J:** permiten que Java se conecte a MySQL.
- `ConexionBD.java`: abre las conexiones.
- `RepositorioUsuariosMySQL.java`: registra usuarios y consulta credenciales.
- `RepositorioCursosMySQL.java`: registra y consulta cursos por usuario.
- `PersistenciaTareasMySQL.java`: guarda, consulta y completa tareas.
- `RepositorioCalificacionesMySQL.java`: guarda y consulta calificaciones.
- `ControladorPromedios`: calcula los promedios a partir de las calificaciones.
- `ServiciosMySQL.java`: construye y reúne los servicios que usa el servidor.
- `sql/`: contiene el esquema y cambios de estructura.

## Conexión

La aplicación requiere MySQL. `DB_PASSWORD` es obligatoria. `DB_HOST` es opcional y usa `127.0.0.1` si no se define. La conexión apunta al puerto `3307`, la base `student_organizer` y el usuario `organizer_app`.

Si MySQL corre en otra computadora, `DB_HOST` debe tener una dirección accesible desde la computadora donde corre Java. No se debe incluir la contraseña en archivos versionados, capturas ni comandos que queden guardados en el historial.

## Estado

El código conecta los módulos a repositorios MySQL. La compilación y las pruebas locales sin base se pueden ejecutar sin tocar registros. Las pruebas de integración deben correrse contra una instancia disponible y una base de pruebas; algunas insertan usuarios, cursos, tareas o calificaciones y no los eliminan al terminar.
