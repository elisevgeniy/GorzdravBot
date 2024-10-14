package ru.kusok_piroga.gorzdravbot.bot.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Entity
@Table(name="last_commands")
@Data
public class DialogEntity {
    @Id
    Long id;

    String lastCommand;
}
