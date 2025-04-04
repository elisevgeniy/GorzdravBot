package ru.kusok_piroga.gorzdravbot.bot.callbacks.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name="callbacks")
@Getter
@Setter
public class CallbackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String function;

    private String data;

    @CreationTimestamp
    private Date createDate;
}
