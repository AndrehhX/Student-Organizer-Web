# Integración de MySQL

## Qué se agregó

El PR de Luis agregó `ServiciosMySQL`, una clase que centraliza la configuración de la conexión y construye los componentes que trabajan con MySQL:

- usuarios;
- cursos;
- tareas;
- calificaciones;
- promedios.

El repositorio también contiene la tabla `cursos` y la tabla `calificaciones`, junto con sus relaciones hacia usuarios y cursos.

Además, `ServidorWeb` ya puede recibir `ServiciosMySQL` y utilizar esos repositorios desde las rutas web. La selección se activa desde `Main` cuando existe `DB_PASSWORD`.

## Cómo funciona la estructura

1. `ConexionBD` representa la conexión a MySQL.
2. `ServiciosMySQL` recibe esa conexión y crea los repositorios.
3. `RepositorioCursosMySQL` puede guardar, listar y buscar cursos por usuario.
4. `RepositorioCalificacionesMySQL` guarda y consulta las calificaciones.
5. `ControladorPromedios` reutiliza el repositorio de calificaciones para calcular los promedios.
6. `ServidorWeb` conecta esos servicios con `/api/cursos`, `/api/tareas`, `/api/calificaciones` y `/api/promedios`.

## Rutas integradas

- `GET /api/cursos`: lista los cursos del usuario autenticado.
- `POST /api/cursos`: registra un curso con el campo `nombre`.
- `GET /api/tareas`: lista las tareas desde MySQL en modo MySQL.
- `POST /api/tareas`: registra una tarea usando `nombre`, `fechaEntrega` y `cursoId`.
- `POST /api/tareas/completar`: marca como completada una tarea usando su id.
- `/api/calificaciones` y `/api/promedios`: utilizan el repositorio MySQL correspondiente en modo MySQL.

## Qué se verificó

En la versión actual se verificó localmente:

- compilación de las clases Java;
- login correcto e incorrecto;
- tareas;
- calificaciones en memoria;
- cálculo de promedios;
- servidor de tareas;
- servidor del dashboard y cierre de sesión;
- respuesta controlada de `/api/cursos` cuando MySQL no está configurado.

Todas esas pruebas locales pasan.

## Pendiente de verificación

La integración de código ya está conectada, pero todavía debe hacerse una prueba con Docker/MySQL para confirmar en el entorno real que los datos se guardan y se recuperan correctamente. En este equipo no se pudo ejecutar esa prueba porque el comando de Docker no está disponible.

El login web continúa usando el archivo CSV como fuente de autenticación. Para trabajar con los módulos MySQL, el usuario debe existir también en la tabla `usuarios`, porque los repositorios usan su id de base de datos.

## Variables esperadas para MySQL

`ServiciosMySQL.desdeEntorno()` espera:

- `DB_PASSWORD`: contraseña obligatoria de la base;
- `DB_HOST`: host opcional, por defecto `127.0.0.1`.

La conexión usa el puerto `3307`, la base `student_organizer` y el usuario `organizer_app`.
