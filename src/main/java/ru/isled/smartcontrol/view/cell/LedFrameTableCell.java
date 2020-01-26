package ru.isled.smartcontrol.view.cell;

import ru.isled.smartcontrol.model.LedFrame;
import ru.isled.smartcontrol.model.Pixel;

public class LedFrameTableCell extends javafx.scene.control.TableCell<LedFrame, Pixel.Frame> {
    private final Pixel parentPixel;

    public LedFrameTableCell(Pixel pixel) {
        super();
        parentPixel = pixel;
    }

    @Override
    protected void updateItem(Pixel.Frame item, boolean empty) {
        super.updateItem(item, empty);
//        styleProperty().set(parentPixel.getFrameStyle(item.getNumber()));
    }

}
