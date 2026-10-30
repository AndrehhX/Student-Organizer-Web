$ErrorActionPreference = "Stop"

$raiz = Split-Path -Parent $PSScriptRoot
$compilador = (Get-Command javac -CommandType Application).Source
$javaProyecto = Join-Path (Split-Path $compilador) "java.exe"

if (!(Test-Path $javaProyecto)) {
    throw "No se encontro java.exe junto a javac."
}

$conector = @(
    Get-ChildItem `
        -Path (Join-Path $raiz "lib") `
        -Filter "mysql-connector-j*.jar" `
        -ErrorAction SilentlyContinue
)

if ($conector.Count -eq 0) {
    throw "No se encontro el conector MySQL en lib."
}

& (Join-Path $PSScriptRoot "compilar.ps1")

if ([string]::IsNullOrWhiteSpace($env:DB_PASSWORD)) {
    $clave = Read-Host `
        "Contrasena MySQL de organizer_app" `
        -AsSecureString

    $env:DB_PASSWORD = (
        [System.Net.NetworkCredential]::new("", $clave)
    ).Password

    $clave.Dispose()
}

Push-Location $raiz

try {
    & $javaProyecto `
        --add-modules jdk.httpserver `
        -cp "out;lib/*" `
        Main

    if ($LASTEXITCODE -ne 0) {
        throw "Java termino con un error. Revisa el mensaje anterior."
    }
}
finally {
    Pop-Location
}