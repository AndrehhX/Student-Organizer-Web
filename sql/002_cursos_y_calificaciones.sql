-- ======================================
-- 1. Cursos de cada Usuario
-- ======================================
USE student_organizer;

-- =====================================================
-- 1. Cursos de los usuarios
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

    CONSTRAINT uq_cursos_id_usuario
        UNIQUE (id, usuario_id)
);

-- =====================================================
-- 2. Relacionar las tareas con los cursos
-- =====================================================

ALTER TABLE tareas
    ADD COLUMN curso_id BIGINT NULL AFTER usuario_id,

    ADD INDEX idx_tareas_curso_usuario (
        curso_id,
        usuario_id
    ),

    ADD CONSTRAINT fk_tareas_curso_usuario
        FOREIGN KEY (curso_id, usuario_id)
        REFERENCES cursos(id, usuario_id);

-- Transición: el código nuevo podrá insertar tareas
-- sin enviar prioridad. El código anterior todavía
-- puede usar la columna mientras se actualiza Java.

ALTER TABLE tareas
    MODIFY COLUMN prioridad VARCHAR(30) NULL DEFAULT NULL;

-- =====================================================
-- 3. Calificaciones
-- =====================================================

CREATE TABLE IF NOT EXISTS student_organizer.calificaciones (
    id CHAR(36) PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,
    nombre_actividad VARCHAR(150) NOT NULL,
    nota_obtenida DECIMAL(10, 2) NOT NULL,
    nota_maxima DECIMAL(10, 2) NOT NULL,
    fecha DATE NOT NULL,

    INDEX idx_calificaciones_usuario_fecha (
        usuario_id,
        fecha
    ),

    INDEX idx_calificaciones_curso_usuario (
        curso_id,
        usuario_id
    ),

    CONSTRAINT fk_calificaciones_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES student_organizer.usuarios(id),

    CONSTRAINT fk_calificaciones_curso_usuario
        FOREIGN KEY (curso_id, usuario_id)
        REFERENCES student_organizer.cursos(id, usuario_id),

    CONSTRAINT chk_calificaciones_notas
        CHECK (
            nota_maxima > 0
            AND nota_obtenida >= 0
            AND nota_obtenida <= nota_maxima
        )
);