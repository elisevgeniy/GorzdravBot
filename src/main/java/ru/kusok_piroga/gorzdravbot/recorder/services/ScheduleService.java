package ru.kusok_piroga.gorzdravbot.recorder.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.domain.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.domain.repositories.TaskRepository;
import ru.kusok_piroga.gorzdravbot.recorder.utils.PassTimeChecker;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final TaskRepository taskRepository;
    private final FetchService fetchService;
    private final NotifyService notifyService;
    private final RecordService recordService;

    @Async
    @Scheduled(cron = "0 */5 * * * *")
    public void scheduleExecute() {
        log.info("Scheduled execution start");

        List<TaskEntity> tasks = taskRepository.findAllUncompletedTasks();

        log.info("Uncompleted tasks id list: {}", tasks.stream().mapToLong(TaskEntity::getId).toArray());

        tasks.stream().parallel()
                .filter(PassTimeChecker::check)
                .forEach(this::taskProcess);

        log.info("Scheduled execution finish");
    }

    public void taskProcess(TaskEntity task) {
        log.info("Task, id={}, process start", task.getId());
        List<AvailableAppointment> availableAppointments = fetchService.getValidAvailableAppointments(task);

        log.info("Task, id={}, available appointments count {}", task.getId(), availableAppointments.size());

        if (!availableAppointments.isEmpty()) {

            log.info("Task, id={}, try to notify", task.getId());

            if (notifyService.needNotify(task) && notifyService.notifyToChat(task, availableAppointments)) {
                task.setLastNotify(LocalDateTime.now());
                task = taskRepository.save(task);
                log.info("Task, id={}, notification success", task.getId());
                return;
            }

            if (recordService.isTimeToRecord(task)){
                log.info("Task, id={}, will be recorded", task.getId());
                if (!recordService.makeRecord(task, availableAppointments)){
                    log.info("Task, id={}, recording fail", task.getId());
                }
            }
        } else {
            if (task.getLastNotify() != null){
                task.setLastNotify(null);
                taskRepository.save(task);
                log.info("Task, id={}, notification nulled", task.getId());
            }
        }

        log.info("Task, id={}, process finish", task.getId());
    }
}
