package ru.kusok_piroga.gorzdravbot.bot.callbacks.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kusok_piroga.gorzdravbot.bot.callbacks.repositories.CallbackRepository;

import java.util.Calendar;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanService {

    private final CallbackRepository repository;

    @Scheduled(cron = "0 0 0 * * *")
    private void clean(){
        log.info("Clean callbacks in db");
        Calendar limitDate = Calendar.getInstance();
        limitDate.add(Calendar.DAY_OF_MONTH, -1);
        repository.deleteByCreateDateBefore(limitDate.getTime());
    }
}
