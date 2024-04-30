create or replace table locmast (
  name char(30) not null default ' ',
  xcoor decimal(7, 3) not null default 0,
  ycoor decimal(7, 3) not null default 0,
  primary key (name)
)
rcdfmt locmastf;

insert into locmast values --forward left is positive
  ('Airport', 17, 32),
  ('Zoo', -18, 30),
  ('Parking Lot', -23, 9),
  ('Beach', -8, -7),
  ('Hospital', -25, 24),
  ('Gas Station', 8, 4),
  ('Police Station', 0, 29),
  ('Fire Station', -13, 14),
  ('Bank', 13, -2);

