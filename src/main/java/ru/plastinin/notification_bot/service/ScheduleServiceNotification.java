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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ScheduleServiceNotification {

    private final NotificationTelegramBot telegramBot;

    private List<Notification> eventsList;
    private List<Notification> birthdaysList;
    private final Map<Notification, LocalDate> historyNotification;

    public ScheduleServiceNotification(NotificationTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        this.historyNotification = new HashMap<>();
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
     * Запускаем проверку каждую 1 минуту.
     */
    @Scheduled(cron = "*/60 * * * * ?")
    public void checkAndSendNotifications() {

        // События
        for (Notification notification : eventsList) {
            if (isTimeToSend(notification)) {
                sendNotification(notification);
            }
        }
        // Дни рождения и памятные даты
        for (Notification notification : birthdaysList) {
            if (isTimeToSend(notification)) {
                sendNotification(notification);
            }
        }
    }

    /**
     * Метод определяет, наступило ли время отправить сообщение
     * @return boolean
     */
    private boolean isTimeToSend(Notification notification) {
        LocalDateTime localDate = LocalDateTime.now();

        if (notification.getDayOfWeekNotify() != null) {
            // Если задан день недели, то проверяем его и точное время и направляем уведомление
            return notification.getDayOfWeekNotify().equals(localDate.getDayOfWeek()) &&
                   notification.getTimeNotify().getHour() == localDate.getHour() &&
                   notification.getTimeNotify().getMinute() == localDate.getMinute();
        } else {
            // Если день недели не задан, то проверяем дату и если дата совпадает и время после 10:00
            // и сообщение не направлялось, то направляем сообщение.
            return notification.getDateNotify().getDayOfMonth() == localDate.getDayOfMonth() &&
                   notification.getDateNotify().getMonth() == localDate.getMonth() &&
                   localDate.getHour() >= 10 && isNotificationWasNotSent(notification);
        }
    }

    /**
     * Метод определяет, направлялось ли сегодня данное сообщение или нет
     */
    private boolean isNotificationWasNotSent(Notification notification) {
        LocalDate today = LocalDate.now();
        if (historyNotification.containsKey(notification)) {
            return !historyNotification.get(notification).equals(today);
        } else {
            return true;
        }
    }

    /**
     * Отправка сообщения в бот
     */
    private void sendNotification(Notification notification) {
        for (Long userId : notification.getUsers()) {
            telegramBot.sendMessage(userId, notification.getTextNotify());
            log.info("Message {} sent to userId {}", notification.getNameNotify(), notification.getUsers());
        }
        // Запишем дату и сообщение в карту
        historyNotification.put(notification, LocalDate.now());
    }

}

