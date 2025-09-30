package ru.plastinin.notification_bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ru.plastinin.notification_bot.model.Notification;
import ru.plastinin.notification_bot.model.User;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class ServiceNotificationBot {

    private final List<User> usersList;
    private final List<Notification> eventsList;
    private final List<Notification> birthdaysList;
    private final Map<DayOfWeek, String> dayOfWeekStringMap;

    // Сортировка списков по дате (исключая год) и дню недели
    Comparator<Notification> byMonthAndDay = Comparator.comparing(notification ->
            notification.getDateNotify().withYear(LocalDate.now().getYear()));
    Comparator<Notification> byDayOfWeek = Comparator.comparing(Notification::getDayOfWeekNotify);

    // Формат даты
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ServiceNotificationBot() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        try {
            ClassPathResource resourceUser = new ClassPathResource("userlist.json");
            ClassPathResource resourceNotification = new ClassPathResource("events.json");
            ClassPathResource resourceBirthdays = new ClassPathResource("birthdays.json");

            usersList = mapper.readValue(resourceUser.getInputStream(),
                    mapper.getTypeFactory().constructCollectionType(ArrayList.class, User.class));
            eventsList = mapper.readValue(resourceNotification.getInputStream(),
                    mapper.getTypeFactory().constructCollectionType(ArrayList.class, Notification.class));
            birthdaysList = mapper.readValue(resourceBirthdays.getInputStream(),
                    mapper.getTypeFactory().constructCollectionType(ArrayList.class, Notification.class));

        } catch (IOException ex) {
            log.error("Ошибка при загрузке данных из файлов.", ex);
            throw new RuntimeException(ex);
        }

        dayOfWeekStringMap = new HashMap<>();
        dayOfWeekStringMap.put(DayOfWeek.MONDAY, "Понедельник");
        dayOfWeekStringMap.put(DayOfWeek.TUESDAY, "Вторник");
        dayOfWeekStringMap.put(DayOfWeek.WEDNESDAY, "Среда");
        dayOfWeekStringMap.put(DayOfWeek.THURSDAY, "Четверг");
        dayOfWeekStringMap.put(DayOfWeek.FRIDAY, "Пятница");
        dayOfWeekStringMap.put(DayOfWeek.SATURDAY, "Суббота");
        dayOfWeekStringMap.put(DayOfWeek.SUNDAY, "Воскресение");
    }

    /**
     * Сообщение со списком зарегистрированных пользователей
     * @return String
     */
    public String getAllUsers() {
        StringBuilder stringBuilder = new StringBuilder("Зарегистрированные пользователи:\n");
        for (User u : usersList) {
            stringBuilder.append("@").append(u.getUserName()).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Список всех событий
     * @return List
     */
    public String getAllEvents() {
        StringBuilder stringBuilder = new StringBuilder("Еженедельные события:\n\n");
        eventsList.sort(byDayOfWeek);
        for (Notification event : eventsList) {
            stringBuilder
                    .append(dayOfWeekStringMap.get(event.getDayOfWeekNotify()))
                    .append(": ")
                    .append(event.getNameNotify())
                    .append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Список всех поздравлений
     * @return List
     */
    public String getAllBirthdays() {
        StringBuilder stringBuilder = new StringBuilder("Поздравления и памятные даты:\n\n");
        birthdaysList.sort(byMonthAndDay); // Сортировка по дню и месяцу исключая год
        for (Notification days : birthdaysList) {
            stringBuilder
                    .append(days.getDateNotify().format(formatter))
                    .append(" ")
                    .append(days.getNameNotify()).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Поиск пользователя по id
     * @return Optional User
     */
    public Optional<User> getUserById(Long chatId) {
        return usersList.stream()
                .filter(user -> user.getUserId().equals(chatId))
                .findFirst();
    }

}
