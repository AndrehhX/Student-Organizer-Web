# RF01 - Iniciar sesión

## Qué hace

El sistema muestra un formulario para que la persona escriba su usuario y contraseña. JavaScript revisa que los campos tengan información y envía esos datos al servidor Java.

El `ControladorLogin` lee los usuarios del CSV. Cada contraseña ingresada pasa por `Seguridad` y se compara con el hash guardado. Si coincide, el controlador permite el acceso; si no, devuelve un mensaje de error.

## Recorrido sencillo

```text
HTML muestra el formulario
        ↓
JavaScript recoge los datos
        ↓
ServidorWeb recibe la petición
        ↓
ControladorLogin valida
        ↓
PersistenciaDatos lee usuarios.csv
        ↓
La página muestra el resultado
```

## Cuenta de prueba

- Usuario: `demo`
- Contraseña: `demo123`

## Casos probados

- Campos vacíos.
- Usuario que no existe.
- Contraseña incorrecta.
- Inicio de sesión correcto.
- Creación automática del archivo CSV.

## Aporte de Andreh

La parte de Andreh corresponde al RF01. Se preparó la interfaz del Login, se conectó con la lógica Java y se documentó el flujo y las pruebas. La estructura general de persistencia queda disponible para que el equipo la reutilice.

