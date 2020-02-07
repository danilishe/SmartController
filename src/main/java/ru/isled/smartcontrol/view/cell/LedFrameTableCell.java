package ru.isled.smartcontrol.view.cell;

import javafx.scene.layout.Background;
import ru.isled.smartcontrol.model.LedFrame;

public class LedFrameTableCell extends javafx.scene.control.TableCell<LedFrame, Background> {
    @Override
    public void updateSelected(boolean selected) {
        super.updateSelected(selected);
    }

    @Override
    protected void updateItem(Background item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            setStyle("");
            setText(null);
            return;
        }
        this.setStyle("-fx-background-color: yellow");
        this.setBackground(item);
    }

}
