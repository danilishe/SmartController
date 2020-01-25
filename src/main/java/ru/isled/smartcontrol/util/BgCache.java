package ru.isled.smartcontrol.util;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;

import java.util.HashMap;
import java.util.Map;

public class BgCache {
    public static final BgCache INSTANCE = new BgCache();
    private final Map<Paint, Background> cache = new HashMap<>();
    private Background background;

    public Background get(Paint paint) {
        if (cache.containsKey(paint))
            return cache.get(paint);
        else {
            background = new Background(new BackgroundFill(paint, null, null));
            cache.put(paint, background);
            return background;
        }
    }
}
