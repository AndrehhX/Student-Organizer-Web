$ErrorActionPreference = "Stop"

$raiz = Split-Path -Parent $PSScriptRoot
$dashboard = Get-Content -Raw (Join-Path $raiz "src\main\resources\public\dashboard.html")
$javascript = Get-Content -Raw (Join-Path $raiz "src\main\resources\public\js\tareas.js")

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
    throw "El JavaScript no conecta la acción de completar tareas."
}

Write-Output "VerificarDashboard: la interfaz está conectada con las tareas."
