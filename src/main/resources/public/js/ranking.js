document.addEventListener('DOMContentLoaded', async () => {
    const lista = document.getElementById('ranking-notas-lista');
    const mensaje = document.getElementById('ranking-notas-mensaje');
    if (!lista || !mensaje) return;

    try {
        const respuesta = await fetch('/api/promedios', {
            headers: { 'Accept': 'application/json' }
        });
        if (!respuesta.ok) throw new Error('No se pudo cargar el ranking.');

        const datos = await respuesta.json();
        const cursos = Array.isArray(datos.cursos) ? datos.cursos : [];
        cursos.sort((a, b) => Number(b.promedio) - Number(a.promedio));
        const mejores = cursos.slice(0, 5);

        lista.replaceChildren();
        if (mejores.length === 0) {
            mensaje.textContent = 'Aún no hay calificaciones para mostrar.';
            return;
        }

        mensaje.textContent = 'Tus cinco cursos con mejor promedio.';
        mejores.forEach((curso, indice) => {
            const promedio = Number(curso.promedio) || 0;
            const tarjeta = document.createElement('article');
            tarjeta.className = 'rounded-xl bg-brand-bright/30 p-4 border border-brand-medium/30';

            const encabezado = document.createElement('div');
            encabezado.className = 'flex items-center justify-between gap-3';

            const nombre = document.createElement('h3');
            nombre.className = 'font-bold text-brand-dark';
            nombre.textContent = `${indice + 1}. ${curso.nombreCurso}`;

            const nota = document.createElement('span');
            nota.className = 'font-bold text-brand-dark';
            nota.textContent = `${promedio.toFixed(2)}/100`;
            encabezado.append(nombre, nota);

            const barra = document.createElement('div');
            barra.className = 'mt-3 h-2 rounded-full bg-white overflow-hidden';
            const progreso = document.createElement('div');
            progreso.className = 'h-full rounded-full bg-brand-medium';
            progreso.style.width = `${Math.max(0, Math.min(100, promedio))}%`;
            barra.append(progreso);

            const detalle = document.createElement('p');
            detalle.className = 'mt-2 text-xs text-gray-500';
            detalle.textContent = `${curso.cantidadCalificaciones} calificación(es)`;

            tarjeta.append(encabezado, barra, detalle);
            lista.append(tarjeta);
        });
    } catch (error) {
        console.error(error);
        mensaje.textContent = 'No se pudo cargar el ranking.';
    }
});
