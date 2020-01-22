package ru.isled.smartcontrol.model;

import javafx.scene.paint.Color;

import static ru.isled.smartcontrol.Constants.MAX_BRIGHT;

/**
 * Frame Pixel is logical unit of each frame of program. Each Frame Pixel knows only own colors and effect
 */
public class FramePixel {
    private Color startColor;
    private Color endColor;
    private PixelEffect effect;

    public FramePixel(Color startColor, Color endColor, PixelEffect effect) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.effect = effect;
    }

    public FramePixel(Color oneColor, PixelEffect effect) {
        this.startColor = oneColor;
        this.endColor = oneColor;
        this.effect = effect;
    }

    public FramePixel setColor(Color startColor, Color endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
        return this;
    }

    public FramePixel setColor(Color oneColor) {
        startColor = oneColor;
        endColor = oneColor;
        return this;
    }

    public Color getStartColor() {
        return startColor;
    }

    public Color getEndColor() {
        return endColor;
    }

    public PixelEffect getEffect() {
        return effect;
    }

    public FramePixel setEffect(PixelEffect effect) {
        this.effect = effect;
        return this;
    }

    public FramePixel setBright(double bright) {
        startColor = Color.hsb(startColor.getHue(), startColor.getSaturation(), bright);
        startColor = Color.hsb(endColor.getHue(), endColor.getSaturation(), bright);
        return this;
    }

    public FramePixel setBright(int bright) {
        return setBright((double) bright / MAX_BRIGHT);
    }

    public Color[] getInterpolated(int frameLength) {
        return effect.interpolate(frameLength, getStartColor(), getEndColor());
    }
}
