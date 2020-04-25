package ru.isled.smartcontrol.model;

import javafx.util.StringConverter;

public class MinutesConverter extends StringConverter<Integer> {
    public static final MinutesConverter INSTANCE = new MinutesConverter();

    private MinutesConverter() {}

    @Override
    public String toString(Integer object) {
        return String.format("%02d", object);
    }

    @Override
    public Integer fromString(String string) {
        try {
            return Math.min(Integer.parseUnsignedInt(string.replaceAll("\\D", "")), 59);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
