package ru.isled.smartcontrol.model.effect.multiframe;

import javafx.scene.paint.Color;
import ru.isled.smartcontrol.model.BlendMode;
import ru.isled.smartcontrol.model.Direction;

public class EffectParameters {
    private Direction direction;
    private int width;
    private int traceBefore;
    private int traceAfter;
    private int blockWidth;
    private boolean autoAdjustLength;
    private Color mainColor;
    private Color bgColor;
    private BlendMode blendMode;

    public Direction getDirection() {
        return direction;
    }

    public EffectParameters setDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public EffectParameters setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getTraceBefore() {
        return traceBefore;
    }

    public EffectParameters setTraceBefore(int traceBefore) {
        this.traceBefore = traceBefore;
        return this;
    }

    public int getTraceAfter() {
        return traceAfter;
    }

    public EffectParameters setTraceAfter(int traceAfter) {
        this.traceAfter = traceAfter;
        return this;
    }

    public int getBlockWidth() {
        return blockWidth;
    }

    public EffectParameters setBlockWidth(int blockWidth) {
        this.blockWidth = blockWidth;
        return this;
    }

    public boolean isAutoAdjustLength() {
        return autoAdjustLength;
    }

    public EffectParameters setAutoAdjustLength(boolean autoAdjustLength) {
        this.autoAdjustLength = autoAdjustLength;
        return this;
    }

    public Color getMainColor() {
        return mainColor;
    }

    public EffectParameters setMainColor(Color mainColor) {
        this.mainColor = mainColor;
        return this;
    }

    public Color getBgColor() {
        return bgColor;
    }

    public EffectParameters setBgColor(Color bgColor) {
        this.bgColor = bgColor;
        return this;
    }

    public BlendMode getBlendMode() {
        return blendMode;
    }

    public EffectParameters setBlendMode(BlendMode blendMode) {
        this.blendMode = blendMode;
        return this;
    }
}
