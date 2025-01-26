package ru.kusok_piroga.gorzdravbot.recorder.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.repositories.TaskRepository;
import ru.kusok_piroga.gorzdravbot.recorder.utils.PassTimeChecker;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final TaskRepository taskRepository;
    private final ScheduleTaskService scheduleTaskService;

    @Async
    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void scheduleExecute() {
        log.info("Scheduled execution start");

        List<TaskEntity> tasks = taskRepository.findAllUncompletedTasks();

        log.info("Uncompleted tasks id list: {}", tasks.stream().mapToLong(TaskEntity::getId).toArray());

        tasks.stream().parallel()
                .filter(PassTimeChecker::check)
                .map(TaskEntity::getId)
                .forEach(scheduleTaskService::taskProcess);

        log.info("Scheduled execution finish");
    }
}
