# Base de datos y pruebas 
Aqui se documenta la instalacion de la base de datos y sus pruebas realizadas

## Instalacion 
### Dbeaver community
Descargar e instalar:
https://dbeaver.io/download/
DBeaver permite ejecutar SQL y consultar los registros de MySQL.

### MySQL Connector/J
Descargar:
https://dev.mysql.com/downloads/connector/j/

1. Seleccionar `Platform Independent`.
2. Descargar el ZIP.
3. Extraer su contenido.
4. Localizar el archivo `.jar`.
5. Copiarlo a la carpeta `lib` del proyecto.
6. Renombrarlo como `mysql-connector-j.jar`.

### Docker desktop

Solo lo necesita la computadora que aloja la base de datos:
https://docs.docker.com/desktop/setup/install/windows-install/
Quienes se conecten al servidor compartido no necesitan crear otro contenedor MySQL.

## configuracion MySQL
Credenciales de desarrollo:

| Parámetro | Valor |
|---|---|
| Base de datos | `student_organizer` |
| Usuario | `organizer_app` |
| Contraseña | `OrganizerLocal2026` |
| Puerto de acceso desde Windows | `3307` |
| Host desde la computadora que aloja MySQL | `127.0.0.1` |
| Contenedor | `student-mysql-windows` |

Esta cuenta permite conectarse al motor MySQL.

### Iniciar el contenedor existente

En la computadora que aloja MySQL, abrir Docker Desktop y ejecutar: (luis)

```powershell
docker start student-mysql-windows
docker ps
```

No es necesario crear otro contenedor cada vez que se trabaja.

## Pruebas realizadas
ejecutar el script completo de pruebas.sql en DBeaver 

Resultado esperado:

- Un usuario nuevo con carné `PRUEBA`.
- Una tarea asociada a ese usuario.
- Una fila en la consulta final.

Cada ejecución agrega un usuario y una tarea nuevos.

### Ejecutar

Este es el comando comprobado en el entorno de desarrollo:

```powershell
$env:DB_PASSWORD = "OrganizerLocal2026"
& (Join-Path (Split-Path -Parent (Get-Command javac).Source) "java.exe") -cp "out;lib/mysql-connector-j.jar" PruebaTareasMySQL
```

Resultado esperado:

```text
Tarea guardada en MySQL. ID: ...
Usuario propietario. ID: ...
```

La prueba busca el usuario más reciente con carné `PRUEBA` e inserta una tarea asociada a él.

Cada ejecución agrega otra tarea.

### Comprobar los registros

En DBeaver:

```sql
SELECT *
FROM student_organizer.tareas
ORDER BY id DESC;
```

Debe aparecer la tarea:

```text
Tarea creada desde Java
```

Para consultar usuarios:

```sql
SELECT id, nombre_usuario, carne
FROM student_organizer.usuarios
ORDER BY id DESC;
```