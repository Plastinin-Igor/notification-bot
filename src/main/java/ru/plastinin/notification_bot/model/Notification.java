package ru.plastinin.notification_bot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

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
    List<Long> users;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(dateNotify, that.dateNotify) && Objects.equals(timeNotify, that.timeNotify)
               && dayOfWeekNotify == that.dayOfWeekNotify && Objects.equals(nameNotify, that.nameNotify)
               && Objects.equals(textNotify, that.textNotify) && Objects.equals(users, that.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateNotify, timeNotify, dayOfWeekNotify, nameNotify, textNotify, users);
    }
}
