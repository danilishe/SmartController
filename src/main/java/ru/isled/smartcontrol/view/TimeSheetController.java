package ru.isled.smartcontrol.view;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.isled.smartcontrol.Constants;
import ru.isled.smartcontrol.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.isled.smartcontrol.Constants.*;

public class TimeSheetController {
    public static final String SCHEDULED_COMMAND_REGEXP = "^(?<command>on|off): (?<hours>\\d\\d):(?<minutes>\\d\\d) (?<weekday>|sun|mon|tue|wed|thu|fri|sat)$";
    public static final String TIME_RECORD_REGEXP = "^time: (?<hours>\\d\\d):(?<minutes>\\d\\d) (?<weekday>sun|mon|tue|wed|thu|fri|sat)$";
    public Pane timeSettingsPane;
    Pattern timeRecord = Pattern.compile(TIME_RECORD_REGEXP);
    Pattern commandRecord = Pattern.compile(SCHEDULED_COMMAND_REGEXP);
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
    private File lastUsedDirectory = new File(DEFAULT_WORK_DIRECTORY);
    private Stage mainView;


    public void initialize() {
        sheet.setItems(FXCollections.observableList(new ArrayList<>()));
        sheet.getItems().addListener((InvalidationListener) observable -> addRecord.disableProperty().setValue(sheet.getItems().size() > MAX_TIME_RECORDS));

        sheet.getItems().add(new ScheduledCommand().setCommand(Command.OFF).setTime(22, 0).setWeekDay(WeekDay.EVERY_DAY));
        sheet.getItems().add(new ScheduledCommand().setCommand(Command.ON).setTime(15, 0).setWeekDay(WeekDay.EVERY_DAY));

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
        weekday.setSortType(TableColumn.SortType.ASCENDING);

        setHours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23));
        setHours.getValueFactory().setWrapAround(true);
        setHours.getValueFactory().setConverter(HoursConverter.INSTANCE);
        setHours.setOnScroll(e -> setHours.increment((int) e.getTextDeltaY()));

        setMinutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));
        setMinutes.getValueFactory().setWrapAround(true);
        setMinutes.getValueFactory().setConverter(MinutesConverter.INSTANCE);
        setMinutes.setOnScroll(e -> setMinutes.increment((int) e.getTextDeltaY()));

        hours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23));
        hours.getValueFactory().setWrapAround(true);
        hours.getValueFactory().setConverter(HoursConverter.INSTANCE);
        hours.setOnScroll(e -> hours.increment((int) e.getTextDeltaY()));

        minutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59));
        minutes.getValueFactory().setWrapAround(true);
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

        timeSettingsPane.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.INSERT)
                addRecord();
            else if (e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE)
                removeSelected();
        });
    }

    public void removeSelected() {
        sheet.getItems().remove(sheet.getSelectionModel().getSelectedItem());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!setTimeGroup.isDisable()) {
            stringBuilder
                    .append("time: ")
                    .append(HoursConverter.INSTANCE.toString(setHours.getValue()))
                    .append(":")
                    .append(MinutesConverter.INSTANCE.toString(setMinutes.getValue()))
                    .append(" ")
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
        fileChooser.setInitialDirectory(lastUsedDirectory);
        fileChooser.setTitle("Сохранить настройки времени");

        fileChooser.getExtensionFilters().setAll(Constants.TIME_CFG);
        File timeCfg = fileChooser.showSaveDialog(mainView);
        if (timeCfg == null) return;
        try {
            Files.write(timeCfg.toPath(), toString().getBytes());
            lastUsedDirectory = timeCfg.getParentFile();
        } catch (IOException e) {
            Dialogs.showErrorAlert("Ошибка при записи файла: \n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void importSettings() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(DEFAULT_TIME_FILE_NAME);
        fileChooser.setInitialDirectory(lastUsedDirectory);
        fileChooser.setTitle("Импорт настроек времени контроллера ISLed");

        fileChooser.getExtensionFilters().setAll(Constants.TIME_CFG);
        File timeCfg = fileChooser.showOpenDialog(mainView);
        if (timeCfg == null) return;
        try {
            List<String> lines = Files.readAllLines(timeCfg.toPath());
            lastUsedDirectory = timeCfg.getParentFile();
            sheet.getItems().clear();
            for (String line : lines) {
                if (line.matches(TIME_RECORD_REGEXP)) {
                    Matcher matcher = timeRecord.matcher(line);
                    matcher.find();
                    setTime(
                            HoursConverter.INSTANCE.fromString(matcher.group("hours")),
                            MinutesConverter.INSTANCE.fromString(matcher.group("minutes")),
                            WeekDay.of(matcher.group("weekday")).ordinal()
                    );
                } else if (line.matches(SCHEDULED_COMMAND_REGEXP)) {
                    if (sheet.getItems().size() > MAX_TIME_RECORDS) break;
                    Matcher matcher = commandRecord.matcher(line);
                    matcher.find();
                    sheet.getItems().add(
                            new ScheduledCommand()
                                    .setWeekDay(WeekDay.of(matcher.group("weekday")))
                                    .setTime(
                                            HoursConverter.INSTANCE.fromString(matcher.group("hours")),
                                            MinutesConverter.INSTANCE.fromString(matcher.group("minutes"))
                                    )
                                    .setCommand(Command.of(matcher.group("command")))
                    );
                }
            }

        } catch (IOException e) {
            Dialogs.showErrorAlert("Ошибка при загрузке: \n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCurrentTime() {
        setTime(LocalTime.now().getHour(), LocalTime.now().getMinute(), LocalDate.now().getDayOfWeek().ordinal());
    }

    private void setTime(int hour, int minute, int dayOfWeek) {
        setHours.getValueFactory().setValue(hour);
        setMinutes.getValueFactory().setValue(minute);
        setWeekday.getSelectionModel().select(dayOfWeek);
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

    public void setMainWindow(Stage view) {
        mainView = view;
    }
}
