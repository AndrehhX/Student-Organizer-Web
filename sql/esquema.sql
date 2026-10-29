-- =====================================================
-- Esquema COMPLETO de student_organizer (pendiente de promeido)
--   002_cursos_y_calificaciones.sql
--   003_retirar_prioridad.sql


CREATE DATABASE IF NOT EXISTS student_organizer;
USE student_organizer;

-- =====================================================
-- 1. Usuarios
-- =====================================================

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(100) NOT NULL UNIQUE,
    contrasena_hash CHAR(64) NOT NULL,
    carne VARCHAR(30) NOT NULL
);

-- =====================================================
-- 2. Cursos de cada usuario
-- =====================================================

CREATE TABLE IF NOT EXISTS cursos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nombre VARCHAR(150) NOT NULL,

    CONSTRAINT fk_cursos_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT uq_cursos_usuario_nombre
        UNIQUE (usuario_id, nombre),

    -- Permite que tareas y calificaciones usen la pareja
    -- (curso, usuario) y así un curso nunca se mezcla
    -- con el de otro usuario.
    CONSTRAINT uq_cursos_id_usuario
        UNIQUE (id, usuario_id)
);

-- =====================================================
-- 3. Tareas
--
-- curso_id acepta NULL para conservar tareas antiguas
-- que se crearon antes de existir los cursos.
-- El código Java exige curso al guardar una tarea nueva.
-- =====================================================

CREATE TABLE IF NOT EXISTS tareas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    curso_id BIGINT NULL,
    nombre VARCHAR(150) NOT NULL,
    fecha_entrega DATE NOT NULL,
    completada BOOLEAN NOT NULL DEFAULT FALSE,
    prioridad VARCHAR(30) NOT NULL DEFAULT "media",
    AFTER fecha_entrega;

    INDEX idx_tareas_curso_usuario (curso_id, usuario_id),

    CONSTRAINT fk_tareas_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_tareas_curso_usuario
        FOREIGN KEY (curso_id, usuario_id)
        REFERENCES cursos(id, usuario_id)
);

-- =====================================================
-- 4. Calificaciones
-- =====================================================

CREATE TABLE IF NOT EXISTS calificaciones (
    id CHAR(36) PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    nombre_actividad VARCHAR(150) NOT NULL,
    nota_obtenida DECIMAL(10, 2) NOT NULL,
    nota_maxima DECIMAL(10, 2) NOT NULL,
    fecha DATE NOT NULL,

    INDEX idx_calificaciones_usuario_fecha (usuario_id, fecha),
    INDEX idx_calificaciones_curso_usuario (curso_id, usuario_id),

    CONSTRAINT fk_calificaciones_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_calificaciones_curso_usuario
        FOREIGN KEY (curso_id, usuario_id)
        REFERENCES cursos(id, usuario_id),

    CONSTRAINT chk_calificaciones_notas
        CHECK (
            nota_maxima > 0
            AND nota_obtenida >= 0
            AND nota_obtenida <= nota_maxima
        )
);