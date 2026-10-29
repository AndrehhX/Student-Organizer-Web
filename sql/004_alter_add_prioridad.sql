ALTER TABLE student_organizer.tareas
ADD COLUMN prioridad VARCHAR(30) NOT NULL DEFAULT 'media'
AFTER fecha_entrega;

--al final lo volvi a agregar, se me fue por alto xd
