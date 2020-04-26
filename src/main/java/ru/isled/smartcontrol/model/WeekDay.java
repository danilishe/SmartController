package ru.isled.smartcontrol.model;

public enum WeekDay {
    EVERY_DAY("Ежедневно", ""),
    MONDAY("Понедельник", "mon"),
    TUESDAY("Вторник", "tue"),
    WEDNESDAY("Среда", "wed"),
    THURSDAY("Четверг", "thu"),
    FRIDAY("Пятница", "fri"),
    SATURDAY("Суббота", "sat"),
    SUNDAY("Воскресенье", "sun");

    private final String description;
    private final String code;

    WeekDay(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return description;
    }

    public static WeekDay of(String name) {
        for (WeekDay weekDay : WeekDay.values()) {
            if (weekDay.name().equalsIgnoreCase(name) || weekDay.getCode().equalsIgnoreCase(name) || weekDay.toString().equalsIgnoreCase(name)) {
                return weekDay;
            }
        }
        return MONDAY;
    }
}
