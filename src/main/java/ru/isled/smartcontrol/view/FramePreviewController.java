package ru.isled.smartcontrol.view;

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
    private final List<Shape> previewPixels = new ArrayList<>(MAX_CHANNELS_COUNT);
    private final List<Node> pixelContainers = new ArrayList<>(MAX_CHANNELS_COUNT);
    private FramePreviewer viewer;

    public FramePreviewController(MainController mc) {
        mainController = mc;
    }

    public void init(HBox previewZone) {
        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {
            Shape pixel = new Rectangle(20, 20, Color.BLACK);
            Text pixelText = new Text("" + (i + 1));
            StackPane stack = new StackPane(pixel, pixelText);
            pixel.setStroke(Color.BLACK);
            pixel.setStrokeWidth(0.7);

            if (i >= DEFAULT_PIXEL_COUNT) stack.setVisible(false);
            previewPixels.add(pixel);
            pixelContainers.add(stack);
            previewZone.getChildren().add(stack);
        }

        viewer = new FramePreviewer(null, previewPixels);
        viewer.setDaemon(true);
        viewer.start();
    }

    public void show(int pixelQuantity) {
        for (int i = 0; i < previewPixels.size(); i++) {
            boolean expectedVisibility = i < pixelQuantity;
            if (pixelContainers.get(i).isVisible() != expectedVisibility)
                pixelContainers.get(i).setVisible(expectedVisibility);
        }
    }

    protected void previewFrame(LedFrame frame) {
        List<Color[]> interpolatedFrame = mainController.getProject().getInterpolatedFrame(frame.getNumber() - 1);
        viewer.changeProgram(interpolatedFrame);
    }
}

