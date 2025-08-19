-- Cambiar nombre de la columna autor_id a usuario_id en la tabla topicos
ALTER TABLE topicos CHANGE autor_id usuario_id BIGINT NOT NULL;

-- Eliminar la antigua clave foránea fk_autor
ALTER TABLE topicos DROP FOREIGN KEY fk_autor;

-- Crear una nueva clave foránea fk_usuario
ALTER TABLE topicos ADD CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id);