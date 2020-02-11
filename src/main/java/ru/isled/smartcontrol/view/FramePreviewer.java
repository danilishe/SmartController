package ru.isled.smartcontrol.view;

import javafx.beans.property.BooleanProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.util.List;

import static ru.isled.smartcontrol.Constants.MIN_FRAME_LENGTH;

public class FramePreviewer extends Thread {
    private List<Shape> previewPixels;
    private final BooleanProperty previewActive;
    private boolean change = false;
    private List<Color[]> colors;

    public FramePreviewer(List<Color[]> colors, List<Shape> previewPixels, BooleanProperty previewActive) {
        this.colors = colors;
        this.previewPixels = previewPixels;
        this.previewActive = previewActive;
    }

    @Override
    public void run() {
        while (!interrupted()) {
            change = false;
            try {
                if (!previewActive.getValue()) {
                    // setting first subframe
                    for (int i = 0; i < colors.size(); i++) {
                        previewPixels.get(i).setFill(colors.get(i)[0]);
                    }
                    while (!change) {
                        sleep(500);
                    }
                }
                if (colors == null) sleep(1_000);
                else {
                    for (int i = 0; i < colors.get(0).length; i++) {
                        for (int j = 0; j < colors.size(); j++) {
                            previewPixels.get(j).setFill(colors.get(j)[i]);
                        }
                        if (change) break;
                        sleep(MIN_FRAME_LENGTH);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    sleep(500);
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    public void changeProgram(List<Color[]> colors) {
        if (colors.equals(this.colors)) return;
        change = true;
        this.colors = colors;
    }


}