package ru.isled.smartcontrol.view;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ru.isled.smartcontrol.model.LedFrame;

import static ru.isled.smartcontrol.Constants.*;

public class FrameHandlersController {
    private final MainController mainController;
    private Spinner<Integer> frameLengthSpinner;
    private Spinner<Integer> frameCyclesSpinner;

    public FrameHandlersController(MainController mc) {
        mainController = mc;
    }

    public void updateHandlers(LedFrame frame) {
        frameCyclesSpinner.getValueFactory().setValue(frame.getCycles());
        frameLengthSpinner.getValueFactory().setValue(frame.getLength());
    }

    public void updateFrame(LedFrame frame) {
        frame.setCycles(frameCyclesSpinner.getValue());
        frame.setLength(frameLengthSpinner.getValue());
    }

    public void init(Spinner<Integer> frameLengthSpinner, Spinner<Integer> frameCyclesSpinner) {

        this.frameLengthSpinner = frameLengthSpinner;
        this.frameCyclesSpinner = frameCyclesSpinner;

        initLengthSpinner();
        initCyclesSpinner();
    }

    private void initCyclesSpinner() {
        frameCyclesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_CYCLES, MAX_CYCLES, 1));
        frameCyclesSpinner.getEditor().textProperty().addListener((ov, oldV, newV) -> {
            if (!newV.matches("\\d+"))
                frameCyclesSpinner.getEditor().setText(oldV);
        });
        frameCyclesSpinner.getValueFactory().valueProperty().addListener((ov, oldV, newV) -> {
            if (newV > MAX_CYCLES)
                frameCyclesSpinner.getEditor().setText("" + MAX_CYCLES);
            else if (newV < MIN_CYCLES)
                frameCyclesSpinner.getEditor().setText("" + MIN_CYCLES);

            if (newV >= MIN_CYCLES && newV <= MAX_CYCLES) {
                mainController.setCycles(newV);
            }
        });
    }

    private void initLengthSpinner() {
        frameLengthSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_FRAME_LENGTH, MAX_FRAME_LENGTH, DEFAULT_FRAME_LENGTH, FRAME_LENGTH_STEP)
        );

        // запрет ввода НЕ цифр
        frameLengthSpinner.getEditor().textProperty().addListener((ov, oldValue, newValue) -> {
            if (!newValue.matches("\\d+"))
                frameLengthSpinner.getEditor().setText(oldValue);
        });

        // отслеживает корректность значения спиннера (не текстового поля, входящего в его состав)
        frameLengthSpinner.getValueFactory().valueProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue % FRAME_LENGTH_STEP != 0)
                frameLengthSpinner.getValueFactory().setValue((newValue / FRAME_LENGTH_STEP * FRAME_LENGTH_STEP));

            if (newValue > MAX_FRAME_LENGTH)
                frameLengthSpinner.getValueFactory().setValue(MAX_FRAME_LENGTH);
            else if (newValue < MIN_FRAME_LENGTH)
                frameLengthSpinner.getValueFactory().setValue(MIN_FRAME_LENGTH);

            if (newValue % MIN_FRAME_LENGTH == 0 && newValue <= MAX_FRAME_LENGTH && newValue >= MIN_FRAME_LENGTH) {
                mainController.setLength(newValue);
            }

        });

        frameLengthSpinner.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                mainController.getColumn(HEADER_COLUMNS - 1).setVisible(false);
                frameLengthSpinner.getEditor().commitValue();
                mainController.getColumn(HEADER_COLUMNS - 1).setVisible(true);
                event.consume();
            }
        });
        // применяет значение после потери фокуса
        frameLengthSpinner.getEditor().focusedProperty().addListener((ov, o, n) -> {
            if (n == false) {
                int val = Integer.parseInt(frameLengthSpinner.getEditor().getText());
                if (val % FRAME_LENGTH_STEP != 0)
                    val = val / FRAME_LENGTH_STEP * FRAME_LENGTH_STEP;

                if (val > MAX_FRAME_LENGTH)
                    val = MAX_FRAME_LENGTH;
                else if (val < MIN_FRAME_LENGTH)
                    val = MIN_FRAME_LENGTH;

                // чётко обновляет внешний вид таблицы, так как после скрытия столбца, его значение меняется
                if (frameLengthSpinner.getValue() != val) {
                    mainController.getColumn(HEADER_COLUMNS - 1).setVisible(false);
                    frameLengthSpinner.getValueFactory().setValue(val);
                    mainController.getColumn(HEADER_COLUMNS - 1).setVisible(true);
                }
            }
        });

    }

}
