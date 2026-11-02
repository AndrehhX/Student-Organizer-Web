const formulario = document.querySelector('#login-form');
const usuarioInput = document.querySelector('#usuario');
const contrasenaInput = document.querySelector('#contrasena');
const mensaje = document.querySelector('#mensaje');
const boton = formulario.querySelector('button');
const mostrarRegistro = document.querySelector('#mostrar-registro');
const formularioRegistro = document.querySelector('#registro-form');
const registroUsuario = document.querySelector('#registro-usuario');
const registroContrasena = document.querySelector('#registro-contrasena');
const registroCarne = document.querySelector('#registro-carne');
const registroMensaje = document.querySelector('#registro-mensaje');
const botonRegistro = formularioRegistro.querySelector('button');

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

function mostrarMensajeRegistro(texto, tipo) {
    registroMensaje.textContent = texto;
    registroMensaje.className = `mensaje ${tipo}`;
}

mostrarRegistro.addEventListener('click', () => {
    formularioRegistro.hidden = false;
    mostrarRegistro.hidden = true;
    registroUsuario.focus();
});

formularioRegistro.addEventListener('submit', async (evento) => {
    evento.preventDefault();

    const usuario = registroUsuario.value.trim();
    const contrasena = registroContrasena.value;
    const carne = registroCarne.value.trim();

    if (!usuario || !contrasena || !carne) {
        mostrarMensajeRegistro(
            'Completa usuario, contraseña y carné.',
            'error'
        );
        return;
    }

    botonRegistro.disabled = true;
    mostrarMensajeRegistro('Guardando usuario...', '');

    try {
        const datos = new URLSearchParams({
            usuario,
            contrasena,
            carne
        });

        const respuesta = await fetch('/api/usuarios', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: datos
        });

        const resultado = await respuesta.json();

        mostrarMensajeRegistro(
            resultado.mensaje,
            resultado.exitoso ? 'exito' : 'error'
        );

        if (resultado.exitoso) {
            registroContrasena.value = '';
            formularioRegistro.hidden = true;
            mostrarRegistro.hidden = false;
            usuarioInput.value = usuario;
            contrasenaInput.focus();
        }
    } catch (error) {
        mostrarMensajeRegistro(
            'No se pudo conectar con el servidor Java.',
            'error'
        );
    } finally {
        botonRegistro.disabled = false;
    }
});
