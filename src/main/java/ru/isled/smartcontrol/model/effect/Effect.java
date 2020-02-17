package ru.isled.smartcontrol.model.effect;

import javafx.scene.paint.Color;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.view.effect_controller.MultiFrameEffect;

import java.util.NoSuchElementException;

public enum Effect implements MultiFrameEffect {
    SmoothColor {
        @Override
        public String getName() {
            return "Сгладить цвет";
        }

        @Override
        public void apply(Project project, int x1, int y1, int x2, int y2) {
            int height = y2 - y1 + 1;
            if (height < 2) return;
            for (int frameNo = y1 + 1; frameNo <= y2; frameNo++) {
                for (int pixelNo = x1; pixelNo <= x2; pixelNo++) {
                    Color middleColor = project.getPixelFrame(frameNo - 1, pixelNo).getStartColor()
                            .interpolate(project.getPixelFrame(frameNo, pixelNo).getEndColor(), .5);
                    project.getPixelFrame(frameNo - 1, pixelNo).setColor(null, middleColor);
                    project.getPixelFrame(frameNo, pixelNo).setColor(middleColor, null);
                }
            }
        }
    },

    Building {
        @Override
        public String getName() {
            return "Сборка";
        }

        @Override
        public void apply(Project project, int x1, int y1, int x2, int y2) {
            int height = y2 - y1 + 1;
            if (height < 2) return;
            for (int frameNo = y1 + 1; frameNo <= y2; frameNo++) {
                for (int pixelNo = x1; pixelNo <= x2; pixelNo++) {
                    Color middleColor = project.getPixelFrame(frameNo - 1, pixelNo).getStartColor()
                            .interpolate(project.getPixelFrame(frameNo, pixelNo).getEndColor(), .5);
                    project.getPixelFrame(frameNo - 1, pixelNo).setColor(null, middleColor);
                    project.getPixelFrame(frameNo, pixelNo).setColor(middleColor, null);
                }
            }
        }
    };

    public static MultiFrameEffect selectEffect(String selectedEffect) {
        for (Effect effect : Effect.values())
            if (effect.getName().equals(selectedEffect))
                return effect;
        throw new NoSuchElementException("Нет эффекта с названием " + selectedEffect);
    }

    public abstract String getName();
}
