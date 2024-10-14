package ru.kusok_piroga.gorzdravbot.bot.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="tasks")
@Data
public class TaskEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;

    @Enumerated(EnumType.STRING)
    TaskState state;

    Long dialogId;

    Integer districtId;

    Integer polyclinicId;

    Integer specialityId;
}
