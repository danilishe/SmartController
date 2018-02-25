package ru.isled.controlit.model;

import javafx.scene.control.TableCell;

import static ru.isled.controlit.Constants.MAX_FRAME_LENGTH;

public class LedFrameLengthCell extends TableCell<LedFrame, Integer> {
    @Override
    protected void updateItem(Integer item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setText(null);
            setStyle("");
        } else {
            int height = 25 + (int) (100d / MAX_FRAME_LENGTH * item);
            setStyle("-fx-min-height: " + height + "px;" +
                    "-fx-pref-height: " + height + "px;" +
                    "-fx-max-height: " + height + "px;");
            setText(item > 999 ? String.format("%.01fс", ((double) item / 1000)) : item.toString());
        }
    }
}