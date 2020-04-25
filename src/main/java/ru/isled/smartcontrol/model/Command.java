package ru.isled.smartcontrol.model;

public enum Command {
    ON("Включить"),
    OFF("Выключить");

    private final String description;

    Command(String description) {
        this.description = description;
    }

    public String getCode() {
        return name().toLowerCase();
    }

    @Override
    public String toString() {
        return description;
    }
}
