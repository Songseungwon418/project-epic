package com.ssw.epicgames.resutls.user;

import com.ssw.epicgames.resutls.Result;

public enum RegisterResult implements Result {
    FAILURE_DUPLICATE_PHONE,
    FAILURE_DUPLICATE_EMAIL,
    FAILURE_NOT_EMAIL_FORMAT,
    FAILURE_DUPLICATE_NICKNAME,
    FAILURE_INVALID_DATE_FORMAT,      // 잘못된 날짜 형식
    SUCCESS
}
