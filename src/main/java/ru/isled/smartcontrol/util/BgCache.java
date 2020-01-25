package ru.isled.smartcontrol.util;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class BgCache {
    public static final BgCache INSTANCE = new BgCache();
    private final Map<Color, Background> cache = new HashMap<>();
    private Background background;

    public Background get(Color color) {
        if (cache.containsKey(color))
            return cache.get(color);
        else {
            background = new Background(new BackgroundFill(color, null, null));
            cache.put(color, background);
            return background;
        }
    }
}
