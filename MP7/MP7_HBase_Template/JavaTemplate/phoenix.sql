DROP VIEW IF EXISTS "powers";

CREATE VIEW IF NOT EXISTS "powers"
    (pk VARCHAR PRIMARY KEY,
    "personal"."hero" VARCHAR,
    "personal"."power" VARCHAR,
    "professional"."name" VARCHAR);

SELECT "p1"."name" AS "Name1", "p2"."name" AS "Name2", "p1"."power" AS "Power"
FROM "powers" AS "p1", "powers" AS "p2"
WHERE "p1"."power" = "p2"."power"
AND "p1"."hero" = 'yes'
AND "p2"."hero" = 'yes';
