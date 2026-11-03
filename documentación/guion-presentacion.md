# Guion breve para presentar Student Organizer

Una guía para explicar lo que ya está en el proyecto. No hace falta leerla palabra por palabra: cada persona puede usar su parte como base y mostrar la pantalla mientras habla.

## La idea general

Student Organizer reúne cursos, tareas y calificaciones en una aplicación. La pantalla está hecha con HTML, CSS y JavaScript; el servidor está escrito en Java y guarda la información en MySQL.

**Recorrido para la demostración:** abrir la aplicación → crear o usar una cuenta disponible → entrar al dashboard → mostrar cursos, tareas y notas → registrar un dato → volver a la pantalla y confirmar el resultado.

**Frase para abrir:**

> Buenas tardes. Vamos a presentar Student Organizer, una aplicación para organizar cursos, tareas y calificaciones. Cada integrante va a explicar el módulo que trabajó y cómo se conecta con el resto del sistema.

## Andreh · RF01 Inicio de sesión

**Qué mostrar:** formulario de acceso y opción para crear una cuenta.

**Guion:**

> Mi parte fue el inicio de sesión. La pantalla recoge usuario y contraseña y los envía al servidor Java. El controlador consulta MySQL para comprobar las credenciales. Si son correctas, crea una sesión y permite entrar al dashboard; si no, la pantalla muestra el mensaje correspondiente. También conecté el registro de cuentas con el mismo flujo. La sesión se cierra desde el botón de salida.

**Si preguntan por la contraseña:**

> En esta entrega se compara el hash SHA-256 de la contraseña. Para un sistema real convendría usar un algoritmo especializado para contraseñas, con sal y factor de costo.

## Fabricio · RF03 Calificaciones

**Qué mostrar:** sección Notas y la tabla de calificaciones.

**Guion:**

> Mi parte permite registrar y consultar calificaciones por curso. Se elige el curso, se escribe la actividad, la nota obtenida, la nota máxima y la fecha. Java valida los valores y el repositorio los guarda en MySQL asociados al usuario y al curso. Después, la pantalla actualiza la tabla con la respuesta del servidor.

**Si preguntan por la validación:**

> La nota debe estar entre cero y la nota máxima, y el curso debe pertenecer a la cuenta que inició sesión.

## Manuel · RF12 Promedios

**Qué mostrar:** tarjeta del promedio general y promedio por curso.

**Guion:**

> Mi parte calcula el promedio a partir de las calificaciones registradas. Para cada curso suma los puntos obtenidos y los divide entre los puntos máximos; luego lo expresa sobre cien y redondea a dos decimales. El promedio general es el promedio simple de los promedios de los cursos. Si todavía no hay notas, la pantalla indica que no hay datos.

**Si preguntan por el ranking:**

> Ordena los cursos de la persona que inició sesión según su promedio. No compara estudiantes entre sí.

## Luis · RF07 Tareas y MySQL

**Qué mostrar:** formulario de tareas y una tarea antes y después de marcarla completa.

**Guion:**

> Mi parte permite crear, consultar y completar tareas. Cada tarea lleva nombre, curso, fecha de entrega y prioridad. Java verifica los datos y que el curso pertenezca al usuario, y luego guarda o actualiza la tarea en MySQL. El dashboard vuelve a consultar la lista para mostrar el cambio.

**Si preguntan por el almacenamiento:**

> MySQL se ejecuta en el equipo que aloja la base. El servidor Java se conecta con la configuración local del equipo; la contraseña de conexión no va dentro del repositorio.

## Cierre y preguntas frecuentes

- **¿Dónde se guardan los datos?** En MySQL: usuarios, cursos, tareas y calificaciones.
- **¿Hay una cuenta demo fija?** No hay que asumirlo. Si no hay una cuenta preparada, se crea desde “Crear cuenta”.
- **¿Qué pasa si falla la conexión?** El servidor necesita MySQL y la variable `DB_PASSWORD`; no tiene respaldo automático con CSV.
- **¿El ranking compara a todo el grupo?** No. Ordena los cursos de la cuenta actual.
- **¿Qué conviene explicar si preguntan por una clase?** Qué recibe, qué decide o guarda, y qué devuelve al siguiente paso.

**Frase para cerrar:**

> En conjunto, la pantalla envía las acciones, Java aplica las reglas y MySQL conserva los datos. Así los módulos trabajan con la misma cuenta y su información.

## Antes de presentar

- Confirmen que MySQL esté iniciado y que la aplicación abra en el equipo de demostración.
- Prueben el usuario que usarán; no cuenten con una cuenta demo por defecto.
- Acordar el orden de los cuatro módulos y quién navega la aplicación.
- Usen datos de ejemplo que puedan enseñar sin exponer credenciales personales.
- Si una integración no se ha probado en esa computadora, digan qué verificaron y qué queda pendiente; no afirmen que se probó si no fue así.
