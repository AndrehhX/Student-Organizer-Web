# RF01 Iniciar sesión

## Cómo funciona

La pantalla inicial recoge usuario y contraseña. JavaScript evita enviar campos vacíos y manda el formulario a `POST /api/login`.

`ServidorWeb` pasa la solicitud a `ControladorLogin`. El controlador consulta `RepositorioUsuariosMySQL`, que busca al usuario en la tabla `usuarios` y compara el hash de la contraseña. Si las credenciales coinciden, el controlador crea un token aleatorio de sesión. El servidor lo devuelve en una cookie `HttpOnly` y `SameSite=Lax`; la sesión vence después de 30 minutos sin actividad.

Si los datos no coinciden, no se crea una sesión. La pantalla muestra el resultado. El botón de cierre llama a `POST /api/logout` y elimina la sesión del servidor.

## Crear una cuenta

La pantalla también ofrece un formulario de registro. Envía usuario, contraseña y carné a `POST /api/usuarios`. El servidor valida que estén completos y `RepositorioUsuariosMySQL` guarda la cuenta. Si el nombre ya existe, se devuelve un mensaje para que se elija otro.

## Partes principales

- `index.html`, `estilos.css` y `app.js`: formulario, estilos, mensajes y solicitudes del navegador.
- `ServidorWeb`: recibe login, registro y cierre de sesión.
- `ControladorLogin`: valida el acceso y administra sesiones independientes.
- `Usuario` y `Seguridad`: representan la cuenta y producen el hash de la contraseña.
- `RepositorioUsuariosMySQL`: consulta y guarda usuarios en MySQL.

## Pruebas

Conviene probar campos vacíos, credenciales incorrectas, registro correcto, nombre repetido, login correcto y cierre de sesión. Las pruebas de integración necesitan MySQL disponible y `DB_PASSWORD` configurada. No se garantiza que exista una cuenta de demostración sembrada: se puede crear desde el formulario.

## Nota técnica

El proyecto usa SHA-256 para esta entrega. Para un sistema real de cuentas se debería usar un algoritmo de almacenamiento de contraseñas diseñado para ello, con sal y factor de costo.

## Aporte de Andreh

El RF01 cubre la pantalla de acceso y su conexión con el servidor Java. La explicación se centra en el recorrido del formulario, la validación, la sesión y la separación entre interfaz y lógica.
