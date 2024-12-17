package com.ssw.epicgames.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = {"user_email", "friend_email"})
public class FriendsEntity {
    private String user_email;
    private String friend_email;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
