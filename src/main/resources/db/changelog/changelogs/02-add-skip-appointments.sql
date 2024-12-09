CREATE TABLE skip_appointment
(
    id             BIGSERIAL PRIMARY KEY,
    task_id        BIGINT       NOT NULL,
    appointment_id VARCHAR(255) NOT NULL
);

ALTER TABLE skip_appointment
    ADD CONSTRAINT FK_SKIP_APPOINTMENT_ON_TASK FOREIGN KEY (task_id) REFERENCES tasks (id);

