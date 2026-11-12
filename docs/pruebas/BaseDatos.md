# Preparar y probar MySQL

Estas instrucciones son para el equipo que aloja la base y para quienes se conectan desde otra computadora.

## Requisitos

- Docker Desktop en el equipo que aloja MySQL.
- MySQL Connector/J en la carpeta `lib` del proyecto Java.
- DBeaver es opcional; sirve para revisar las tablas y ejecutar consultas.

## Iniciar el contenedor existente

En la computadora que aloja la base, abre Docker Desktop y ejecuta:

```powershell
docker start student-mysql-windows
docker ps
```

Los demás equipos no necesitan iniciar otro contenedor. Deben poder alcanzar el equipo anfitrión por la red.

## Configurar Java

En la computadora que ejecuta el servidor, define la contraseña en la sesión local de PowerShell. No la pegues en este documento ni la subas a GitHub:

```powershell
$clave = Read-Host "Contraseña MySQL de organizer_app" -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $clave).Password
$clave.Dispose()
$env:DB_HOST = "127.0.0.1"
powershell -ExecutionPolicy Bypass -File scripts\ejecutar.ps1
```

Si MySQL está en otra computadora, reemplaza `127.0.0.1` por la dirección de esa computadora. La conexión usa el puerto `3307`, la base `student_organizer` y el usuario `organizer_app`.

## Esquema y pruebas

Aplica los scripts de `sql/` en el orden indicado por el equipo. `sql/pruebas.sql` inserta un usuario, un curso y una tarea de ejemplo. Las pruebas Java que terminan en `MySQL` también insertan datos y los dejan en la base; ejecútalas en una base de pruebas o coordina su limpieza antes de volver a probar.

Para consultar lo más reciente desde DBeaver:

```sql
SELECT id, nombre, fecha_entrega, completada
FROM student_organizer.tareas
ORDER BY id DESC;
```

```sql
SELECT id, nombre_usuario, carne
FROM student_organizer.usuarios
ORDER BY id DESC;
```
