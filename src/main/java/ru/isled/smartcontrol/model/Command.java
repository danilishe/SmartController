package ru.isled.smartcontrol.model;

public enum Command {
    ON("Включить"),
    OFF("Выключить");

    private final String description;

    Command(String description) {
        this.description = description;
    }

    public static Command of(String command) {
        for (Command value : Command.values()) {
            if (command.equalsIgnoreCase(value.getCode()) || command.equalsIgnoreCase(value.toString())) {
                return value;
            }
        }
        return Command.ON;
    }

    public String getCode() {
        return name().toLowerCase();
    }

    @Override
    public String toString() {
        return description;
    }
}
