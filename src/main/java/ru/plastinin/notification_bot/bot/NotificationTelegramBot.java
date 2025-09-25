package ru.plastinin.notification_bot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.plastinin.notification_bot.service.ServiceNotificationBot;

@Component
@Slf4j
public class NotificationTelegramBot extends TelegramLongPollingBot {

    @Value("${bot.username}")
    private String botUsername;

    @Autowired
    private ServiceNotificationBot service;

    private static final String START = "/start";
    private static final String HELP = "/help";
    private static final String DEBUG = "/debug";

    public NotificationTelegramBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {

        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        Message msg = update.getMessage();
        String name = update.getMessage().getChat().getUserName();
        String firstName = update.getMessage().getChat().getTitle();
        String userName = name != null ? name : firstName;

        // Проверим, что пользователь есть в списке
        // Если отсутствует, то дальнейшие действия запрещены
        if (service.getUserById(chatId).isEmpty()) {
            log.info("Access denied for user: {} with, chatId: {}.", userName, chatId);
            sendMessage(chatId, "Access denied");
            return;
        }

        // Проверим, что пользовательское сообщение не пустое
        if (!update.hasMessage() || !msg.hasText()) {
            return;
        }

        // Обработка команд
        switch (message) {
            case START -> {
                startCommand(chatId, userName);
                log.info("START from username: {}, chatId: {}.", userName, chatId);
            }
            case HELP -> {
                helpCommand(chatId, userName);
                log.info("HELP from username: {}, chatId: {}.", userName, chatId);
            }
            case DEBUG -> {

                sendMessage(chatId, service.getAllUsers());
                sendMessage(chatId, service.getAllEvents());
                sendMessage(chatId, service.getAllBirthdays());

                log.info("DEBUG from username: {}, chatId: {}.", userName, chatId);
            }
            default -> {
                sendMessage(chatId, "Команда не поддерживается");
                log.info("The command is not supported. Username: {}, chatId: {}.", userName, chatId);
            }
        }

    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }


    public void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        sendMessage.setParseMode("HTML");
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }


    private void startCommand(Long chatId, String userName) {
        String text = """
                %s, добро пожаловать в бот!
                
                Я умею отправлять в группу заранее настроенные уведомления:
                
                Например:
                 - Сегодня День рождения у бабушки 🥳
                 - Скоро начнется урок 🎓
                 - Пора собираться на занятия ✏️📐
                 - Не забудьте принять витамины 💊
                
                
               
                Начало работы 🚀 /start
                Справка 🔍 /help
                """;
        String formatedText = String.format(text, userName);
        sendMessage(chatId, formatedText);
    }

    private void helpCommand(Long chatId, String userName) {
        String text = """
                Телеграм-бот направляет пользователям уведомления.
                
                
                
                Команды:
                
                - Начало работы 🚀 /start
                
                - Справка 🔍 /help
                
                - Список всех уведомлений 📝 /list
                
                - Список напоминаний 🔔 /event
                
                - Список дней рождений 🗓 /day
                
                """;
        sendMessage(chatId, text);
    }

}
