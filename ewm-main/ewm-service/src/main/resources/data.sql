DELETE FROM compilations_events;

DELETE FROM compilations;
ALTER TABLE compilations ALTER COLUMN id RESTART WITH 1;

DELETE FROM participation_requests;
ALTER TABLE participation_requests ALTER COLUMN id RESTART WITH 1;

DELETE FROM events;
ALTER TABLE events ALTER COLUMN id RESTART WITH 1;

DELETE FROM locations;
ALTER TABLE locations ALTER COLUMN id RESTART WITH 1;

DELETE FROM users;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;

DELETE FROM categories;
ALTER TABLE categories ALTER COLUMN id RESTART WITH 1;

