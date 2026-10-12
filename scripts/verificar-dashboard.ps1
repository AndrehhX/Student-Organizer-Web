$ErrorActionPreference = "Stop"

$raiz = Split-Path -Parent $PSScriptRoot
$dashboard = Get-Content -Raw (Join-Path $raiz "src\main\resources\public\dashboard.html")
$javascript = Get-Content -Raw (Join-Path $raiz "src\main\resources\public\js\tareas.js")
$dashboardJavascript = Get-Content -Raw (Join-Path $raiz "src\main\resources\public\js\dashboard.js")

if ($dashboard -notmatch 'id="form-tarea"') {
    throw "El dashboard no tiene el formulario de tareas."
}

if ($dashboard -notmatch 'id="lista-tareas"') {
    throw "El dashboard no tiene la lista de tareas."
}

if ($dashboard -notmatch 'src="/js/tareas.js"') {
    throw "El dashboard no carga el JavaScript de tareas."
}

if ($javascript -notmatch "'/api/tareas'") {
    throw "El JavaScript no consulta el endpoint de tareas."
}

if ($javascript -notmatch "'/api/tareas/completar'") {
    throw "El JavaScript no conecta la accion de completar tareas."
}

if ($javascript -match 'classList\.add\(tarea\.completada') {
    throw "El JavaScript intenta agregar varias clases como un solo nombre."
}

if ($dashboard -notmatch 'id="boton-cerrar-sesion"') {
    throw "El dashboard no tiene un boton funcional para cerrar sesion."
}

if ($dashboard -notmatch 'id="panel-notificaciones"') {
    throw "El dashboard no tiene un panel de notificaciones."
}

if ($dashboard -notmatch 'id="panel-notas"' -or $dashboard -notmatch 'id="panel-cursos"') {
    throw "El dashboard no tiene secciones definidas para Notas y Cursos."
}

if ($dashboard -notmatch 'src="/js/dashboard.js"') {
    throw "El dashboard no carga el JavaScript de sus controles."
}

if ($dashboardJavascript -notmatch "'/api/logout'") {
    throw "El dashboard no conecta el cierre de sesion."
}

if ($dashboardJavascript -notmatch 'data-seccion') {
    throw "El dashboard no conecta la navegacion entre secciones."
}

if ($dashboardJavascript -notmatch 'cerrarPanelNotificaciones') {
    throw "La navegacion no cierra el panel de notificaciones."
}

Write-Output "VerificarDashboard: la interfaz esta conectada con las tareas y sus controles."
