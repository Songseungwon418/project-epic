package com.ssw.epicgames.enums;

public enum GracEnum {
    ALL("ALL"),
    TWELVE_YEARS("12Y"),
    FIFTEEN_YEARS("15Y"),
    ADULT("ADULT");

    private final String value;

    // 생성자
    GracEnum(String value) {
        this.value = value;
    }

    // 값을 가져오는 메서드
    public String getValue() {
        return value;
    }

    // 문자열로 enum을 찾는 메서드 (예: "12Y"로 GracEnum.TWELVE_YEARS를 얻을 수 있음)
    public static GracEnum fromValue(String value) {
        for (GracEnum grac : GracEnum.values()) {
            if (grac.getValue().equals(value)) {
                return grac;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }
}
