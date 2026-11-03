document.addEventListener('DOMContentLoaded', () => {
    const listaCursos = document.getElementById('lista-cursos');
    const formulario = document.getElementById('form-curso');
    const nombreCurso = document.getElementById('nombre-curso');
    const botonCrear = document.getElementById('btn-crear-curso');
    const mensaje = document.getElementById('mensaje-cursos');
    const detalle = document.getElementById('detalle-curso');
    const detalleNombre = document.getElementById('detalle-curso-nombre');
    const cerrarDetalle = document.getElementById('cerrar-detalle-curso');
    const detalleCalificaciones = document.getElementById('detalle-calificaciones');
    const detalleTareas = document.getElementById('detalle-tareas');

    if (!listaCursos) return;

    function mostrarMensaje(texto, error = false) {
        if (!mensaje) return;
        mensaje.textContent = texto;
        mensaje.className = error
            ? 'mt-2 text-sm text-red-700'
            : 'mt-2 text-sm text-brand-dark';
    }

    async function cargarCursos() {
        listaCursos.textContent = 'Cargando cursos...';

        try {
            const respuesta = await fetch('/api/cursos', {
                headers: { 'Accept': 'application/json' }
            });
            if (!respuesta.ok) throw new Error('No se pudieron cargar los cursos.');

            const cursos = await respuesta.json();
            if (!Array.isArray(cursos)) throw new Error('Formato de cursos incorrecto.');

            listaCursos.replaceChildren();

            if (cursos.length === 0) {
                const vacio = document.createElement('p');
                vacio.className = 'text-sm text-gray-500 text-center sm:col-span-2';
                vacio.textContent = 'Todavía no tienes cursos registrados.';
                listaCursos.append(vacio);
                return;
            }

            cursos.forEach(curso => {
                const boton = document.createElement('button');
                boton.type = 'button';
                boton.className = 'bg-white rounded-xl p-4 shadow-sm border border-brand-medium/30 flex items-center gap-3 text-left hover:border-brand-medium';
                boton.addEventListener('click', () => cargarDetalle(curso));

                const icono = document.createElement('i');
                icono.setAttribute('data-lucide', 'book-open');
                icono.className = 'w-6 h-6 text-brand-medium shrink-0';

                const nombre = document.createElement('span');
                nombre.className = 'font-semibold text-brand-dark';
                nombre.textContent = curso.nombreCurso;

                boton.append(icono, nombre);
                listaCursos.append(boton);
            });

            if (window.lucide) window.lucide.createIcons();
        } catch (error) {
            console.error(error);
            listaCursos.textContent = 'No se pudieron cargar los cursos.';
        }
    }

    async function cargarDetalle(curso) {
        if (!detalle) return;
        detalle.classList.remove('hidden');
        detalleNombre.textContent = curso.nombreCurso;
        detalleCalificaciones.textContent = 'Cargando...';
        detalleTareas.textContent = 'Cargando...';

        try {
            const respuesta = await fetch(
                `/api/cursos/detalle?cursoId=${encodeURIComponent(curso.idCurso)}`
            );
            if (!respuesta.ok) throw new Error('No se pudo cargar el detalle.');

            const datos = await respuesta.json();
            mostrarCalificaciones(datos.calificaciones || []);
            mostrarTareas(datos.tareas || []);
        } catch (error) {
            detalleCalificaciones.textContent = error.message;
            detalleTareas.textContent = error.message;
        }
    }

    function mostrarCalificaciones(calificaciones) {
        detalleCalificaciones.replaceChildren();
        if (calificaciones.length === 0) {
            detalleCalificaciones.textContent = 'No hay calificaciones.';
            return;
        }

        calificaciones.forEach(calificacion => {
            const item = document.createElement('p');
            item.className = 'rounded-lg bg-brand-bright/40 p-3 text-sm text-gray-700';
            item.textContent = `${calificacion.nombreActividad}: ${calificacion.notaObtenida}/${calificacion.notaMaxima}`;
            detalleCalificaciones.append(item);
        });
    }

    function mostrarTareas(tareas) {
        detalleTareas.replaceChildren();
        if (tareas.length === 0) {
            detalleTareas.textContent = 'No hay tareas.';
            return;
        }

        tareas.forEach(tarea => {
            const item = document.createElement('p');
            item.className = 'rounded-lg bg-brand-bright/40 p-3 text-sm text-gray-700';
            item.textContent = `${tarea.nombre} · ${tarea.fechaEntrega}`;
            detalleTareas.append(item);
        });
    }

    if (formulario) {
        formulario.addEventListener('submit', async evento => {
            evento.preventDefault();
            const nombre = nombreCurso.value.trim();
            if (!nombre) {
                mostrarMensaje('Escribe el nombre del curso.', true);
                return;
            }

            botonCrear.disabled = true;
            mostrarMensaje('Guardando curso...');
            try {
                const respuesta = await fetch('/api/cursos', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: new URLSearchParams({ nombre })
                });
                const datos = await respuesta.json();
                if (!respuesta.ok) throw new Error(datos.mensaje || 'No se pudo crear el curso.');

                formulario.reset();
                mostrarMensaje('Curso creado correctamente.');
                await cargarCursos();
                document.dispatchEvent(new CustomEvent('cursos:actualizados'));
            } catch (error) {
                mostrarMensaje(error.message, true);
            } finally {
                botonCrear.disabled = false;
            }
        });
    }

    if (cerrarDetalle) {
        cerrarDetalle.addEventListener('click', () => detalle.classList.add('hidden'));
    }

    document.querySelectorAll('[data-seccion="cursos"]')
        .forEach(enlace => enlace.addEventListener('click', cargarCursos));

    cargarCursos();
});
