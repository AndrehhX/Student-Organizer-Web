-- Agrega correo a una base existente sin eliminar cuentas.
-- Las cuentas anteriores quedan con correo NULL y pueden seguir
-- iniciando sesión con su nombre de usuario.

USE student_organizer;

SET @existe_columna_correo = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'usuarios'
      AND column_name = 'correo'
);

SET @sql_correo = IF(
    @existe_columna_correo = 0,
    'ALTER TABLE usuarios ADD COLUMN correo VARCHAR(254) NULL AFTER nombre_usuario',
    'SELECT ''La columna correo ya existe'''
);

PREPARE sentencia_correo FROM @sql_correo;
EXECUTE sentencia_correo;
DEALLOCATE PREPARE sentencia_correo;

SET @existe_indice_correo = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'usuarios'
      AND index_name = 'uq_usuarios_correo'
);

SET @sql_indice_correo = IF(
    @existe_indice_correo = 0,
    'ALTER TABLE usuarios ADD UNIQUE INDEX uq_usuarios_correo (correo)',
    'SELECT ''El índice de correo ya existe'''
);

PREPARE sentencia_indice_correo FROM @sql_indice_correo;
EXECUTE sentencia_indice_correo;
DEALLOCATE PREPARE sentencia_indice_correo;
