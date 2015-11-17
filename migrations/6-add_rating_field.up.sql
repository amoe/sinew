ALTER TABLE scene
ADD COLUMN rating SMALLINT CHECK (rating > 0 AND rating <= 5);
