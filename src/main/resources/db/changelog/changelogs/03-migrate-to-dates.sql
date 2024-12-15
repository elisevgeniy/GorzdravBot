ALTER TABLE patients
    RENAME COLUMN birthday TO birthday_old;

ALTER TABLE patients
    ADD birthday date;

UPDATE patients
set birthday = birthday_old::date;

ALTER TABLE patients
    DROP COLUMN birthday_old;

