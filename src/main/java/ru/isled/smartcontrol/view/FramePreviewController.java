package ru.isled.smartcontrol.view;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import ru.isled.smartcontrol.model.LedFrame;

import java.util.ArrayList;
import java.util.List;

import static ru.isled.smartcontrol.Constants.DEFAULT_PIXEL_COUNT;
import static ru.isled.smartcontrol.Constants.MAX_CHANNELS_COUNT;


public class FramePreviewController {
    private final MainController mainController;
    private HBox previewZone;
    private List<Shape> previewPixels = new ArrayList<>(MAX_CHANNELS_COUNT);

    public FramePreviewController(MainController mc) {
        mainController = mc;
    }

    public void init(HBox previewZone) {
        this.previewZone = previewZone;

        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {
            Shape pixel = new Rectangle(20, 20, Color.BLACK);
            Text pixelText = new Text("" + (i + 1));
            StackPane stack = new StackPane(pixel, pixelText);
            pixel.setStroke(Color.BLACK);
            pixel.setStrokeWidth(0.7);

            if (i >= DEFAULT_PIXEL_COUNT) stack.setVisible(false);
            previewPixels.add(pixel);
            previewZone.getChildren().add(stack);
        }
    }

    public void show(int pixelQuantity) {
        for (int i = 0; i < previewPixels.size(); i++) {
            boolean expectedVisibility = i < pixelQuantity;
            if (previewPixels.get(i).isVisible() != expectedVisibility)
                previewPixels.get(i).setVisible(expectedVisibility);
        }
    }


    protected void previewFrame(LedFrame frame) {
//        frame.cyclesProperty()
//        previewZone.setPreview(project.getframe.getSubFrames())
//        for (int i = 0; i < pixelSpinner.getValue(); i++) {
//
//            Shape pixel = previewPixels.get(i);
//
//            pixel.getStyleClass().clear();
//            if (frame.getPixelValue(i) <= MAX_BRIGHT) {
//                pixel.getStyleClass().clear();
//                pixel.scaleYProperty().set(1 + .1 * project.getPixels().get(i));
//                pixel.fillProperty().setValue(
//                        Color.rgb(0xFF, 0xFF, 0, ((double) frame.getPixelValue(i) / MAX_BRIGHT)));
//            } else {
////                pixel.fillProperty().setValue(
////                        null
////                );
//
//                if (pixel.getStyleClass().isEmpty())
//                    pixel.getStyleClass().add(PixelEffect.cssByIndex(frame.getPixelValue(i)));
//                else
//                    pixel.getStyleClass().set(0, PixelEffect.cssByIndex(frame.getPixelValue(i)));
//
//            }
    }
}


