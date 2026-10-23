# Student Organizer Web

Proyecto grupal para organizar estudiantes, tareas, calificaciones y promedios.

## Estado actual

### Ya implementado

- Inicio de sesión con usuario y contraseña.
- Validación de credenciales con Java.
- Usuarios guardados en `data/usuarios.csv`.
- Contraseñas protegidas con hash antes de guardarse.
- Dashboard principal responsive.
- Registro, listado y finalización de tareas.
- Cierre de sesión desde el botón `Log off`.
- Navegación para Tareas, Notas y Cursos.
- Registro y consulta de calificaciones mediante `/api/calificaciones`.
- Cálculo y consulta del promedio mediante `/api/promedios`.
- Registro y consulta de cursos mediante `/api/cursos`.
- Modo MySQL integrado para Cursos, Tareas, Calificaciones y Promedios cuando se configura `DB_PASSWORD`.
- Animaciones suaves en login y dashboard.
- Respeto por la preferencia de reducir movimiento del dispositivo.
- Estructura SQL y repositorios MySQL preparados para la integración de base de datos.

### Pendiente

- Completar las pruebas de integración contra la base de datos real.
- Revisar la eliminación definitiva de `prioridad` en las partes antiguas de Tareas, si el requisito final ya no la utilizará.

Si `DB_PASSWORD` no está configurada, el proyecto conserva el modo local de respaldo para poder probar el login y las tareas sin Docker. Con MySQL configurado, el servidor utiliza los repositorios de Cursos, Tareas, Calificaciones y Promedios.

## Requisitos del equipo

| Requisito | Responsable | Carpeta principal |
|---|---|---|
| RF01 - Iniciar sesión | Andreh | `src/main/java/controlador`, `src/main/java/modelo`, `src/main/java/servidor` |
| RF03 - Registrar calificaciones | Fabricio | `src/main/java/modulos/calificaciones` |
| RF12 - Calcular promedio | Manuel | `src/main/java/modulos/promedio` |
| RF07 - Registrar tareas | Luis | `src/main/java/modulos/tareas` |

## Cómo ejecutar el proyecto

Desde PowerShell, dentro de la carpeta del proyecto:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\ejecutar.ps1
```

Después abre `http://localhost:8080` en el navegador.

Cuenta de demostración:

- Usuario: `demo`
- Contraseña: `demo123`

## Verificaciones

Compilar el proyecto:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\compilar.ps1
```

Probar el login:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\verificar-login.ps1
```

Revisar la conexión del dashboard:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\verificar-dashboard.ps1
```

Las pruebas Java principales son:

- `PruebasLogin`
- `PruebasTareas`
- `PruebasCalificaciones`
- `PruebasPromedios`
- `PruebasServidorTareas`
- `PruebasServidorDashboard`
- `PruebasServidorCursos`

Estas pruebas pasan en la versión actual. La prueba contra MySQL/Docker queda pendiente de ejecutarse en el equipo donde Luis tiene levantada la base de datos.

## Cómo trabajamos

1. Cada persona crea su propia rama desde `main`.
2. Cada persona trabaja principalmente en su carpeta.
3. Antes de mezclar, se prueba el cambio y se abre un Pull Request.
4. No se sube directamente a `main` sin revisar el cambio.
5. Si hay que tocar un archivo compartido, primero se avisa en el grupo.
6. Cada integrante registra sus horas reales en `docs/horas-contribuciones.md`.

## Estructura principal

```text
src/main/java/              lógica Java y servidor
src/main/resources/public/  interfaz HTML, CSS y JavaScript
src/main/java/modulos/      espacios de trabajo por requisito
data/                       archivos de persistencia local
sql/                        esquema y scripts de base de datos
docs/                       guías, horas, pruebas y planificación
scripts/                    comandos para compilar, ejecutar y verificar
```




## Entrevistas tercera entrega

https://youtu.be/9yRYS6GIyck?si=1H5ffpH4WgA-2pg7
