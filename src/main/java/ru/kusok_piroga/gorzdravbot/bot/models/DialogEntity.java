package ru.kusok_piroga.gorzdravbot.bot.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="last_commands")
@Data
public class DialogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String lastCommand;
}
