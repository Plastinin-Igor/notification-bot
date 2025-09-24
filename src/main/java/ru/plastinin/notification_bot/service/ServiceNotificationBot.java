package ru.plastinin.notification_bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import ru.plastinin.notification_bot.model.Notification;
import ru.plastinin.notification_bot.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ServiceNotificationBot {

    private final List<User> usersList;
    private final List<Notification> eventsList;
    private final List<Notification> birthdaysList;


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
        StringBuilder stringBuilder = new StringBuilder("События:\n");
        for (Notification n : eventsList) {
            stringBuilder.append(n.getNameNotify()).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * Список всех поздравлений
     * @return List
     */
    public String getAllBirthdays() {
        StringBuilder stringBuilder = new StringBuilder("Поздравления:\n");
        for (Notification n : birthdaysList) {
            stringBuilder.append(n.getNameNotify()).append("\n");
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
