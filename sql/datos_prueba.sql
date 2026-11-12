-- Cuentas ficticias para presentar y probar la aplicación.
-- Solo deben cargarse en una base de desarrollo o pruebas.
-- La contraseña se guarda como SHA-256, igual que en Java.

USE student_organizer;

INSERT IGNORE INTO usuarios (
    nombre_usuario, correo, contrasena_hash, carne
)
VALUES
    (
        'ana.mendez',
        'ana.mendez@example.test',
        SHA2('Ana-Organiza-2026!', 256),
        'TEST-001'
    ),
    (
        'carlos.ruiz',
        'carlos.ruiz@example.test',
        SHA2('Carlos-Tareas-2026!', 256),
        'TEST-002'
    ),
    (
        'sofia.lopez',
        'sofia.lopez@example.test',
        SHA2('Sofia-Cursos-2026!', 256),
        'TEST-003'
    );

SELECT nombre_usuario, correo, carne
FROM usuarios
WHERE correo IN (
    'ana.mendez@example.test',
    'carlos.ruiz@example.test',
    'sofia.lopez@example.test'
)
ORDER BY nombre_usuario;
