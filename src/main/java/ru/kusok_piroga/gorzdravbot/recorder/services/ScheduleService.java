package ru.kusok_piroga.gorzdravbot.recorder.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.api.models.AvailableAppointment;
import ru.kusok_piroga.gorzdravbot.common.models.TaskEntity;
import ru.kusok_piroga.gorzdravbot.common.repositories.TaskRepository;

import java.util.Date;
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
//    @Scheduled(cron = "0 */5 * * * *")
    @Scheduled(cron = "*/20 * * * * *")
    public void scheduleExecute() {
        log.info("Scheduled execution start");

        List<TaskEntity> tasks = taskRepository.findAllUncompletedTasks();
        tasks.stream().parallel().forEach(this::taskProcess);

        log.info("Scheduled execution finish");
    }

    public void taskProcess(TaskEntity task) {
        log.info("Task, id={}, process start", task.getId());
        List<AvailableAppointment> availableAppointments = fetchService.getValidAvailableAppointments(task);

        log.info("Task, id={}, available appointments count {}", task.getId(), availableAppointments.size());

        if (!availableAppointments.isEmpty()) {

            log.info("Task, id={}, try to notify", task.getId());

            if (notifyService.needNotify(task) && notifyService.notifyToChat(task, availableAppointments)) {
                task.setLastNotify(new Date());
                task = taskRepository.save(task);
                log.info("Task, id={}, notification success", task.getId());
                return;
            }

            if (recordService.isTimeToRecord(task)){
                log.info("Task, id={}, will be recorded", task.getId());
                if (!recordService.makeRecord(task, availableAppointments)){
                    task.setCompleted(true);
                    task = taskRepository.save(task);
                    log.info("Task, id={}, recording success", task.getId());
                }
            }
        }

        log.info("Task, id={}, process finish", task.getId());
    }
}
