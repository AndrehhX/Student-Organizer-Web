# Pruebas del Login

## Casos

| Caso | Resultado esperado |
|---|---|
| Usuario o contraseña vacíos | La pantalla pide completar los campos |
| Usuario inexistente | Rechaza el acceso y no crea sesión |
| Contraseña incorrecta | Rechaza el acceso y no crea sesión |
| Credenciales válidas | Crea una sesión y abre el dashboard |
| Registro con campos incompletos | No guarda la cuenta |
| Registro con nombre repetido | Informa que ya existe una cuenta con ese nombre |
| Cierre de sesión | Elimina la sesión y vuelve a la pantalla inicial |

## Requisitos

La autenticación consulta MySQL y acepta el nombre de usuario o el correo. Se necesita el servidor levantado, `DB_PASSWORD`, MySQL Connector/J y una cuenta registrada. Para la presentación, ejecuta `sql/datos_prueba.sql` en una base de desarrollo o prueba; la aplicación no carga esas cuentas por sí sola.

La prueba de integración `PruebasLogin` requiere `DB_PASSWORD` y una base MySQL accesible. La prueba modifica la base; úsala solo en una instancia de desarrollo o de pruebas.
