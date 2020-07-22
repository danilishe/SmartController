package ru.isled.smartcontrol.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ScheduledCommand {
    final public StringProperty commandProperty;
    final public StringProperty weekDayProperty;
    final public StringProperty timeProperty;
    private Command command;
    private WeekDay weekDay;
    private int hours;
    private int minutes;

    public ScheduledCommand() {
        commandProperty = new SimpleStringProperty();
        weekDayProperty = new SimpleStringProperty();
        timeProperty = new SimpleStringProperty();
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public Command getCommand() {
        return command;
    }

    public ScheduledCommand setCommand(Command command) {
        this.command = command;
        commandProperty.setValue(command.toString());
        return this;
    }

    public WeekDay getWeekDay() {
        return weekDay;
    }

    public ScheduledCommand setWeekDay(WeekDay weekDay) {
        this.weekDay = weekDay;
        this.weekDayProperty.setValue(weekDay.toString());
        return this;
    }

    public ScheduledCommand setTime(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
        timeProperty.setValue(getTime());
        return this;
    }

    @Override
    public String toString() {
        if (getWeekDay() == WeekDay.EVERY_DAY) return String.format("%s: %02d:%02d", getCommand().getCode(), hours, minutes);
        return String.format("%s: %02d:%02d %s", getCommand().getCode(), hours, minutes, getWeekDay().getCode());
    }

    public String getTime() {
        return String.format("%02d:%02d", hours, minutes);
    }

}
