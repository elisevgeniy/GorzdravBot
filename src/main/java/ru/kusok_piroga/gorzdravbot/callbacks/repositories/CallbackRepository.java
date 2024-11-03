package ru.kusok_piroga.gorzdravbot.callbacks.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kusok_piroga.gorzdravbot.callbacks.models.CallbackEntity;

@Repository
public interface CallbackRepository extends CrudRepository<CallbackEntity, Long> {
}
