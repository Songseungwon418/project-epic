package com.ssw.epicgames.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(of ={"email"})
public class UserEntity {
    private String email;
    private String password;
    private String Name;
    private LocalDateTime birthdate;
    private String nickname;
    private String phone;
    private String addr;
    private LocalDateTime registerDate;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedDate;
    private int isVerified;
}
