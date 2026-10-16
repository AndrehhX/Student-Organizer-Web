
document.addEventListener('DOMContentLoaded', () => {

    const listaCursos = document.getElementById('lista-cursos');
    if (!listaCursos) return;

    async function cargarCursos() {
        listaCursos.textContent = 'Cargando cursos...';

        try {
            const respuesta = await fetch('/api/cursos');

            if (!respuesta.ok) {
                throw new Error('No se pudieron consultar los cursos.');
            }

            const cursos = await respuesta.json();

            if (!Array.isArray(cursos)) {
                throw new Error('Formato de cursos incorrecto.');
            }

            listaCursos.replaceChildren();

            if (cursos.length === 0) {
                const mensaje = document.createElement('p');
                mensaje.className =
                    'text-sm text-gray-500 text-center sm:col-span-2';
                mensaje.textContent =
                    'Todavía no tienes cursos registrados. ' +
                    'Registra una calificación para agregar tu primer curso.';
                listaCursos.appendChild(mensaje);
                return;
            }

            cursos.forEach(curso => {
                const tarjeta = document.createElement('div');
                tarjeta.className =
                    'bg-white rounded-xl p-4 shadow-sm ' +
                    'border border-brand-medium/30 flex items-center gap-3';

                const icono = document.createElement('i');
                icono.setAttribute('data-lucide', 'book-open');
                icono.className =
                    'w-6 h-6 text-brand-medium shrink-0';

                const nombre = document.createElement('span');
                nombre.className =
                    'font-semibold text-brand-dark';
                nombre.textContent = curso.nombreCurso;

                tarjeta.appendChild(icono);
                tarjeta.appendChild(nombre);
                listaCursos.appendChild(tarjeta);
            });

            if (window.lucide) {
                window.lucide.createIcons();
            }

        } catch (error) {
            console.error('Error al cargar cursos:', error);
            listaCursos.textContent =
                'No se pudieron cargar los cursos.';
        }
    }

    // Actualizar cuando se abre el apartado Cursos
    document.querySelectorAll('[data-seccion="cursos"]')
        .forEach(enlace => {
            enlace.addEventListener('click', cargarCursos);
        });

    cargarCursos();
});
