package ru.isled.smartcontrol.view;

import javafx.beans.property.BooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import ru.isled.smartcontrol.model.LedFrame;

import java.util.ArrayList;
import java.util.List;

import static ru.isled.smartcontrol.Constants.*;


public class FramePreviewController {
    private final MainController mainController;
    private FramePreviewer viewer;

    public FramePreviewController(MainController mc) {
        mainController = mc;
    }

    public void init(List<Shape> previewPixels, BooleanProperty booleanProperty) {
        viewer = new FramePreviewer(null, previewPixels, booleanProperty);
        viewer.setDaemon(true);
        viewer.start();
    }

    protected void previewFrame(LedFrame frame) {
        List<Color[]> interpolatedFrame = mainController.getProject()
                .getInterpolatedFrame(frame.getNumber() - 1);
        viewer.changeProgram(interpolatedFrame);
    }
}

