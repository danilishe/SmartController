package ru.isled.smartcontrol.view.cell;

import ru.isled.smartcontrol.model.LedFrame;

public class LedFrameTableCell extends javafx.scene.control.TableCell<LedFrame, String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setStyle("");
            setText(null);
            return;
        }
        this.setStyle(item);
    }

}
