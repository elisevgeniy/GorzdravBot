package ru.kusok_piroga.gorzdravbot.bot.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.checkerframework.common.aliasing.qual.Unique;

@Entity
@Table(name = "last_commands")
@Data
public class DialogEntity {
    @Id
    @Unique
    Long dialogId;

    String lastCommand;
}
