# Integración con MySQL

## Qué usa el servidor

`Main` obtiene la configuración de `ServiciosMySQL` y pasa esos servicios a `ServidorWeb`. Los repositorios guardan y consultan usuarios, cursos, tareas y calificaciones. `ControladorPromedios` reutiliza las calificaciones del usuario para calcular resultados.

La aplicación requiere una base MySQL. No cambia automáticamente a CSV ni a un repositorio temporal si faltan credenciales.

## Rutas principales

- `POST /api/usuarios`: registra una cuenta.
- `POST /api/login`: valida las credenciales y crea una sesión.
- `POST /api/logout`: cierra la sesión.
- `GET` y `POST /api/cursos`: consultan y registran cursos del usuario.
- `GET` y `POST /api/tareas`: consultan y registran tareas.
- `POST /api/tareas/completar`: marca una tarea como completada.
- `GET` y `POST /api/calificaciones`: consultan y registran notas.
- `GET /api/promedios`: devuelve los promedios del usuario.
- `GET /api/cursos/detalle?cursoId=...`: consulta tareas y notas del curso.

## Configuración

Define `DB_PASSWORD` localmente y, si la base está en otro equipo, define `DB_HOST` con su dirección accesible. El puerto usado es `3307`; la base se llama `student_organizer` y la cuenta de conexión es `organizer_app`. Se necesita MySQL Connector/J en `lib/` para iniciar el servidor.

## Qué se verificó en la revisión del PR

Se compiló el proyecto y pasaron las pruebas locales de tareas, calificaciones, promedios y rutas del servidor que no requieren una base MySQL. También se revisó la sintaxis de los scripts JavaScript.

Las pruebas que escriben en MySQL no se ejecutaron contra la base compartida para evitar agregar datos de prueba. Deben correrse con Docker/MySQL y credenciales de desarrollo; varias dejan filas creadas, así que conviene usar una base de pruebas.
