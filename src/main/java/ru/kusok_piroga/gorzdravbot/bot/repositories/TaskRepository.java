package ru.kusok_piroga.gorzdravbot.bot.repositories;

import lombok.NonNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kusok_piroga.gorzdravbot.bot.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.bot.models.TaskState;

import java.util.Optional;

@Repository
    public interface TaskRepository extends CrudRepository<TaskEntity, Long> {
    Optional<TaskEntity> findFirstByDialogIdAndStateIsNot(@NonNull Long id, @NonNull TaskState state);

    @Transactional
    @Modifying
    @Query("delete from TaskEntity t where t.dialogId = ?1 and not t.state = ?2 ")
    // todo: add limit 1 for PostgreSQL
    void deleteByDialogIdAndStateIsNot(Long dialogId, TaskState state);
}
