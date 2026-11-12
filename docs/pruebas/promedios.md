# Pruebas de promedios (RF12)

Responsable: Manuel

## Regla de cálculo

- Promedio de un curso = (suma de notas obtenidas / suma de punteos máximos) × 100.
  El punteo máximo de cada actividad funciona como su peso.
- Promedio general = promedio simple de los promedios por curso.
- Escala 0–100, 2 decimales, redondeo `HALF_UP`.
- Si no hay calificaciones no se muestra 0: se indica que no hay notas registradas.

Ejemplo: 12/15 y 5/5 en Matemática → (12 + 5) / (15 + 5) × 100 = **85.00**.

## Archivos del módulo

| Archivo | Responsabilidad |
| --- | --- |
| `modulos/promedio/CalculadoraPromedios.java` | Lógica matemática (sin repositorio, sin MySQL, sin HTML) |
| `modulos/promedio/ResultadoPromedio.java` | Resultado de un curso: id, nombre, promedio y cantidad de notas |
| `modulos/promedio/ControladorPromedios.java` | Valida el usuario, consulta `RepositorioCalificaciones` y usa la calculadora |
| `public/js/promedios.js` | Consulta `/api/promedios` y muestra el resultado en la tarjeta |
| `servidor/ServidorWeb.java` | Ruta `GET /api/promedios` con el usuario de la sesión |

`ServiciosMySQL` entrega a `ControladorPromedios` el mismo repositorio que usa calificaciones.
`ranking.js` reutiliza `/api/promedios` para ordenar los cursos del usuario.

## Pruebas Java

`src/test/java/PruebasPromedios.java` usa el repositorio en memoria de las pruebas y registra
las notas con `ControladorCalificaciones`, por lo que no necesita MySQL ni el servidor.

| Caso | Resultado esperado | Resultado |
| --- | --- | --- |
| Estudiante sin calificaciones | Sin promedio general | paso |
| Lista vacía en la calculadora | Sin promedio | paso |
| Curso con varias notas (12/15 y 5/5) | 85.00 | paso |
| Promedio general de 2 cursos | 87.50 | paso |
| Promedios por curso | 2 cursos | paso |
| Separación de estudiantes | Cada uno ve solo sus notas | paso |
| Curso sin calificaciones | Sin promedio | paso |
| Redondeo 2/3 | 66.67 | paso |
| Usuario vacío | Rechazar | paso |
| Punteos máximos distintos (5/5 y 15/30) | 57.14 | paso |
| Redondeo HALF_UP (1/32) | 3.13 | paso |
| No modifica las notas originales | Lista y notas sin cambios | paso |
| Nota nueva sin guardar copias | El promedio se actualiza | paso |
| Lista nula en la calculadora | Rechazar | paso |
| Repositorio que devuelve null | Error controlado | paso |

### Ejecutar

```bash
mkdir -p out && javac --add-modules jdk.httpserver -encoding UTF-8 -cp "lib/*" -d out $(find src/main/java -name "*.java")
javac -encoding UTF-8 -cp out -d out src/test/java/modulos/calificaciones/RepositorioCalificacionesMemoria.java src/test/java/PruebasPromedios.java
java -cp out PruebasPromedios
```

Resultado esperado:

```text
PruebasPromedios: todas las pruebas pasaron.
```

## Pruebas desde el navegador (con MySQL)

| Caso | Resultado esperado | Resultado |
| --- | --- | --- |
| `GET /api/promedios` sin sesión | 401 "Debes iniciar sesión." | paso |
| Usuario nuevo sin notas | `--` y "Aún no tienes calificaciones registradas." | paso |
| Registrar 12/15 y 5/5 (Matemática) y 9/10 (Química) | 87.50 "Basado en 2 cursos" | paso |
| Registrar una nota desde el formulario | La tarjeta se actualiza sin recargar | paso |
| Ruta no disponible | `--` y "No se pudo cargar el promedio." | paso |

## Formato de `GET /api/promedios`

```json
{
  "promedioGeneral": 87.50,
  "cursos": [
    { "idCurso": "1", "nombreCurso": "Matematica", "promedio": 85.00, "cantidadCalificaciones": 2 },
    { "idCurso": "2", "nombreCurso": "Quimica", "promedio": 90.00, "cantidadCalificaciones": 1 }
  ]
}
```

Sin calificaciones: `{"promedioGeneral":null,"cursos":[]}`.

## Pendientes

- El modelo no tiene créditos por curso ni tipo de actividad; si se agregan, solo cambia
  la regla de `CalculadoraPromedios`.
