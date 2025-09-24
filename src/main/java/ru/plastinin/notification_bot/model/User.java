package ru.plastinin.notification_bot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class User {
    @JsonProperty("userId")
    int userId;

    @JsonProperty("userName")
    String userName;

    @JsonProperty("fullName")
    String fullName;
}
