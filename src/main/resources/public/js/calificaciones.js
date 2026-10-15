
document.addEventListener('DOMContentLoaded', () => {

    // =====================================================
    // ELEMENTOS DEL FORMULARIO
    // =====================================================

    const formulario = document.getElementById('form-calificaciones');
    if (!formulario) return;

    const selectCurso = document.getElementById('calificacion-curso');
    const inputActividad = document.getElementById('calificacion-actividad');
    const inputObtenida = document.getElementById('calificacion-obtenida');
    const inputMaxima = document.getElementById('calificacion-maxima');
    const inputFecha = document.getElementById('calificacion-fecha');
    const btnGuardar = document.getElementById('btn-guardar-calificacion');
    const mensaje = document.getElementById('mensaje-calificaciones');
    const tablaBody = document.getElementById('tabla-calificaciones-body');
    const spinner = document.getElementById('cargando-calificaciones');
    const sinRegistros = document.getElementById('sin-calificaciones');

    if (!selectCurso || !inputActividad || !inputObtenida ||
        !inputMaxima || !inputFecha || !btnGuardar ||
        !mensaje || !tablaBody) {
        console.error('Faltan elementos del módulo de calificaciones.');
        return;
    }

    // =====================================================
    // INICIALIZACION
    // =====================================================

    cargarCursos();
    cargarCalificaciones();

    // =====================================================
    // REGISTRAR CALIFICACION
    // =====================================================

    formulario.addEventListener('submit', async (evento) => {
        evento.preventDefault();
        ocultarMensaje();

        const idCurso = selectCurso.value.trim();
        const nombreCurso =
            selectCurso.selectedOptions[0]?.textContent.trim() || '';

        const nombreActividad = inputActividad.value.trim();
        const notaObtenidaTexto = inputObtenida.value.trim();
        const notaMaximaTexto = inputMaxima.value.trim();
        const fecha = inputFecha.value;

        // VALIDACIONES

        if (!idCurso) {
            mostrarMensaje('Selecciona un curso.', 'error');
            return;
        }

        if (!nombreCurso) {
            mostrarMensaje('El curso seleccionado no es válido.', 'error');
            return;
        }

        if (!nombreActividad) {
            mostrarMensaje('Ingresa el nombre de la actividad.', 'error');
            return;
        }

        if (!notaObtenidaTexto || !notaMaximaTexto) {
            mostrarMensaje('Completa las calificaciones.', 'error');
            return;
        }

        const notaObtenida = Number(notaObtenidaTexto);
        const notaMaxima = Number(notaMaximaTexto);

        if (!Number.isFinite(notaObtenida) ||
            !Number.isFinite(notaMaxima)) {
            mostrarMensaje('Ingresa números válidos.', 'error');
            return;
        }

        if (notaObtenida < 0) {
            mostrarMensaje('La nota no puede ser negativa.', 'error');
            return;
        }

        if (notaMaxima <= 0) {
            mostrarMensaje('El punteo máximo debe ser mayor a cero.', 'error');
            return;
        }

        if (notaObtenida > notaMaxima) {
            mostrarMensaje(
                'La nota no puede superar el punteo máximo.',
                'error'
            );
            return;
        }

        if (!fecha || !inputFecha.validity.valid) {
            mostrarMensaje('Selecciona una fecha válida.', 'error');
            return;
        }

        // DATOS PARA SERVIDORWEB.JAVA

        const nuevaCalificacion = new URLSearchParams({
            idCurso,
            nombreCurso,
            nombreActividad,
            notaObtenida: notaObtenidaTexto,
            notaMaxima: notaMaximaTexto,
            fecha
        });

        setEstadoGuardando(true);
        mostrarMensaje('Guardando calificación...', 'info');

        try {
            const respuesta = await fetch('/api/calificaciones', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Accept': 'application/json'
                },
                body: nuevaCalificacion
            });

            if (!respuesta.ok) {
                throw new Error(await leerError(respuesta));
            }

            formulario.reset();

            mostrarMensaje(
                '¡Calificación guardada exitosamente!',
                'exito'
            );

            const actualizado = await cargarCalificaciones(false);

            if (!actualizado) {
                mostrarMensaje(
                    'La calificación se guardó, pero no se pudo actualizar el listado.',
                    'info'
                );
            }

        } catch (error) {
            console.error('Error al registrar:', error);

            mostrarMensaje(
                error.message || 'No se pudo guardar la calificación.',
                'error'
            );

        } finally {
            setEstadoGuardando(false);
        }
    });

    // =====================================================
    // CONSULTAR CALIFICACIONES
    // =====================================================

    async function cargarCalificaciones(mostrarErrores = true) {

        if (spinner) spinner.classList.remove('hidden');
        if (sinRegistros) sinRegistros.classList.add('hidden');

        try {
            const respuesta = await fetch('/api/calificaciones', {
                method: 'GET',
                headers: {
                    'Accept': 'application/json'
                }
            });

            if (!respuesta.ok) {
                throw new Error(await leerError(respuesta));
            }

            const calificaciones = await respuesta.json();

            if (!Array.isArray(calificaciones)) {
                throw new Error('Formato de respuesta incorrecto.');
            }

            renderizarTabla(calificaciones);

            if (sinRegistros) {
                sinRegistros.classList.toggle(
                    'hidden',
                    calificaciones.length > 0
                );
            }

            return true;

        } catch (error) {
            console.error('Error al consultar:', error);

            if (mostrarErrores) {
                mostrarMensaje(
                    'No se pudieron cargar las calificaciones.',
                    'error'
                );
            }

            return false;

        } finally {
            if (spinner) spinner.classList.add('hidden');

            if (window.lucide) {
                window.lucide.createIcons();
            }
        }
    }

    // =====================================================
    // CARGAR CURSOS
    // =====================================================

    async function cargarCursos() {

        selectCurso.disabled = true;
        selectCurso.innerHTML =
            '<option value="">Cargando cursos...</option>';

        try {
            const respuesta = await fetch('/api/cursos', {
                headers: {
                    'Accept': 'application/json'
                }
            });

            if (!respuesta.ok) {
                throw new Error('API de cursos no disponible.');
            }

            const cursos = await respuesta.json();

            if (!Array.isArray(cursos)) {
                throw new Error('Formato de cursos incorrecto.');
            }

            selectCurso.innerHTML =
                '<option value="">-- Selecciona un curso --</option>';

            cursos.forEach(curso => {

                const id = curso.idCurso ?? curso.id;
                const nombre = curso.nombreCurso ?? curso.nombre;

                if (id == null || !nombre) return;

                const opcion = document.createElement('option');

                opcion.value = String(id);
                opcion.textContent = String(nombre);

                selectCurso.appendChild(opcion);
            });

            if (selectCurso.options.length > 1) {
                selectCurso.disabled = false;
            } else {
                selectCurso.innerHTML =
                    '<option value="">No hay cursos disponibles</option>';
            }

        } catch (error) {
            console.warn('No se pudieron cargar los cursos:', error);

            selectCurso.innerHTML =
                '<option value="">Cursos no disponibles todavía</option>';
        }
    }

    // =====================================================
    // MOSTRAR CALIFICACIONES EN LA TABLA
    // =====================================================

    function renderizarTabla(calificaciones) {

        tablaBody.innerHTML = '';

        calificaciones.forEach(item => {

            const fila = document.createElement('tr');

            fila.className =
                'hover:bg-emerald-50/50 transition border-b border-gray-100';

            const valores = [
                item.nombreCurso ?? item.idCurso ?? '',
                item.nombreActividad ?? '',
                item.notaObtenida ?? '',
                item.notaMaxima ?? '',
                item.fecha ?? 'N/A'
            ];

            valores.forEach((valor, indice) => {

                const celda = document.createElement('td');

                celda.className = 'py-3 px-4';

                if (indice >= 2) {
                    celda.classList.add('text-center');
                }

                if (indice === 0 || indice === 2) {
                    celda.classList.add(
                        'font-semibold',
                        'text-[#1b4332]'
                    );
                } else {
                    celda.classList.add('text-gray-600');
                }

                celda.textContent = String(valor);
                fila.appendChild(celda);
            });

            tablaBody.appendChild(fila);
        });
    }

    // =====================================================
    // ESTADO DEL BOTON
    // =====================================================

    function setEstadoGuardando(guardando) {

        btnGuardar.disabled = guardando;

        const texto = btnGuardar.querySelector('span');

        if (texto) {
            texto.textContent = guardando
                ? 'Guardando...'
                : 'Guardar calificación';
        }
    }

    // =====================================================
    // MENSAJES
    // =====================================================

    function mostrarMensaje(texto, tipo) {

        mensaje.textContent = texto;

        mensaje.className =
            'p-4 rounded-lg text-sm font-medium mb-4';

        if (tipo === 'exito') {
            mensaje.classList.add(
                'bg-[#d8f3dc]',
                'text-[#1b4332]',
                'border',
                'border-[#74c69d]'
            );

        } else if (tipo === 'error') {
            mensaje.classList.add(
                'bg-red-50',
                'text-red-700',
                'border',
                'border-red-200'
            );

        } else {
            mensaje.classList.add(
                'bg-blue-50',
                'text-blue-700',
                'border',
                'border-blue-200'
            );
        }

        mensaje.classList.remove('hidden');
    }

    function ocultarMensaje() {
        mensaje.classList.add('hidden');
        mensaje.textContent = '';
    }

    // =====================================================
    // LEER ERRORES DEL SERVIDOR
    // =====================================================

    async function leerError(respuesta) {

        try {
            const datos = await respuesta.json();

            return datos.mensaje ||
                `Error del servidor (${respuesta.status}).`;

        } catch (error) {
            return `Error del servidor (${respuesta.status}).`;
        }
    }
});
