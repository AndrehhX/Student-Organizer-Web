USE student_organizer;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(100) NOT NULL UNIQUE,
    contrasena_hash CHAR(64) NOT NULL,
    carne VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS tareas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    fecha_entrega DATE NOT NULL,
    prioridad VARCHAR(30) NOT NULL,
    completada BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_tareas_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);