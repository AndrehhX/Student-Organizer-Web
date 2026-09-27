const formulario = document.querySelector('#login-form');
const usuarioInput = document.querySelector('#usuario');
const contrasenaInput = document.querySelector('#contrasena');
const mensaje = document.querySelector('#mensaje');
const boton = formulario.querySelector('button');

function mostrarMensaje(texto, tipo) {
    mensaje.textContent = texto;
    mensaje.className = `mensaje ${tipo}`;
}

formulario.addEventListener('submit', async (evento) => {
    evento.preventDefault();

    const usuario = usuarioInput.value.trim();
    const contrasena = contrasenaInput.value;

    if (!usuario || !contrasena) {
        mostrarMensaje('Completa usuario y contraseña.', 'error');
        return;
    }

    boton.disabled = true;
    mostrarMensaje('Revisando datos...', '');

    try {
        const datos = new URLSearchParams({ usuario, contrasena });

        const respuesta = await fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: datos
        });

        const resultado = await respuesta.json();

        // Mostrar el mensaje que envía el servidor
        mostrarMensaje(
            resultado.mensaje,
            resultado.exitoso ? 'exito' : 'error'
        );

        // Si el login fue correcto, esperar 1 segundo y cambiar de página
        if (resultado.exitoso) {
            setTimeout(() => {
                window.location.href = "dashboard.html";
            }, 1000);
        }

    } catch (error) {
        mostrarMensaje('No se pudo conectar con el servidor Java.', 'error');
    } finally {
        boton.disabled = false;
    }
});