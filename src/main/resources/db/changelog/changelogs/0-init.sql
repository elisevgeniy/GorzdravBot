create table patients
(
    id          bigserial primary key,
    birthday    timestamp(6),
    dialog_id   bigint,
    first_name  varchar(255),
    middle_name varchar(255),
    patient_id  varchar(255),
    second_name varchar(255),
    state       varchar(255)
);

create table tasks
(
    id                      bigserial primary key,
    completed               boolean,
    dialog_id               bigint,
    district_id             integer,
    doctor_id               varchar(255),
    high_date_limit         timestamp(6),
    high_time_limit         varchar(255),
    last_notify             timestamp(6),
    low_time_limit          varchar(255),
    polyclinic_id           integer,
    recorded_appointment_id varchar(255),
    speciality_id           integer,
    state                   varchar(255),
    patient_id              bigint
);

create table last_commands
(
    id           bigserial primary key,
    last_command varchar(255)
);

create table callbacks
(
    id          bigserial primary key,
    create_date timestamp(6),
    data        varchar(255),
    function    varchar(255)
);


alter table patients add constraint patients_state_check
    check ((state)::text = ANY (
        (ARRAY [
            'SET_FIRST_NAME'::character varying,
            'SET_SECOND_NAME'::character varying,
            'SET_MIDDLE_NAME'::character varying,
            'SET_BIRTHDAY'::character varying,
            'COMPLETED'::character varying
            ])::text[]
        ));

alter table tasks add constraint tasks_state_check
    check ((state)::text = ANY (
        (ARRAY [
            'INIT'::character varying,
            'SET_PATIENT'::character varying,
            'SET_DISTRICT'::character varying,
            'SET_POLYCLINIC'::character varying,
            'SET_SPECIALITY'::character varying,
            'SET_DOCTOR'::character varying,
            'SET_TIME_LOW_LIMITS'::character varying,
            'SET_TIME_HIGH_LIMITS'::character varying,
            'SET_DATE_LIMITS'::character varying,
            'SETUPED'::character varying
            ])::text[]
        ));

alter table tasks add constraint patient_id_ref foreign key (patient_id) references patients(id);