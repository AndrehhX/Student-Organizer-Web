# Student Organizer

Aplicación para que cada estudiante organice cursos, tareas y calificaciones desde una sola pantalla.

La interfaz está hecha con HTML, CSS y JavaScript. Java 17 recibe las solicitudes y aplica las reglas del sistema. MySQL conserva usuarios, cursos, tareas y calificaciones.

## Qué está implementado

- Crear una cuenta con usuario, contraseña y carné.
- Iniciar y cerrar sesión. La sesión se identifica con una cookie y vence después de 30 minutos sin actividad.
- Crear cursos y consultar sus calificaciones y tareas.
- Registrar, listar y completar tareas, con fecha y prioridad.
- Registrar y consultar calificaciones por curso.
- Calcular el promedio de cada curso y el promedio general.
- Mostrar los cinco cursos con promedio más alto del estudiante. Este panel ordena los cursos de una cuenta; no compara estudiantes entre sí.

El servidor actual requiere MySQL. No tiene un modo de respaldo con CSV. Las pruebas específicas de base de datos agregan filas, así que deben ejecutarse contra una base de pruebas o después de acordar cómo limpiar sus datos.

## Requisitos

- JDK 17.
- MySQL 8, ya sea en Docker o en otro equipo accesible.
- MySQL Connector/J en `lib/`.
- La base `student_organizer` y sus tablas creadas con los scripts de `sql/`.
- Variables de entorno `DB_PASSWORD` y, si MySQL está en otra máquina, `DB_HOST`.

Por defecto, Java intenta conectar a `127.0.0.1:3307`. En otra computadora, configura `DB_HOST` con la dirección del equipo que aloja MySQL. La contraseña se configura localmente y no se guarda en este repositorio.

## Ejecutar

Desde PowerShell, en la carpeta del proyecto:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\ejecutar.ps1
```

Después abre <http://localhost:8080>. Si no existe una cuenta preparada para la demostración, selecciona **Crear cuenta** en la pantalla inicial y registra una.

Para compilar sin iniciar el servidor:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\compilar.ps1
```

Para revisar el login con una cuenta ya registrada, ejecuta `scripts\verificar-login.ps1`. El script solicita los datos de forma interactiva y comprueba una contraseña válida y otra incorrecta. Necesita MySQL y Connector/J disponibles.

## Equipo y recorrido de la presentación

| Integrante | Requisito principal | Enfoque para explicar |
|---|---|---|
| Andreh | RF01 - Login | Formulario, validación de credenciales y sesión |
| Fabricio | RF03 - Calificaciones | Registro y consulta de notas asociadas a un curso |
| Manuel | RF12 - Promedio | Cálculo por curso y promedio general |
| Luis | RF07 - Tareas | Registro, consulta y finalización de tareas |

El guion para exponer está en [`documentación/guion-presentacion.pdf`](documentación/guion-presentacion.pdf), con una versión editable en [`documentación/guion-presentacion.md`](documentación/guion-presentacion.md).

## Organización del repositorio

```text
src/main/java/              servidor y lógica Java
src/main/resources/public/  interfaz HTML, CSS y JavaScript
src/main/java/modulos/      módulos de tareas, notas y promedios
sql/                        esquema y cambios de la base
docs/                       requisitos, pruebas y contribuciones
documentación/              guías del equipo y guion de exposición
scripts/                    comandos para compilar, ejecutar y verificar
```
