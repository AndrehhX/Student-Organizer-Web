# Student Organizer Web

Proyecto grupal para organizar estudiantes, tareas, calificaciones y promedios.

La idea es que cada integrante trabaje en su propio requisito y que después se conecten las partes sin estar editando todos los mismos archivos.

## Qué ya funciona

- Inicio de sesión con usuario y contraseña.
- Validación de credenciales con Java.
- Usuarios guardados en `data/usuarios.csv`.
- Contraseñas protegidas con hash antes de guardarse.
- Dashboard principal con estilo responsive.
- Registro de tareas con nombre, fecha y prioridad.
- Lista de tareas pendientes.
- Marcar tareas como completadas.
- Cierre de sesión desde el botón `Log off`.
- Menú funcional para Tareas, Notas y Cursos.
- Panel sencillo de notificaciones.
- Animaciones suaves en el login y el dashboard.
- Configuración para reducir animaciones si el dispositivo lo solicita.

## Partes que todavía faltan conectar

- Registrar y mostrar calificaciones reales.
- Calcular y mostrar el promedio real.
- Conectar las secciones de Notas y Cursos con la base de datos.
- Terminar las pruebas de la integración completa.

Las pantallas de Notas y Cursos ya están preparadas para recibir esa información, pero por ahora muestran un aviso mientras se terminan esas partes.

## Requisitos del equipo

| Requisito | Responsable | Carpeta de trabajo |
|---|---|---|
| RF01 - Iniciar sesión | Andreh | `src/main/java/modulos/login` |
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

Para probar el login:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\verificar-login.ps1
```

Para revisar que el dashboard tenga conectados sus controles:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\verificar-dashboard.ps1
```

También se puede compilar el proyecto con:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\compilar.ps1
```

## Cómo vamos a trabajar

1. Cada persona crea su propia rama desde `main`.
2. Cada persona trabaja principalmente en su carpeta.
3. Antes de mezclar, se prueba el cambio y se abre un Pull Request.
4. Nadie sube directamente a `main`.
5. Si hay que tocar un archivo compartido, primero se avisa en el grupo.
6. Cada integrante llena sus horas reales en `docs/horas-contribuciones.md`.

## Estructura principal

```text
src/main/java/              lógica Java
src/main/resources/public/  interfaz HTML, CSS y JavaScript
src/main/java/modulos/      espacios de trabajo por requisito
data/                       archivos de persistencia
docs/                       guías, horas, pruebas y planificación
scripts/                    comandos para compilar, ejecutar y verificar
```




## Entrevistas tercera entrega

https://youtu.be/9yRYS6GIyck?si=1H5ffpH4WgA-2pg7
