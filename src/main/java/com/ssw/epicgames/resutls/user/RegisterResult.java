package com.ssw.epicgames.resutls.user;

import com.ssw.epicgames.resutls.Result;

public enum RegisterResult implements Result {
    FAILURE_DUPLICATE_PHONE,
    FAILURE_DUPLICATE_EMAIL,
    FAILURE_DUPLICATE_NICKNAME,
}
