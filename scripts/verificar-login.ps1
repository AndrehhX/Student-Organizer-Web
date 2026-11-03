$ErrorActionPreference = "Stop"

$raiz = Split-Path -Parent $PSScriptRoot
$java = Get-Command java.exe -CommandType Application -ErrorAction Stop
$url = "http://localhost:8080"
$dbPasswordAnterior = $env:DB_PASSWORD
$cliente = [System.Net.Sockets.TcpClient]::new()

try {
    $cliente.Connect("localhost", 8080)
    throw "El puerto 8080 ya está ocupado. Cierra el servidor actual y vuelve a ejecutar esta prueba."
} catch [System.Net.Sockets.SocketException] {
    # El puerto está libre.
} finally {
    $cliente.Dispose()
}

$usuario = Read-Host "Usuario registrado para probar el login"
$claveSegura = Read-Host "Contraseña de ese usuario" -AsSecureString
$contrasena = [System.Net.NetworkCredential]::new("", $claveSegura).Password
$claveSegura.Dispose()

if ([string]::IsNullOrWhiteSpace($usuario) -or [string]::IsNullOrWhiteSpace($contrasena)) {
    throw "Debes escribir un usuario y una contraseña."
}

& (Join-Path $PSScriptRoot "compilar.ps1")

if ([string]::IsNullOrWhiteSpace($env:DB_PASSWORD)) {
    $claveSegura = Read-Host "Contraseña MySQL de organizer_app" -AsSecureString
    $env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $claveSegura).Password
    $claveSegura.Dispose()
}

$salidaLog = Join-Path ([System.IO.Path]::GetTempPath()) "student-organizer-login-$PID.out.log"
$errorLog = Join-Path ([System.IO.Path]::GetTempPath()) "student-organizer-login-$PID.err.log"
$argumentos = "--add-modules jdk.httpserver -cp `"out;lib/*`" Main"
$proceso = $null

try {
    $proceso = Start-Process -FilePath $java.Source `
        -ArgumentList $argumentos `
        -WorkingDirectory $raiz `
        -WindowStyle Hidden `
        -RedirectStandardOutput $salidaLog `
        -RedirectStandardError $errorLog `
        -PassThru

    $listo = $false
    for ($intento = 0; $intento -lt 40; $intento++) {
        if ($proceso.HasExited) {
            break
        }

        try {
            $pagina = Invoke-WebRequest -UseBasicParsing "$url/" -TimeoutSec 2
            if ($pagina.StatusCode -eq 200) {
                $listo = $true
                break
            }
        } catch {
            Start-Sleep -Milliseconds 250
        }
    }

    if (-not $listo) {
        if (Test-Path $salidaLog) {
            Get-Content $salidaLog
        }
        if (Test-Path $errorLog) {
            Get-Content $errorLog
        }
        throw "El servidor no inició. Revisa MySQL, DB_PASSWORD y Connector/J."
    }

    $cuerpoCorrecto = "usuario=$([uri]::EscapeDataString($usuario))&contrasena=$([uri]::EscapeDataString($contrasena))"
    $correcto = Invoke-RestMethod -Method Post `
        -Uri "$url/api/login" `
        -ContentType "application/x-www-form-urlencoded" `
        -Body $cuerpoCorrecto

    $cuerpoIncorrecto = "usuario=$([uri]::EscapeDataString($usuario))&contrasena=$([uri]::EscapeDataString(($contrasena + '-incorrecta')))"
    $incorrecto = Invoke-RestMethod -Method Post `
        -Uri "$url/api/login" `
        -ContentType "application/x-www-form-urlencoded" `
        -Body $cuerpoIncorrecto

    if (-not $correcto.exitoso -or $incorrecto.exitoso) {
        throw "El resultado no coincide: el login correcto debe pasar y la clave incorrecta debe rechazarse."
    }

    Write-Output "Página de login: HTTP $($pagina.StatusCode)"
    Write-Output "Credenciales válidas: aceptadas"
    Write-Output "Contraseña incorrecta: rechazada"
} finally {
    if ($null -ne $proceso -and -not $proceso.HasExited) {
        Stop-Process -Id $proceso.Id -Force -ErrorAction SilentlyContinue
    }
    Remove-Item -LiteralPath $salidaLog, $errorLog -Force -ErrorAction SilentlyContinue
    $contrasena = $null
    $env:DB_PASSWORD = $dbPasswordAnterior
}
