
CREATE TABLE to_do_list(
  id INTEGER serial NOT NULL,
  name VARCHAR(50) NOT NULL,
  description VARCHAR(255) NOT NULL,
  done BOOL NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id)
);

CREATE DOMAIN priority_domain AS
  INT2 DEFAULT 1
  CHECK VALUE BETWEEN 1 AND 5;

CREATE DOMAIN status_domain AS
  VARCHAR(15) DEFAULT 'TO DO'
  CHECK VALUE IN ('TO DO', 'IN PROGRESS', 'DEFERRED', 'DONE');

CREATE TABLE task(
  id INTEGER SERIAL NOT NULL,
  to_do_list_id INTEGER NOT NULL,
  description VARCHAR(255) NOT NULL,
  priority priority_domain NOT NULL,
  status status_domain NOT NULL,
  done BOOL NOT NULL DEFAULT FALSE,
  observations VARCHAR(255),
  PRIMARY KEY (id),
  CONSTRAINT task_to_do_list_fkey
    FOREIGN KEY (to_do_list_id) REFERENCES to_do_list(id)
    ON DELETE CASCADE ON UPDATE CASCADE
);
