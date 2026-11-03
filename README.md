# Student Organizer

Aplicación para organizar cursos, tareas y calificaciones. La interfaz está hecha con HTML, CSS y JavaScript; Java aplica las reglas del sistema y MySQL guarda la información.

## Funciones disponibles

- Crear cuenta, iniciar sesión y cerrar sesión.
- Crear cursos y consultar sus tareas y calificaciones.
- Registrar, consultar y completar tareas con fecha y prioridad.
- Registrar calificaciones por curso y actividad.
- Calcular promedios por curso y un promedio general.
- Ver los cinco cursos con mejor promedio de la cuenta actual. El ranking no compara estudiantes.

La sesión vence después de 30 minutos sin actividad. La aplicación requiere MySQL y no tiene respaldo automático con CSV.

## Cómo se conectan las partes

```text
Navegador (HTML, CSS y JavaScript)
                 ↓
       Servidor y lógica en Java
                 ↓
             Base MySQL
```

El navegador recoge las acciones del usuario, Java valida y coordina los módulos, y MySQL conserva usuarios, cursos, tareas y calificaciones.

## Requisitos

- JDK 17.
- MySQL 8 disponible en esta computadora o en otra accesible por red.
- MySQL Connector/J dentro de `lib/`.
- Base `student_organizer` y tablas preparadas con los archivos de `sql/`.
- Contraseña local de la cuenta MySQL `organizer_app` en `DB_PASSWORD`.

La conexión usa el puerto `3307` y `DB_HOST` vale `127.0.0.1` si no se configura. Si la base está en otra computadora, asigna a `DB_HOST` la dirección de ese equipo. No guardes contraseñas en el repositorio.

## Ejecutar

Desde PowerShell, en la carpeta del proyecto:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\ejecutar.ps1
```

El script pide la contraseña de MySQL de forma interactiva cuando `DB_PASSWORD` no está configurada. Después abre <http://localhost:8080>. Si no hay una cuenta preparada, elige **Crear cuenta**.

Para compilar sin iniciar el servidor:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\compilar.ps1
```

Para comprobar el inicio de sesión con una cuenta existente, ejecuta `scripts\verificar-login.ps1`. Solicita las credenciales localmente y verifica que una contraseña válida se acepte y una incorrecta se rechace. Requiere MySQL y Connector/J.

Las pruebas de integración con MySQL pueden agregar filas y dejarlas en la base. Ejecútalas en una base de pruebas o acuerda con el equipo cómo limpiar esos datos.

## Documentación del equipo

- [Guía de integración MySQL](documentación/integracion-mysql.md): conexión, rutas y verificación.
- [Guion de presentación en PDF](documentación/guion-presentacion.pdf).
- [Guion editable](documentación/guion-presentacion.md).
- [Inicio de sesión, RF01](docs/RF01_Login.md).
- [Calificaciones, RF03](docs/RF03_Notas.md).
- [Tareas, RF07](docs/RF07_Tareas.md).
- [Promedios, RF12](docs/tareas/manuel-rf12-promedio.txt).
- [Base de datos](docs/DB.md).
- [Horas y contribuciones](docs/horas-contribuciones.md).

## Módulos del equipo

| Integrante | Módulo | Enfoque |
|---|---|---|
| Andreh | RF01 · Inicio de sesión | Formulario, autenticación y sesión |
| Fabricio | RF03 · Calificaciones | Registro y consulta de notas por curso |
| Manuel | RF12 · Promedios | Cálculo por curso y promedio general |
| Luis | RF07 · Tareas | Registro, consulta y finalización de tareas |

## Carpetas principales

```text
src/main/java/              servidor y lógica Java
src/main/resources/public/  interfaz web
sql/                        esquema y scripts MySQL
docs/                       requisitos, pruebas y contribuciones
documentación/              integración y guion de exposición
scripts/                    compilación, ejecución y verificación
```
