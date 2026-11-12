RF12 - Calcular promedio
Responsable: Manuel

Calcula el promedio de cada curso y el promedio general del estudiante
a partir de las calificaciones registradas en RF03.

Regla de calculo
- Promedio de un curso = (suma de notas obtenidas / suma de punteos maximos) x 100.
- Promedio general = promedio simple de los promedios por curso.
- Escala 0-100, 2 decimales, redondeo HALF_UP.

Clases
- CalculadoraPromedios: logica matematica, sin base de datos ni HTML.
- ResultadoPromedio: resultado de un curso (inmutable).
- ControladorPromedios: valida el usuario, consulta RepositorioCalificaciones
  y usa la calculadora.

Uso en el sistema
- ServidorWeb expone GET /api/promedios (usuario de la sesion actual).
- public/js/promedios.js muestra el promedio general en el dashboard y se
  actualiza al registrar una calificacion (evento calificaciones:actualizadas).
- public/js/ranking.js reutiliza /api/promedios para ordenar los cursos.

Pruebas
- src/test/java/PruebasPromedios.java
- Documentacion: docs/pruebas/promedios.md
