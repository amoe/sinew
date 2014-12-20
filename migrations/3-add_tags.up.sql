CREATE TABLE tag (
    id SERIAL,
    name VARCHAR(80)
);

CREATE TABLE scene_tags (
    scene_id INTEGER,
    tag_id INTEGER
);
