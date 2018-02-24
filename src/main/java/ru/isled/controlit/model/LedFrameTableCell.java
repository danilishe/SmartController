package ru.isled.controlit.model;

import javafx.beans.property.BooleanProperty;
import ru.isled.controlit.view.MainController;

import static ru.isled.controlit.Constants.*;

public class LedFrameTableCell extends javafx.scene.control.TableCell<LedFrame, Integer> {


    private BooleanProperty isDigitsEnabled;
    private BooleanProperty isBrightEnabled;

    public LedFrameTableCell(BooleanProperty isDigitEnabled, BooleanProperty isBrightEnabled) {
        super();
        this.isBrightEnabled = isBrightEnabled;
        this.isDigitsEnabled = isDigitEnabled;
    }

    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);
        // данный иф необходим для удаления артефакто
        if (empty || item == null) {
            setText(null);
            styleProperty().setValue(DEFAULT_CELL_STYLE);
            getStyleClass().set(0, "base");

        } else {
            if (isDigitsEnabled.get()) {
                if (item <= MAX_BRIGHT)
                    setText(item.toString());
                else
                    setText(PixelEffect.byIndex(item).toString());
            } else {
                setText(null);
            }

            if (isBrightEnabled.get()) {
//                if (!getStyleClass().isEmpty()) getStyleClass().clear();
                if (item <= MAX_BRIGHT) {
                    styleProperty().setValue(DEFAULT_CELL_STYLE + "   -fx-background-size:" + item * 100/ MAX_BRIGHT + "% 100%;");
                    getStyleClass().set(0, "base");
                } else {
                    styleProperty().setValue(DEFAULT_CELL_STYLE);
                    getStyleClass().set(0, PixelEffect.cssByIndex(item));
                }
            } else {
                styleProperty().setValue(DEFAULT_CELL_STYLE);
                getStyleClass().set(0, "base");
            }
        }
    }

}
