package ru.isled.controlit.model;

import javafx.scene.control.TableCell;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import static ru.isled.controlit.Constants.MAX_BRIGHT;

public class LedFrameTableCell extends javafx.scene.control.TableCell<LedFrame, Integer> {

        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);

            // данный иф необходим для удаления артефакто
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
            } else {
//                if (showBright.isSelected()) {
//                    double brightness = (double) item / MAX_BRIGHT;
//                    Shape fig = new Circle(10, Color.color(1, 1, 0, brightness));
//                    fig.setStroke(Color.BLACK);
//                    fig.setStrokeWidth(0.5);
//                    setGraphic(fig);
//                } else {
//                    setGraphic(null);
//                }

//                if (showDigits.isSelected())
                if (item <= MAX_BRIGHT) {
                    setText(item.toString());
                } else {
                    setText(PixelEffect.byIndex(item).name());
                }
//                else
//                    setText(null);
            }
        }

}
