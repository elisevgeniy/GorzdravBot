package ru.kusok_piroga.gorzdravbot.recorder.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleTaskService {
    private final TaskRepository taskRepository;
    private final FetchService fetchService;
    private final NotifyService notifyService;
    private final RecordService recordService;

    @Transactional
    public void taskProcess(@NonNull long taskId) {
        log.info("Task, id={}, process start", taskId);

        TaskEntity task = getTask(taskId);
        if (task == null) return;

        List<AvailableAppointment> availableAppointments = getAvailableAppointments(task);

        if (!availableAppointments.isEmpty()) {
            if (notifyIfNeed(task, availableAppointments)){
                return;
            }

            if (recordService.isTimeToRecord(task)){
                log.info("Task, id={}, will be recorded", task.getId());
                if (!recordService.makeRecord(task, availableAppointments)){
                    log.info("Task, id={}, recording fail", task.getId());
                }
            }
        } else {
            clearNotify(task);
        }

        log.info("Task, id={}, process finish", task.getId());
    }

    @NotNull
    private List<AvailableAppointment> getAvailableAppointments(TaskEntity task) {
        List<AvailableAppointment> availableAppointments = fetchService.getValidAvailableAppointments(task);
        log.info("Task, id={}, available appointments count {}", task.getId(), availableAppointments.size());
        return availableAppointments;
    }

    @Nullable
    private TaskEntity getTask(long taskId) {
        try {
            TaskEntity task = taskRepository.findTaskByIdWithLock(taskId).orElseThrow();
            if (task.getCompleted()){
                log.warn("Task, id={}, process terminate, already completed", taskId);
                return null;
            }
            return task;
        } catch (PessimisticLockingFailureException e){
            log.error("Task, id={}, process error, db row is locked", taskId, e);
            return null;
        }
    }

    private boolean notifyIfNeed(TaskEntity task, List<AvailableAppointment> availableAppointments) {
        log.info("Task, id={}, try to notify", task.getId());

        if (!notifyService.needNotify(task)) {
            log.info("Task, id={}, already notified", task.getId());
            return false;
        }
        if (notifyService.notifyToChat(task, availableAppointments)) {
            task.setLastNotify(LocalDateTime.now());
            task = taskRepository.save(task);
            log.info("Task, id={}, notification success", task.getId());
        }
        return true;
    }

    private void clearNotify(TaskEntity task){
        if (task.getLastNotify() != null){
            task.setLastNotify(null);
            taskRepository.save(task);
            log.info("Task, id={}, notification nulled", task.getId());
        }
    }


}
