DROP TABLE IF EXISTS relations CASCADE;
DROP TABLE IF EXISTS digit_tree CASCADE;

CREATE TABLE digit_tree
(
  digit_node_id LONG  NOT NULL  PRIMARY KEY   AUTO_INCREMENT,
  value         INT   NOT NULL,
  sum           INT   NOT NULL
);

CREATE TABLE relations
(
  id        LONG  NOT NULL  PRIMARY KEY   AUTO_INCREMENT,
  parent_id LONG  NOT NULL  REFERENCES digit_tree(digit_node_id) ON DELETE CASCADE,
  child_id  LONG  NOT NULL  REFERENCES digit_tree(digit_node_id) ON DELETE CASCADE
)