## RF03 - Registrar calificaciones

## Qué hace

El sistema permite que el estudiante registre sus calificaciones académicas desde el apartado Notas del dashboard. La persona escribe el nombre del curso, la actividad, la nota obtenida, el punteo máximo y la fecha.

JavaScript valida que los campos estén completos y que las calificaciones sean correctas. Después envía los datos al servidor Java.

El ControladorCalificaciones utiliza la clase Calificacion para validar los datos y el RepositorioCalificacionesMemoria para guardar temporalmente las notas. Cada calificación recibe un identificador único y queda asociada al usuario y al curso correspondiente.

Las calificaciones registradas se muestran en una tabla dentro del dashboard. Además, los nombres de los cursos pueden consultarse desde el apartado Cursos, evitando mostrar cursos duplicados.

## Recorrido sencillo

HTML muestra el formulario de Notas
        ↓
JavaScript recoge y valida los datos
        ↓
ServidorWeb recibe la petición
        ↓
ControladorCalificaciones registra la nota
        ↓
Calificacion valida los datos
        ↓
RepositorioCalificacionesMemoria guarda la nota
        ↓
La página muestra la calificación registrada
        ↓
El apartado Cursos muestra el curso utilizado

## Datos de prueba

Curso: Matemática
Actividad: Parcial 1
Nota obtenida: 85
Punteo máximo: 100
Fecha: 2026-10-15
Casos probados
Registro correcto de una calificación.
Rechazo de notas negativas.
Rechazo de notas superiores al punteo máximo.


## Aporte de Fabricio

Mi parte corresponde al RF03 – Registrar Calificaciones. Se desarrolló la clase Calificacion, el controlador, junto a su compatibilidad con el resto de archivos y el index

También se preparó el formulario de Notas dentro del dashboard, se conectó con JavaScript y se integraron las rutas necesarias en ServidorWeb para registrar y consultar calificaciones.

Se incorporó la posibilidad de escribir libremente el nombre del curso y se preparó su visualización en el apartado Cursos.

La estructura queda disponible para que Luis conecte la base de datos MySQL y Manuel utilice las calificaciones registradas para calcular los promedios académicos.
