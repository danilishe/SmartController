package ru.isled.smartcontrol.model.effect.multiframe;

import javafx.scene.paint.Color;
import javafx.util.Pair;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MultiFrameEffect {
    private final List<List<Pair<Color, Color>>> frames;
    private final int width;

    public MultiFrameEffect(int width) {
        frames = new ArrayList<>();
        this.width = width;
    }

    public int length() {
        return frames.size();
    }
}
