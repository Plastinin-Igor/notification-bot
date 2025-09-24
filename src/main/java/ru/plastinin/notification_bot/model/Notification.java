package ru.plastinin.notification_bot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @JsonProperty("typeNotify")
    String type;

    @JsonProperty("dateNotify")
    LocalDate dateNotify;

    @JsonProperty("timeNotify")
    LocalTime timeNotify;

    @JsonProperty("dayOfWeekNotify")
    DayOfWeek dayOfWeekNotify;

    @JsonProperty("nameNotify")
    String nameNotify;

    @JsonProperty("textNotify")
    String textNotify;

    @JsonProperty("users")
    List<User> users;
}
