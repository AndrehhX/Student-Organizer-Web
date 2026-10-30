$ErrorActionPreference = "Stop"

$raiz = Split-Path -Parent $PSScriptRoot
$salida = Join-Path $raiz "out"
$compilador = (Get-Command javac -CommandType Application).Source

$fuentes = @(
    Get-ChildItem `
        -Path (Join-Path $raiz "src\main\java") `
        -Filter *.java `
        -Recurse
)

if ($fuentes.Count -eq 0) {
    throw "No se encontraron archivos Java."
}

New-Item -ItemType Directory -Force -Path $salida | Out-Null

# Retirar clases generadas anteriormente.
Get-ChildItem -Path $salida -Filter *.class -Recurse |
    Remove-Item -Force

& $compilador `
    --add-modules jdk.httpserver `
    -encoding UTF-8 `
    -d $salida `
    $fuentes.FullName

if ($LASTEXITCODE -ne 0) {
    throw "La compilacion fallo. No se iniciara la aplicacion."
}

Write-Host "Compilacion correcta."