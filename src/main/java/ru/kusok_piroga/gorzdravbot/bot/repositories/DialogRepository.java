package ru.kusok_piroga.gorzdravbot.bot.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kusok_piroga.gorzdravbot.bot.models.DialogEntity;

@Repository
    public interface DialogRepository extends CrudRepository<DialogEntity, Long> {
}
