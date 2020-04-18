package ru.isled.smartcontrol.model.wraps;

import ru.isled.smartcontrol.model.effect.PixelEffect;

public class PixelFrameWrapper {
    private String startColor;
    private String endColor;
    private PixelEffect effect;

    public String getStartColor() {
        return startColor;
    }

    public PixelFrameWrapper setStartColor(String startColor) {
        this.startColor = startColor;
        return this;
    }

    public String getEndColor() {
        return endColor;
    }

    public PixelFrameWrapper setEndColor(String endColor) {
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
