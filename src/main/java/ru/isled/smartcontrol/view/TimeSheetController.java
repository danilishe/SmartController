package ru.isled.smartcontrol.view;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import ru.isled.smartcontrol.Constants;
import ru.isled.smartcontrol.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import static ru.isled.smartcontrol.Constants.*;

public class TimeSheetController {
    public Spinner<Integer> hours;
    public Spinner<Integer> minutes;
    public ChoiceBox<WeekDay> selectedWeekday;
    public Spinner<Integer> setHours;
    public Spinner<Integer> setMinutes;
    public TableView<ScheduledCommand> sheet;
    public TableColumn<ScheduledCommand, String> command;
    public TableColumn<ScheduledCommand, String> time;
    public TableColumn<ScheduledCommand, String> weekday;

    public ChoiceBox<WeekDay> setWeekday;
    public CheckBox setTime;
    public Pane setTimeGroup;
    public RadioButton onCommand;
    public Button addRecord;
    public ToggleGroup selectedCommand;


    public void initialize() {
        sheet.setItems(FXCollections.observableList(new ArrayList<>()));
        sheet.getItems().addListener((InvalidationListener) observable -> addRecord.disableProperty().setValue(sheet.getItems().size() > MAX_TIME_RECORDS));

        sheet.getItems().add(new ScheduledCommand().setCommand(Command.OFF).setTime(22, 15).setWeekDay(WeekDay.EVERY_DAY));
        sheet.getItems().add(new ScheduledCommand().setCommand(Command.ON).setTime(15, 15).setWeekDay(WeekDay.SUNDAY));

        sheet.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;
            hours.getValueFactory().setValue(newValue.getHours());
            minutes.getValueFactory().setValue(newValue.getMinutes());
            selectedCommand.selectToggle(selectedCommand.getToggles().get(newValue.getCommand() == Command.ON ? 0 : 1));
            selectedWeekday.getSelectionModel().select(newValue.getWeekDay());
        });

        command.setCellValueFactory(c -> c.getValue().commandProperty);
        time.setCellValueFactory(c -> c.getValue().timeProperty);
        weekday.setCellValueFactory(c -> c.getValue().weekDayProperty);

        setHours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23));
        setHours.getValueFactory().setConverter(HoursConverter.INSTANCE);
        setHours.setOnScroll(e -> setHours.increment((int) e.getTextDeltaY()));

        setMinutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));
        setMinutes.getValueFactory().setConverter(MinutesConverter.INSTANCE);
        setMinutes.setOnScroll(e -> setMinutes.increment((int) e.getTextDeltaY()));

        hours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23));
        hours.getValueFactory().setConverter(HoursConverter.INSTANCE);
        hours.setOnScroll(e -> hours.increment((int) e.getTextDeltaY()));

        minutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));
        minutes.getValueFactory().setConverter(MinutesConverter.INSTANCE);
        minutes.setOnScroll(e -> minutes.increment((int) e.getTextDeltaY()));

        selectedWeekday.setItems(FXCollections.observableList(Arrays.asList(WeekDay.values())));
        selectedWeekday.getSelectionModel().selectFirst();
        selectedWeekday.setOnScroll(event -> {
            if (event.getTextDeltaY() > 0) selectedWeekday.getSelectionModel().selectPrevious();
            else selectedWeekday.getSelectionModel().selectNext();
        });

        setTimeGroup.disableProperty().bind(setTime.selectedProperty().not());
        setWeekday.setItems(FXCollections.observableList(Arrays.asList(WeekDay.values()).subList(1, 8)));
        setWeekday.setOnScroll(event -> {
            if (event.getTextDeltaY() > 0) setWeekday.getSelectionModel().selectPrevious();
            else setWeekday.getSelectionModel().selectNext();
        });
        setCurrentTime();
    }

    public void removeSelected() {
        sheet.getItems().remove(sheet.getSelectionModel().getSelectedItem());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!setTimeGroup.isDisable()) {
            stringBuilder.append("time: ").append(setHours.getValue()).append(":").append(setMinutes.getValue()).append(" ")
                    .append(setWeekday.getValue().getCode());
        }
        for (ScheduledCommand sc : sheet.getItems()) {
            stringBuilder.append("\n").append(sc.toString());
        }
        return stringBuilder.toString();
    }

    public void export() {
        if (setTimeGroup.isDisable() && sheet.getItems().isEmpty()) {
            Dialogs.warn("Нет настроек для сохранения!");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(DEFAULT_TIME_FILE_NAME);
        fileChooser.setInitialDirectory(new File(DEFAULT_WORK_DIRECTORY));
        fileChooser.setTitle("Сохранить настройки времени");

        fileChooser.getExtensionFilters().setAll(Constants.TIME_CFG);
        File timeCfg = fileChooser.showSaveDialog(null);
        if (timeCfg == null) return;
        try {
            Files.write(timeCfg.toPath(), toString().getBytes());
        } catch (IOException e) {
            Dialogs.showErrorAlert("Ошибка при записи файла: \n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCurrentTime() {
        setHours.getValueFactory().setValue(LocalTime.now().getHour());
        setMinutes.getValueFactory().setValue(LocalTime.now().getMinute());
        setWeekday.getSelectionModel().select(LocalDate.now().getDayOfWeek().ordinal());
    }

    public void addRecord() {
        sheet.getItems().add(new ScheduledCommand()
                .setCommand(onCommand.isSelected() ? Command.ON : Command.OFF)
                .setTime(hours.getValue(), minutes.getValue())
                .setWeekDay(selectedWeekday.getValue()));
    }

    public void apply() {
        ScheduledCommand selectedItem = sheet.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            selectedItem
                    .setCommand(onCommand.isSelected() ? Command.ON : Command.OFF)
                    .setTime(hours.getValue(), minutes.getValue())
                    .setWeekDay(selectedWeekday.getValue());
        }
    }
}
