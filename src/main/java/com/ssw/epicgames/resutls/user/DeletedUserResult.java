package com.ssw.epicgames.resutls.user;

import com.ssw.epicgames.resutls.Result;

public enum DeletedUserResult implements Result {
    USER_NOT_FOUND,
    FAILURE_DUPLICATE_EMAIL,
    FAILURE_DUPLICATE_PASSWORD
}
