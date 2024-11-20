package ru.kusok_piroga.gorzdravbot.domain.repositories;

import lombok.NonNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskState;

import java.util.List;
import java.util.Optional;

@Repository
    public interface TaskRepository extends CrudRepository<TaskEntity, Long> {
    Optional<TaskEntity> findFirstByDialogIdAndStateIsNot(@NonNull Long id, @NonNull TaskState state);

    @Query("select t from TaskEntity t where t.completed = false and t.state = ?1")
    List<TaskEntity> findAllUncompletedTasksWithState(TaskState state);

    @Query("select t from TaskEntity t where t.dialogId = ?1 and t.completed = ?2 and t.state = ?3")
    List<TaskEntity> findTasksByDialogIdWithStateAndCompletedStatus(Long dialogId, Boolean completed, TaskState state);

    default List<TaskEntity> findAllUncompletedTasks(){
        return findAllUncompletedTasksWithState(TaskState.FINAL);
    }
    default List<TaskEntity> findAllCompletedTasksByDialogId(Long dialogId){
        return findTasksByDialogIdWithStateAndCompletedStatus(dialogId, true, TaskState.FINAL);
    }
    default List<TaskEntity> findAllUncompletedTasksByDialogId(Long dialogId){
        return findTasksByDialogIdWithStateAndCompletedStatus(dialogId, false, TaskState.FINAL);
    }


    @Query("select t from TaskEntity t where t.dialogId = ?1 and t.state = ?2")
    List<TaskEntity> findByDialogIdAndState(Long dialogId, TaskState state);

    default List<TaskEntity> findAllByDialogId(Long dialogId){
        return findByDialogIdAndState(dialogId, TaskState.FINAL);
    }

    @Transactional
    @Modifying
    @Query("delete from TaskEntity t where t.dialogId = ?1 and not t.state = ?2 ")
    // todo: add limit 1 for PostgreSQL
    void deleteByDialogIdAndStateIsNot(Long dialogId, TaskState state);
}
