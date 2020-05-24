package ru.isled.smartcontrol.model.effect.multiframe;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Контейнер для структурных эффектов, содержит сведения об эффекте в упрощённом виде, без цвета и все кадры эффекта
 */
@Getter
public class MultiFrameEffect {
    private final List<List<MicroPixel>> frames;
    private final int width;

    public MultiFrameEffect(int width) {
        frames = new ArrayList<>();
        this.width = width;
    }

    public int length() {
        return frames.size();
    }

    public MultiFrameEffect addFrame(List<MicroPixel> frame) {
        if (frame.size() != width) throw new IllegalArgumentException("Ширина эффекта должна быть ровно " + width + "!");
        frames.add(frame);
        return this;
    }
}
