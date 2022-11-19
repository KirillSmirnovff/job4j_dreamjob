CREATE TABLE candidate (
   id SERIAL PRIMARY KEY,
   name TEXT,
   description TEXT,
   city_id INT,
   created TIMESTAMP,
   photo BYTEA
);