USE student_organizer;

START TRANSACTION;

INSERT INTO usuarios (nombre_usuario, contrasena_hash, carne)
VALUES (
    CONCAT('prueba_', REPLACE(UUID(), '-', '')),
    SHA2('Prueba123', 256),
    'PRUEBA'
);

SET @usuario_id = LAST_INSERT_ID();

INSERT INTO tareas (
    usuario_id, nombre, fecha_entrega, prioridad
)
VALUES (
    @usuario_id,
    'Probar mi base de datos',
    '2026-10-30',
    'alta'
);

COMMIT;

SELECT
    u.nombre_usuario,
    t.nombre,
    t.fecha_entrega,
    t.prioridad,
    t.completada
FROM usuarios u
JOIN tareas t ON t.usuario_id = u.id
WHERE u.id = @usuario_id;