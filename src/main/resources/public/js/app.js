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
        const respuesta = await fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ usuario, contrasena })
        });
        const resultado = await respuesta.json();

        mostrarMensaje(resultado.mensaje,
            resultado.exitoso ? 'exito' : 'error');
    } catch (error) {
        mostrarMensaje('No se pudo conectar con el servidor Java.', 'error');
    } finally {
        boton.disabled = false;
    }
});
