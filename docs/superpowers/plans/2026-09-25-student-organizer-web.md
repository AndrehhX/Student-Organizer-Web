# Student Organizer Web Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Crear una base web sencilla y ordenada donde la interfaz use HTML, CSS y JavaScript, mientras la lógica del Login continúe en Java.

**Architecture:** El navegador mostrará los archivos estáticos de `src/main/resources/public`. Un servidor Java pequeño recibirá el formulario de login y llamará a `ControladorLogin`. Los datos seguirán en `data/usuarios.csv`, y cada requisito tendrá una carpeta propia para reducir choques entre compañeros.

**Tech Stack:** Java 17, `HttpServer` incluido en el JDK, HTML, CSS y JavaScript sin frameworks externos.

**Spec:** Requisitos del equipo para RF01, RF03, RF07 y RF12, con prioridad en dejar una base común y el Login funcional.

## Global Constraints

- Mantener la lógica de negocio, seguridad y persistencia en Java.
- Usar HTML/CSS/JavaScript únicamente para la interfaz y la interacción del navegador.
- No agregar frameworks externos para que el proyecto siga siendo fácil de ejecutar.
- Cada integrante trabaja dentro de su módulo y registra sus horas reales.
- Ningún cambio entra directamente a `main`; se integra mediante ramas y Pull Requests.
- Los commits se harán por avances reales; no se alterarán fechas ni se simulará trabajo.

## Review Focus

- Campos vacíos: el Login debe rechazar la petición y mostrar un mensaje claro.
- Usuario inexistente o contraseña incorrecta: no debe iniciar sesión.
- Usuario válido: debe devolver acceso correcto usando el CSV.
- Archivo CSV inexistente: debe crearse automáticamente.
- Módulos de compañeros: deben tener un espacio propio sin romper el Login.

### Task 1: Base del repositorio y guía de trabajo

**Files:**
- Create: `README.md`
- Create: `.gitignore`
- Create: `docs/horas-contribuciones.md`
- Create: `docs/tareas/*.txt`
- Create: `docs/calendario.md`
- Create: `src/main/java/modulos/*/README.txt`

- [ ] Crear la estructura base del proyecto.
- [ ] Documentar responsabilidades, ramas y formato de horas.
- [ ] Commit: `chore: preparar estructura inicial del proyecto`.

### Task 2: Lógica Java del Login

**Files:**
- Create: `src/main/java/modelo/Usuario.java`
- Create: `src/main/java/util/Seguridad.java`
- Create: `src/main/java/persistencia/PersistenciaDatos.java`
- Create: `src/main/java/controlador/ControladorLogin.java`
- Create: `data/usuarios.csv`

- [ ] Crear el modelo Usuario con nombre, contraseña codificada y carné.
- [ ] Codificar la contraseña con SHA-256.
- [ ] Leer y guardar usuarios en el CSV.
- [ ] Validar campos, comparar credenciales y preparar una cuenta demo.
- [ ] Commit: `feat: agregar logica java del login`.

### Task 3: Interfaz web y servidor

**Files:**
- Create: `src/main/resources/public/index.html`
- Create: `src/main/resources/public/css/estilos.css`
- Create: `src/main/resources/public/js/app.js`
- Create: `src/main/java/servidor/ServidorWeb.java`
- Create: `src/main/java/Main.java`

- [ ] Crear una pantalla sencilla de Login.
- [ ] Enviar el formulario al endpoint Java.
- [ ] Servir HTML, CSS y JavaScript desde Java.
- [ ] Mostrar mensajes de éxito y error.
- [ ] Commit: `feat: conectar interfaz web con java`.

### Task 4: Pruebas y documentación del RF01

**Files:**
- Create: `src/test/java/PruebasLogin.java`
- Create: `docs/RF01_Login.md`
- Create: `docs/pruebas/login.md`

- [ ] Probar campos vacíos, datos incorrectos y cuenta válida.
- [ ] Probar creación automática del CSV.
- [ ] Documentar el flujo y la forma de ejecutar el Login.
- [ ] Commit: `test: probar y documentar el login web`.

### Task 5: Entrega preparada para el equipo

**Files:**
- Modify: `README.md`
- Modify: `docs/horas-contribuciones.md`
- Create: `docs/guia-integracion.md`

- [ ] Explicar cómo crear ramas y Pull Requests.
- [ ] Dejar instrucciones concretas para los tres módulos restantes.
- [ ] Verificar que el estado final se pueda compilar y ejecutar.
- [ ] Commit: `docs: dejar guia de integracion para el equipo`.
