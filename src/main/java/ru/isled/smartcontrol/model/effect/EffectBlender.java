package ru.isled.smartcontrol.model.effect;

import javafx.scene.paint.Color;
import ru.isled.smartcontrol.model.Area;
import ru.isled.smartcontrol.model.BlendMode;
import ru.isled.smartcontrol.model.Pixel;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.model.effect.multiframe.EffectParameters;
import ru.isled.smartcontrol.model.effect.multiframe.MicroPixel;
import ru.isled.smartcontrol.model.effect.multiframe.MultiFrameEffect;

public class EffectBlender {
    public static void blend(Project project, MultiFrameEffect effect, Area area, EffectParameters params) {
        int lastFrame = area.getY2();
        if (params.isAutoAdjustLength() && effect.length() > project.framesCount() - area.getY1()) {
            lastFrame = area.getY1() + effect.length();
            project.setFramesCount(lastFrame);
        }
        for (int y = area.getY1(); y < lastFrame; y++) {
            int x = area.getX1();
            for (MicroPixel microPixel : effect.getFrames().get(y - area.getY1())) {
                blend(project.getPixelFrame(y, x), microPixel, params.getMainColor(), params.getBgColor(), params.getBlendMode());
                x++;
            }
        }
    }

    private static void blend(Pixel.Frame pixelFrame, MicroPixel microPixel, Color mainColor, Color bgColor, BlendMode blendMode) {
        final Color first = bgColor.interpolate(mainColor, (double) microPixel.getFirst() / 255);
        final Color second = bgColor.interpolate(mainColor, (double) microPixel.getSecond() / 255);
        switch (blendMode) {
            case EFFECT:
                pixelFrame.setColor(
                        microPixel.getFirst() != 0 ? first : null,
                        microPixel.getSecond() != 0 ? second : null
                );
                break;
            case EFFECT_ON_EMPTY:
                if (microPixel.getFirst() > 0 && pixelFrame.getStartColor().equals(Color.BLACK))
                    pixelFrame.setColor(first, null);
                if (microPixel.getSecond() > 0 && pixelFrame.getEndColor().equals(Color.BLACK))
                    pixelFrame.setColor(null, second);
                break;
            case ON_EMPTY:
                if (pixelFrame.getStartColor().equals(Color.BLACK)) pixelFrame.setColor(first, null);
                if (pixelFrame.getEndColor().equals(Color.BLACK)) pixelFrame.setColor(null, second);
                break;
            case EFFECT_ON_FULL:
                if (microPixel.getFirst() > 0 && !pixelFrame.getStartColor().equals(Color.BLACK))
                    pixelFrame.setColor(first, null);
                if (microPixel.getSecond() > 0 && !pixelFrame.getEndColor().equals(Color.BLACK))
                    pixelFrame.setColor(null, second);
                break;
            case ON_FULL:
                if (!pixelFrame.getStartColor().equals(Color.BLACK)) pixelFrame.setColor(first, null);
                if (!pixelFrame.getEndColor().equals(Color.BLACK)) pixelFrame.setColor(null, second);
                break;
            case FULL_BLEND:
            default:
                pixelFrame.setColor(first, second);
        }
    }

    public static void applyColor(Project project, MultiFrameEffect effect, Area area, EffectParameters params) {
        // todo
    }
}
