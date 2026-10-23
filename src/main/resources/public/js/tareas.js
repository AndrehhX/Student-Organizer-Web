const formularioTarea = document.querySelector('#form-tarea');
const listaTareas = document.querySelector('#lista-tareas');
const mensajeTareas = document.querySelector('#mensaje-tareas');
const selectorCursoTarea = document.querySelector('#curso-tarea');

function mostrarMensajeTareas(texto, error = false) {
    mensajeTareas.textContent = texto;
    mensajeTareas.className = error
        ? 'mb-3 text-sm text-red-700'
        : 'mb-3 text-sm text-brand-dark';
}

function mostrarTareas(tareas) {
    listaTareas.replaceChildren();

    if (tareas.length === 0) {
        const vacio = document.createElement('p');
        vacio.className = 'text-sm text-gray-500';
        vacio.textContent = 'Todavía no hay tareas registradas.';
        listaTareas.append(vacio);
        return;
    }

    tareas.forEach((tarea) => {
        const tarjeta = document.createElement('article');
        tarjeta.className = 'flex flex-col gap-2 rounded-lg bg-white p-4 shadow-sm md:flex-row md:items-center md:justify-between';

        const informacion = document.createElement('div');
        const nombre = document.createElement('h3');
        nombre.className = 'font-semibold text-brand-dark';
        nombre.textContent = tarea.nombre;

        const detalles = document.createElement('p');
        detalles.className = 'text-sm text-gray-500';
        detalles.textContent = `${tarea.fechaEntrega} · Prioridad ${tarea.prioridad}`;
        informacion.append(nombre, detalles);

        const accion = document.createElement('button');
        accion.type = 'button';
        accion.textContent = tarea.completada ? 'Completada' : 'Marcar completada';
        accion.disabled = tarea.completada;
        accion.className = tarea.completada
            ? 'rounded-lg bg-gray-400 px-3 py-2 text-sm font-semibold text-white'
            : 'rounded-lg bg-brand-medium px-3 py-2 text-sm font-semibold text-white hover:bg-brand-dark';
        accion.addEventListener('click', () =>
            completarTarea(tarea.id || tarea.indice));

        tarjeta.append(informacion, accion);
        listaTareas.append(tarjeta);
    });
}

async function cargarTareas() {
    try {
        const respuesta = await fetch('/api/tareas');
        if (!respuesta.ok) {
            throw new Error('No se pudieron cargar las tareas.');
        }

        mostrarTareas(await respuesta.json());
    } catch (error) {
        mostrarMensajeTareas(error.message, true);
    }
}

async function completarTarea(indice) {
    try {
        const respuesta = await fetch('/api/tareas/completar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ indice })
        });

        const resultado = await respuesta.json();
        if (!respuesta.ok) {
            throw new Error(resultado.mensaje);
        }

        mostrarMensajeTareas(resultado.mensaje);
        await cargarTareas();
    } catch (error) {
        mostrarMensajeTareas(error.message, true);
    }
}

async function cargarCursosParaTareas() {
    if (!selectorCursoTarea) return;

    try {
        const respuesta = await fetch('/api/cursos', {
            headers: { 'Accept': 'application/json' }
        });

        if (!respuesta.ok) {
            return;
        }

        const cursos = await respuesta.json();
        selectorCursoTarea.innerHTML =
            '<option value="">Selecciona un curso</option>';

        cursos.forEach((curso) => {
            const id = curso.idCurso ?? curso.id;
            const nombre = curso.nombreCurso ?? curso.nombre;
            if (id == null || !nombre) return;

            const opcion = document.createElement('option');
            opcion.value = String(id);
            opcion.textContent = String(nombre);
            selectorCursoTarea.append(opcion);
        });
    } catch (error) {
        console.warn('No se pudieron cargar los cursos para tareas:', error);
    }
}

formularioTarea.addEventListener('submit', async (evento) => {
    evento.preventDefault();
    const datos = new FormData(formularioTarea);

    try {
        const respuesta = await fetch('/api/tareas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams(datos)
        });
        const resultado = await respuesta.json();

        if (!respuesta.ok) {
            throw new Error(resultado.mensaje);
        }

        formularioTarea.reset();
        mostrarMensajeTareas(resultado.mensaje);
        await cargarTareas();
    } catch (error) {
        mostrarMensajeTareas(error.message, true);
    }
});

cargarTareas();
cargarCursosParaTareas();
