package ru.isled.smartcontrol.model.wraps;

import ru.isled.smartcontrol.model.Pixel;
import ru.isled.smartcontrol.model.effect.RgbMode;

import java.util.List;

public class PixelWrapper {
    private List<PixelFrameWrapper> frames;
    private RgbMode rgbMode;
    private int quantifier;
    private int number;

    public List<PixelFrameWrapper> getFrames() {
        return frames;
    }

    public PixelWrapper setFrames(List<PixelFrameWrapper> frames) {
        this.frames = frames;
        return this;
    }

    public RgbMode getRgbMode() {
        return rgbMode;
    }

    public PixelWrapper setRgbMode(RgbMode rgbMode) {
        this.rgbMode = rgbMode;
        return this;
    }

    public int getQuantifier() {
        return quantifier;
    }

    public PixelWrapper setQuantifier(int quantifier) {
        this.quantifier = quantifier;
        return this;
    }

    public int getNumber() {
        return number;
    }

    public PixelWrapper setNumber(int number) {
        this.number =  number;
        return this;
    }
}
