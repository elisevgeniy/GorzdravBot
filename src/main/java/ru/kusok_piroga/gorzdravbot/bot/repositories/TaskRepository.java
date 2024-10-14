package ru.kusok_piroga.gorzdravbot.bot.repositories;

import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kusok_piroga.gorzdravbot.bot.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.bot.models.TaskState;

import java.util.Optional;

@Repository
    public interface TaskRepository extends CrudRepository<TaskEntity, Integer> {
    Optional<TaskEntity> findFirstByDialogIdAndStateIsNot(@NonNull Long id, @NonNull TaskState state);
}
