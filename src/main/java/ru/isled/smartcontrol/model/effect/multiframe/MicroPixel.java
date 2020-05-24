package ru.isled.smartcontrol.model.effect.multiframe;

/**
 * Контейнер для структурных эффектов (яркостных)
 */
public class MicroPixel {
    private final int frame;
    public static final MicroPixel EMPTY = new MicroPixel(0, 0);
    public static final MicroPixel FULL = new MicroPixel(255, 255);

    public static MicroPixel get(int both) {
        if (both == 0) return EMPTY;
        if (both == 255) return FULL;
        return get(both, both);
    }

    public static MicroPixel get(int first, int second) {
        if (first == second) {
            if (first == 0) return EMPTY;
            if (first == 255) return FULL;
        }
        return new MicroPixel(first, second);
    }

    private MicroPixel(int first, int second) {
        frame = first & 255 | (second & 255) << 8;
    }

    public int getFirst() {
        return frame & 255;
    }

    public int getSecond() {
        return (frame >>> 8) & 255;
    }

    public int getMiddle() {
        return (getFirst() + getSecond()) / 2;
    }

    @Override
    public String toString() {
        return Integer.toString(getMiddle());
    }
}
