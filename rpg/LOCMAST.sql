create or replace table locmast (
  name char(30) not null default ' ',
  xcoor decimal(7, 3) not null default 0,
  ycoor decimal(7, 3) not null default 0,
  primary key (name)
)
rcdfmt locmastf;

insert into locmast values
  ('Airport', 1, 12),
  ('Zoo', 2, 22),
  ('Parking Lot', 3, 32),
  ('Beach', 4, 42),
  ('Hospital', 5, 52),
  ('Gas Station', 6, 62),
  ('Police Station', 7, 72),
  ('Fire Station', 8, 82),
  ('Bank', 9, 92);

