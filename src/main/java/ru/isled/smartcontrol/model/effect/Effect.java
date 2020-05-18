package ru.isled.smartcontrol.model.effect;

import javafx.scene.paint.Color;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.view.effect_controller.*;

import java.util.NoSuchElementException;

public enum Effect implements MultiFrameEffect {
    ColorGradient {
        @Override
        public String getName() {
            return "Цветовой перелив";
        }

        @Override
        public void apply(Project project, int x1, int y1, int x2, int y2) {
            ColorGradientController.get(project, x1, y1, x2, y2)
                    .apply();
        }
    },
    Random {
        @Override
        public String getName() {
            return "Произвольные вспышки";
        }

        @Override
        public void apply(Project project, int x1, int y1, int x2, int y2) {
            RandomEffectController.get(project, x1, y1, x2, y2)
                    .apply();
        }
    },
    SmoothColor {
        @Override
        public String getName() {
            return "Сгладить цвет";
        }

        @Override
        public void apply(Project project, int x1, int y1, int x2, int y2) {
            int height = y2 - y1 + 1;
            if (height < 2) return;
            for (int frameNo = y1 + 1; frameNo < y2; frameNo++) {
                for (int pixelNo = x1; pixelNo < x2; pixelNo++) {
                    Color middleColor = project.getPixelFrame(frameNo - 1, pixelNo).getStartColor()
                            .interpolate(project.getPixelFrame(frameNo, pixelNo).getEndColor(), .5);
                    project.getPixelFrame(frameNo - 1, pixelNo).setColor(null, middleColor);
                    project.getPixelFrame(frameNo, pixelNo).setColor(middleColor, null);
                }
            }
        }
    },

    Fill {
        @Override
        public String getName() {
            return "Наполнение";
        }

        @Override
        public void apply(Project project, int x1, int y1, int x2, int y2) {
            final FillingEffectController fillingEffectController = FillingEffectController.get(project, x1, y1, x2, y2);
            fillingEffectController.apply();
        }
    },

    Building {
        @Override
        public String getName() {
            return "Сборка";
        }

        @Override
        public void apply(Project project, int x1, int y1, int x2, int y2) {
            final BuildingEffectController buildingEffectController = BuildingEffectController.get(project, x1, y1, x2, y2);
            buildingEffectController.apply();
        }
    },

    Glare {
        @Override
        public void apply(Project project, int x1, int y1, int x2, int y2) {
            final GlareEffectController glareEffectController = GlareEffectController.get(project, x1, y1, x2, y2);
            glareEffectController.apply();
        }

        @Override
        public String getName() {
            return "Блик";
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
