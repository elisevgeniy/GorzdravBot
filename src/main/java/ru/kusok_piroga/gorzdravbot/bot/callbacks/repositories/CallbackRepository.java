package ru.kusok_piroga.gorzdravbot.bot.callbacks.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.models.CallbackEntity;

import java.util.Date;

@Repository
public interface CallbackRepository extends CrudRepository<CallbackEntity, Long> {

    @Transactional
    @Modifying
    @Query("delete from CallbackEntity c where c.createDate < ?1")
    void deleteByCreateDateBefore(Date createDate);

}
