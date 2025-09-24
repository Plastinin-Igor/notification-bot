package ru.plastinin.notification_bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.plastinin.notification_bot.bot.NotificationTelegramBot;
import ru.plastinin.notification_bot.model.Notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ScheduleServiceNotification {

    private final NotificationTelegramBot telegramBot;

    private List<Notification> eventsList;
    private List<Notification> birthdaysList;

    public ScheduleServiceNotification(NotificationTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }


    @PostConstruct
    public void initNotifications() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        try {
            ClassPathResource resourceNotification = new ClassPathResource("events.json");
            ClassPathResource resourceBirthdays = new ClassPathResource("birthdays.json");

            eventsList = mapper.readValue(resourceNotification.getInputStream(),
                    mapper.getTypeFactory().constructCollectionType(ArrayList.class, Notification.class));
            birthdaysList = mapper.readValue(resourceBirthdays.getInputStream(),
                    mapper.getTypeFactory().constructCollectionType(ArrayList.class, Notification.class));

        } catch (IOException ex) {
            log.error("Ошибка при загрузке данных из файлов.", ex);
            throw new RuntimeException(ex);
        }
    }


    /**
     * Запускаем проверку каждые 1 минуту.
     */
    @Scheduled(cron = "*/60 * * * * ?")
    public void checkAndSendNotifications() {
        for (Notification notification : eventsList) {
            sendNotification(notification);
        }
    }

    private boolean isTimeToSend(Notification notification) {
        return true;
    }


    private void sendNotification(Notification notification) {
        for (Long userId : notification.getUsers()) {
            telegramBot.sendMessage(userId, notification.getTextNotify());
            log.info("Message {} sent to userId {}", notification.getNameNotify(), notification.getUsers());
        }
    }
}

