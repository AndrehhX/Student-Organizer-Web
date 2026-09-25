$ErrorActionPreference = "Stop"

$raiz = Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot "compilar.ps1")
java --add-modules jdk.httpserver -cp (Join-Path $raiz "out") Main
