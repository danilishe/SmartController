package ru.isled.smartcontrol.view.cell;

import javafx.beans.property.BooleanProperty;
import ru.isled.smartcontrol.model.LedFrame;
import ru.isled.smartcontrol.model.effect.PixelEffect;

import static ru.isled.smartcontrol.Constants.DEFAULT_CELL_STYLE;
import static ru.isled.smartcontrol.Constants.MAX_BRIGHT;

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
                if (item <= MAX_BRIGHT) {
                    int brightPerc = item * 100 / MAX_BRIGHT;
                    styleProperty().setValue(DEFAULT_CELL_STYLE + "   -fx-background-size:"+brightPerc+"% 100%;");
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
