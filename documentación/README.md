# Documentación del proyecto

Esta carpeta reúne la documentación técnica y el estado actual de la integración.

## Documentos

- [Integración con MySQL](integracion-mysql.md): qué quedó implementado, qué se probó y qué falta conectar.

## Estado actual

- El login y el dashboard funcionan con el servidor Java.
- La estructura de cursos, tareas, calificaciones y promedios ya tiene repositorios MySQL.
- `ServiciosMySQL` centraliza la creación de esos repositorios y controladores.
- `ServidorWeb` puede iniciar en modo MySQL cuando existe la variable `DB_PASSWORD`.
- `/api/cursos` ya permite listar y registrar cursos para el usuario autenticado.
- En modo MySQL, Tareas, Calificaciones y Promedios también utilizan sus repositorios de base de datos.
- Si no se configura MySQL, se mantiene el modo local de respaldo para las pruebas del servidor.
- La conexión real con Docker/MySQL todavía debe probarse en el equipo que tiene la base levantada.
