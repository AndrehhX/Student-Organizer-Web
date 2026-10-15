 -- ya no se uso lo que es prioridad, queda afuera. ya que la base de datos ya tenia prioridad

USE student_organizer;
 
ALTER TABLE tareas
    DROP COLUMN prioridad;