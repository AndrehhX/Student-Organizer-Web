USE student_organizer;

START TRANSACTION;

INSERT INTO usuarios (nombre_usuario, contrasena_hash, carne)
VALUES (
    CONCAT('prueba_', REPLACE(UUID(), '-', '')),
    SHA2('Prueba123', 256),
    'PRUEBA'
);

SET @usuario_id = LAST_INSERT_ID();

INSERT INTO cursos (usuario_id, nombre)
VALUES (@usuario_id, 'Curso de prueba');

SET @curso_id = LAST_INSERT_ID();

INSERT INTO tareas (
    usuario_id, curso_id, nombre, fecha_entrega
)
VALUES (
    @usuario_id,
    @curso_id,
    'Probar mi base de datos',
    '2026-10-30'
);

COMMIT;

SELECT
    u.nombre_usuario,
    c.nombre AS curso,
    t.nombre,
    t.fecha_entrega,
    t.completada
FROM usuarios u
JOIN tareas t ON t.usuario_id = u.id
JOIN cursos c ON c.id = t.curso_id AND c.usuario_id = u.id
WHERE u.id = @usuario_id;