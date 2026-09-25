$ErrorActionPreference = "Stop"

$raiz = Split-Path -Parent $PSScriptRoot
$salida = Join-Path $raiz "out"
$fuentes = Get-ChildItem -Path (Join-Path $raiz "src\main\java") -Filter *.java -Recurse

New-Item -ItemType Directory -Force -Path $salida | Out-Null
javac --add-modules jdk.httpserver -encoding UTF-8 -d $salida $fuentes.FullName
Write-Output "Compilación terminada correctamente."
