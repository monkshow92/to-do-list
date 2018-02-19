INSERT INTO to_do_list
  (name, description)
VALUES
  ('To-do List 1', 'First to-do list'),
  ('To-do List 2', 'Second to-do list'),
  ('To-do List 3', 'Third to-do list'),
  ('To-do List 4', 'Fourth to-do list'),
  ('To-do List 5', 'Fifth to-do list')
;

INSERT INTO task
  (to_do_list_id, description)
VALUES
  (1, 'First task'),
  (1, 'Second task'),
  (1, 'Third task'),
  (2, 'First task'),
  (2, 'Second task'),
  (2, 'Third task'),
  (3, 'First task'),
  (3, 'Second task'),
  (3, 'Third task'),
  (4, 'First task'),
  (4, 'Second task'),
  (4, 'Third task'),
  (5, 'First task'),
  (5, 'Second task'),
  (5, 'Third task')
;