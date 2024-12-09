ALTER TABLE tasks
    ADD date_limits VARCHAR(255);
ALTER TABLE tasks
    ADD time_limits VARCHAR(255);

UPDATE tasks
set (date_limits, time_limits) =
        (
         concat(
                 to_char(timestamp '2000-01-01 00:00:00.00', 'DD.MM.YYYY'),
                 '-',
                 to_char(high_date_limit, 'DD.MM.YYYY')
         ),
         concat(
                 low_time_limit,
                 '-',
                 high_time_limit
         )
            );

ALTER TABLE tasks
    DROP COLUMN high_date_limit;
ALTER TABLE tasks
    DROP COLUMN high_time_limit;
ALTER TABLE tasks
    DROP COLUMN low_time_limit;

ALTER TABLE tasks DROP CONSTRAINT tasks_state_check;
ALTER TABLE tasks ADD CONSTRAINT tasks_state_check
    CHECK ((state)::text = ANY (
        (ARRAY [
            'INIT'::character varying,
            'SET_PATIENT'::character varying,
            'SET_DISTRICT'::character varying,
            'SET_POLYCLINIC'::character varying,
            'SET_SPECIALITY'::character varying,
            'SET_DOCTOR'::character varying,
            'SET_TIME_LIMITS'::character varying,
            'SET_DATE_LIMITS'::character varying,
            'SETUPED'::character varying
            ])::text[]
        ));
