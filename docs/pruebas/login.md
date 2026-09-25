# Pruebas del Login

Las pruebas principales se ejecutan con `PruebasLogin.java`.

| Caso | Resultado esperado |
|---|---|
| Usuario y contraseña vacíos | Rechazar el acceso |
| Usuario inexistente | Rechazar el acceso |
| Contraseña incorrecta | Rechazar el acceso |
| `demo` y `demo123` | Permitir el acceso |
| CSV inexistente | Crear el archivo y la cuenta demo |
| Guardar usuario nuevo | Agregarlo sin borrar los anteriores |

