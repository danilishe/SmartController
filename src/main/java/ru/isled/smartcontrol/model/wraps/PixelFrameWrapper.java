package ru.isled.smartcontrol.model.wraps;

import javafx.scene.paint.Color;
import ru.isled.smartcontrol.model.effect.PixelEffect;

public class PixelFrameWrapper {
    private Color startColor;
    private Color endColor;
    private PixelEffect effect;

    public Color getStartColor() {
        return startColor;
    }

    public PixelFrameWrapper setStartColor(Color startColor) {
        this.startColor = startColor;
        return this;
    }

    public Color getEndColor() {
        return endColor;
    }

    public PixelFrameWrapper setEndColor(Color endColor) {
        this.endColor = endColor;
        return this;
    }

    public PixelEffect getEffect() {
        return effect;
    }

    public PixelFrameWrapper setEffect(PixelEffect effect) {
        this.effect = effect;
        return this;
    }
}
