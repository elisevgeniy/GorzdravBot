-- liquibase formatted sql

-- changeset elisevgeniy:1733583229838-1
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

-- changeset elisevgeniy:1733583229838-3
ALTER TABLE tasks
    DROP COLUMN high_date_limit;
ALTER TABLE tasks
    DROP COLUMN high_time_limit;
ALTER TABLE tasks
    DROP COLUMN low_time_limit;

