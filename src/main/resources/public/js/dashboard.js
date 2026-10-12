const botonCerrarSesion = document.querySelector('#boton-cerrar-sesion');
const botonNotificaciones = document.querySelector('#boton-notificaciones');
const panelNotificaciones = document.querySelector('#panel-notificaciones');
const enlacesSeccion = document.querySelectorAll('[data-seccion]');
const paneles = {
    tareas: document.querySelector('#panel-tareas'),
    notas: document.querySelector('#panel-notas'),
    cursos: document.querySelector('#panel-cursos')
};

function cerrarPanelNotificaciones() {
    if (panelNotificaciones) {
        panelNotificaciones.classList.add('hidden');
        botonNotificaciones?.setAttribute('aria-expanded', 'false');
    }
}

function mostrarSeccion(nombre) {
    Object.entries(paneles).forEach(([clave, panel]) => {
        panel.classList.toggle('hidden', clave !== nombre);
    });
    cerrarPanelNotificaciones();

    enlacesSeccion.forEach((enlace) => {
        const activo = enlace.dataset.seccion === nombre;
        enlace.classList.toggle('bg-brand-medium', activo);
        enlace.classList.toggle('shadow-sm', activo);
        enlace.classList.toggle('bg-brand-light', !activo);
        enlace.classList.toggle('text-brand-dark', !activo);
    });

    if (window.lucide) {
        window.lucide.createIcons();
    }
}

enlacesSeccion.forEach((enlace) => {
    enlace.addEventListener('click', (evento) => {
        evento.preventDefault();
        mostrarSeccion(enlace.dataset.seccion);
    });
});

if (botonNotificaciones && panelNotificaciones) {
    botonNotificaciones.addEventListener('click', () => {
        const abierto = !panelNotificaciones.classList.contains('hidden');
        panelNotificaciones.classList.toggle('hidden', abierto);
        botonNotificaciones.setAttribute('aria-expanded', String(!abierto));
    });
}

if (botonCerrarSesion) {
    botonCerrarSesion.addEventListener('click', async () => {
        botonCerrarSesion.disabled = true;

        try {
            const respuesta = await fetch('/api/logout', { method: 'POST' });
            if (!respuesta.ok) {
                throw new Error('No se pudo cerrar la sesión.');
            }

            window.location.href = '/';
        } catch (error) {
            botonCerrarSesion.disabled = false;
            window.alert(error.message);
        }
    });
}

mostrarSeccion('tareas');
