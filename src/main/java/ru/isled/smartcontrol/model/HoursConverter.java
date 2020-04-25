package ru.isled.smartcontrol.model;

import javafx.util.StringConverter;

public class HoursConverter extends StringConverter<Integer> {
    public static final HoursConverter INSTANCE = new HoursConverter();

    private HoursConverter() { }

    @Override
    public String toString(Integer object) {
        return String.format("%02d", object);
    }

    @Override
    public Integer fromString(String string) {
        try {
            return Math.min(Integer.parseUnsignedInt(string.replaceAll("\\D", "")), 23);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
