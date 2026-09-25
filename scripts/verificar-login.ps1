$ErrorActionPreference = "Stop"

$raiz = Split-Path -Parent $PSScriptRoot
$proceso = Start-Process -FilePath "java.exe" `
    -ArgumentList "--add-modules", "jdk.httpserver", "-cp", "out", "Main" `
    -WorkingDirectory $raiz -WindowStyle Hidden -PassThru

try {
    Start-Sleep -Milliseconds 800
    $pagina = Invoke-WebRequest -UseBasicParsing "http://localhost:8080/"
    $correcto = Invoke-RestMethod -Method Post `
        -Uri "http://localhost:8080/api/login" `
        -ContentType "application/x-www-form-urlencoded" `
        -Body "usuario=demo&contrasena=demo123"
    $incorrecto = Invoke-RestMethod -Method Post `
        -Uri "http://localhost:8080/api/login" `
        -ContentType "application/x-www-form-urlencoded" `
        -Body "usuario=demo&contrasena=incorrecta"

    Write-Output "HTTP=$($pagina.StatusCode)"
    Write-Output "CORRECTO=$($correcto.exitoso) / $($correcto.mensaje)"
    Write-Output "INCORRECTO=$($incorrecto.exitoso) / $($incorrecto.mensaje)"
} finally {
    Stop-Process -Id $proceso.Id -Force -ErrorAction SilentlyContinue
}
