

// Muestra el promedio general en la tarjeta del dashboard.
// Los calculos se hacen en Java; aqui solo se muestran.


document.addEventListener('DOMContentLoaded', () => {

    const valor = document.getElementById('promedio-general-valor');
    const detalle = document.getElementById('promedio-general-detalle');

    if (!valor || !detalle) {
        console.error('Faltan elementos de la tarjeta de promedio.');
        return;
    }

    cargarPromedio();

    // Al volver a Tareas (donde esta la tarjeta) se actualiza
    // el promedio, por si se registraron notas nuevas.
    const enlaceTareas = document.querySelector('[data-seccion="tareas"]');

    if (enlaceTareas) {
        enlaceTareas.addEventListener('click', cargarPromedio);
    }


    // CONSULTAR PROMEDIO


    async function cargarPromedio() {

        try {
            const respuesta = await fetch('/api/promedios', {
                headers: { 'Accept': 'application/json' }
            });

            if (respuesta.status === 401) {
                mostrar('--', 'Inicia sesión para ver tu promedio.');
                return;
            }

            if (!respuesta.ok) {
                throw new Error(`Error del servidor (${respuesta.status}).`);
            }

            const datos = await respuesta.json();

            if (datos.promedioGeneral === null ||
                datos.promedioGeneral === undefined) {

                mostrar('--', 'Aún no tienes calificaciones registradas.');
                return;
            }

            const cantidadCursos = Array.isArray(datos.cursos)
                ? datos.cursos.length
                : 0;

            mostrar(
                Number(datos.promedioGeneral).toFixed(2),
                cantidadCursos === 1
                    ? 'Basado en 1 curso'
                    : `Basado en ${cantidadCursos} cursos`
            );

        } catch (error) {
            console.error('Error al consultar el promedio:', error);
            mostrar('--', 'No se pudo cargar el promedio.');
        }
    }

 
    // MOSTRAR RESULTADO (textContent, sin innerHTML)
    

    function mostrar(textoValor, textoDetalle) {
        valor.textContent = textoValor;
        detalle.textContent = textoDetalle;
    }
});