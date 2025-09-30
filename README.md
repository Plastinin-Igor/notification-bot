# Телеграм бот "Помню всё" ⏳💬

<b> Уведомления о важных датах и событиях, будь то дни рождения близких или еженедельные занятия спортом.</b>

---

## 📖 Описание

### Бот умеет:

✅ <b>Рассылать напоминание двух типов:</b>

- <b>По расписанию:</b> выберите конкретные дни недели и точное время, когда хотите получать уведомление.
- <b>Ежегодные события:</b> установите напоминание на определённую дату каждый год, которое придёт ровно в 10 часов
  утра.

## Команды

- Начало работы 🚀 /start
- Справка 🔍 /help
- Список всех уведомлений 📝 /list
- Список напоминаний 🔔 /event
- Список дней рождений 🗓 /day

## 🚀 Установка и запуск

1. <b>Клонировать репозиторий:</b>

```
git clone git@github.com:Plastinin-Igor/notification-bot.git
cd notification-bot
```

2. <b>Настроить application.properties:</b>

```
bot.token=    # токен вашего телеграм-бота
bot.username= # имя пользователя - владельца телеграм бота
```
3. <b>Настроить/создать список пользователей src/main/resources/userlist.json</b>

```
[
  {
    "userId":   12345, //chatId
    "userName": "UserLogin",
    "fullName": "Иванов Иван"
  }
]
```

4. <b>Настроить/создать список событий src/main/resources/events.json</b>

```
[
  {
    "timeNotify": "17:00:00",
    "dayOfWeekNotify": "TUESDAY",
    "nameNotify": "Английский язык",
    "textNotify": "Через 15 минут начнется урок английского языка.",
    "users": [
      12345 //chatId  
    ]
  },
  {
    "dateNotify": "2025-10-16",
    "timeNotify": "20:15:00",
    "dayOfWeekNotify": "FRIDAY",
    "nameNotify": "Тренировка",
    "textNotify": "Через 15 минут пора начинать тренировку.",
    "users": [
      12345 //chatId
    ]
  }
]  
```
5. <b>Настроить/создать список памятных дат и дней рождений src/main/resources/birthdays.json</b>

```
[
  {
    "dateNotify": "0001-01-01",
    "timeNotify": "10:00:00",
    "nameNotify": "С Новым Годом",
    "textNotify": "С Новым годом🎄✨🎁",
    "users": [
      12345
    ]
  },
  {
    "dateNotify": "1985-01-03",
    "timeNotify": "10:00:00",
    "nameNotify": "День рождения у Насти",
    "textNotify": "Сегодня день рождения у Насти 🍰🎉. Не забудь поздравить 🥂✨",
    "users": [
      12345
    ]
  }
]  
```

6. <b>Собрать проект:</b>
```
mvn clean install -DskipTests
```

7. <b>Создать образ на основе файла Dockerfile:</b>
```
docker build -t notification-image .
```

8. <b>Создать и запустить контейнер в фоновом режиме:</b>

```
docker run -d --restart=always --name notification-bot notification-image
```