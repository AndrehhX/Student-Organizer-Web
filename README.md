# Student Organizer Web

Proyecto grupal para organizar estudiantes, calificaciones, tareas y promedios.

La idea de este repositorio es trabajar más ordenados que en el proyecto anterior:

- Java mantiene la lógica, los controladores, la seguridad y la persistencia.
- HTML y CSS construyen la interfaz.
- JavaScript conecta los formularios y botones con Java.
- Cada requisito tiene un espacio propio para que no estemos editando todos el mismo archivo.

## Requisitos del equipo

| Requisito | Responsable | Carpeta de trabajo |
|---|---|---|
| RF01 - Iniciar sesión | Andreh | `modulos/login` |
| RF03 - Registrar calificaciones | Fabricio | `modulos/calificaciones` |
| RF12 - Calcular promedio | Manuel | `modulos/promedio` |
| RF07 - Registrar tareas | Luis | `modulos/tareas` |

## Cómo vamos a trabajar

1. Cada persona crea su propia rama desde `main`.
2. Cada persona trabaja principalmente en su carpeta.
3. Antes de mezclar, se prueba el cambio y se abre un Pull Request.
4. Nadie sube directamente a `main`.
5. Si hay que tocar un archivo compartido, primero se avisa en el grupo.
6. Cada integrante llena sus horas reales en `docs/horas-contribuciones.md`.

## Primer avance funcional

La primera parte preparada es el Login. La pantalla está hecha con HTML y CSS, JavaScript envía los datos y Java valida contra `data/usuarios.csv`.

La cuenta de demostración es:

- Usuario: `demo`
- Contraseña: `demo123`

## Estructura principal

```text
src/main/java/              lógica Java
src/main/resources/public/  interfaz HTML, CSS y JavaScript
src/main/java/modulos/      espacios de trabajo por requisito
data/                       archivos de persistencia
docs/                       guías, horas, pruebas y planificación
```

Para ver las instrucciones de cada persona, revisa los archivos dentro de `docs/tareas` y los `README.txt` de cada módulo.

