# Guía rápida para integrar el trabajo

## Regla principal

Cada persona trabaja en su rama y abre un Pull Request. Nadie trabaja directamente sobre `main`.

## Ramas sugeridas

- `feature/rf01-login-andreh`
- `feature/rf03-calificaciones-fabricio`
- `feature/rf12-promedio-manuel`
- `feature/rf07-tareas-luis`

## Cómo agregar un requisito

1. Trabaja dentro de tu carpeta de módulo.
2. Crea archivos propios para tu pantalla, modelo y controlador.
3. No cambies `Main`, `ServidorWeb`, `ControladorLogin` o `PersistenciaDatos` sin avisar.
4. Si necesitas un método compartido, escríbelo en el grupo antes de implementarlo.
5. Prueba tu módulo y escribe qué probaste en `docs/pruebas`.
6. Actualiza tus horas en `docs/horas-contribuciones.md`.
7. Abre el Pull Request explicando qué archivos cambiaste.

## Para probar el Login actual

Desde la carpeta del proyecto:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\compilar.ps1
powershell -ExecutionPolicy Bypass -File scripts\ejecutar.ps1
```

Después se abre `http://localhost:8080` en el navegador.

