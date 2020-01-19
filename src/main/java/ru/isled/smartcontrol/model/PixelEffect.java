package ru.isled.smartcontrol.model;

import javafx.scene.paint.Color;

import java.security.InvalidParameterException;
import java.util.Arrays;

import static ru.isled.smartcontrol.Constants.*;

public enum PixelEffect {
    FadeIn(12345, "fadeIn") {
        @Override
        public Color[] getColors(int baseLengthCount, Color start, Color end) {
            return PixelEffect.Solid.getColors(
                    baseLengthCount,
                    Color.hsb(start.getHue(), start.getSaturation(), 0),
                    end);
        }
    },

    FadeOutIn(53135, "fadeOutIn") {
        @Override
        public Color[] getColors(int baseLengthCount, Color start, Color end) {
            int middle = baseLengthCount / 2;
            final Color middleColor = start.interpolate(end, .5);
            final Color[] fadeOut = FadeOut.getColors(middle + 1, start, middleColor);
            final Color[] fadeIn = FadeIn.getColors(baseLengthCount - middle, middleColor, end);
            Color[] colors = Arrays.copyOf(fadeOut, baseLengthCount);
            for (Color i : fadeIn) {
                colors[middle++] = i;
            }
            return colors;
        }
    },
    FadeInOut(13531, "fadeInOut") {
        @Override
        public Color[] getColors(int baseLengthCount, Color start, Color end) {
            int middle = baseLengthCount / 2;
            final Color middleColor = start.interpolate(end, .5);
            final Color[] fadeIn = FadeIn.getColors(middle + 1, start, middleColor);
            final Color[] fadeOut = FadeOut.getColors(baseLengthCount - middle, middleColor, end);
            Color[] colors = Arrays.copyOf(fadeIn, baseLengthCount);
            for (Color i : fadeOut) {
                colors[middle++] = i;
            }
            return colors;
        }
    },


    FadeOut(54321, "fadeOut") {
        @Override
        public Color[] getColors(int baseLengthCount, Color start, Color end) {
            return PixelEffect.Solid.getColors(baseLengthCount, start,
                    Color.hsb(end.getHue(), end.getSaturation(), MIN_BRIGHT));
        }
    },

    BlinkingFadeIn(19395, "mFadeIn") {
        @Override
        public Color[] getColors(int baseLengthCount, Color start, Color end) {
            Color[] colors = FadeIn.getColors(baseLengthCount, start, end);
            for (int i = 0; i < baseLengthCount; i++) {
                if (i % 4 == 0) {
                    colors[i] = i < baseLengthCount / 2
                            ? Color.hsb(colors[i].getHue(), colors[i].getSaturation(), MAX_BRIGHT)
                            : Color.hsb(colors[i].getHue(), colors[i].getSaturation(), MIN_BRIGHT);
                }
            }
            return colors;
        }
    },
    BlinkingFadeOut(59391, "mFadeOut") {
        @Override
        public Color[] getColors(int baseLengthCount, Color start, Color end) {
            Color[] colors = FadeOut.getColors(baseLengthCount, start, end);
            for (int i = 0; i < baseLengthCount; i++) {
                if (i % 4 == 0) {
                    colors[i] = i < baseLengthCount / 2
                            ? Color.hsb(colors[i].getHue(), colors[i].getSaturation(), MIN_BRIGHT)
                            : Color.hsb(colors[i].getHue(), colors[i].getSaturation(), MAX_BRIGHT);
                }
            }
            return colors;
        }
    },
    // при мерцании главное, чтобы вспышка была не менее VISIBLE_PERIOD, иначе её сложно разглядеть как вспышку
    Blinking(90909, "blink") {
        @Override
        public Color[] getColors(int baseLengthCount, Color start, Color end) {
            if (baseLengthCount < 3) return new Color[]{Color.hsb(start.getHue(), start.getSaturation(), 0), end};
            final Color[] colors = PixelEffect.Solid.getColors(baseLengthCount, start, end);
            int step = baseLengthCount * BASE_FRAME_LENGTH > VISIBLE_PERIOD
                    ? VISIBLE_PERIOD / BASE_FRAME_LENGTH  // переключение в с видимым мерцанием
                    : 1; // переключение с минимально возможной скоростью = MIN_FRAME_LENGTH
            int phaseChanger = 0;
            boolean darkPhase = true;
            for (int i = 0; i < baseLengthCount; i++) {
                if (darkPhase) colors[i] = Color.hsb(colors[i].getHue(), colors[i].getSaturation(), 0);
                if (phaseChanger++ > step) {
                    phaseChanger = 0;
                    darkPhase = !darkPhase;
                }
            }
            return colors;
        }
    },
    Chaos(75381, "chaos") {
        @Override
        public Color[] getColors(int baseLengthCount, Color start, Color end) {
            final Color[] colors = PixelEffect.Solid.getColors(baseLengthCount, start, end);
            final int changeBrightTimeThreshold = VISIBLE_PERIOD / baseLengthCount;
            int counter = changeBrightTimeThreshold;
            double currentBright = 0;
            for (int i = 0; i < baseLengthCount; i++) {
                if (++counter > changeBrightTimeThreshold) {
                    currentBright = Math.random();
                    counter = 0;
                }
                colors[i] = Color.hsb(colors[i].getHue(), colors[i].getSaturation(), currentBright);
            }
            return colors;
        }
    },
    Solid(0, "base") {
        @Override
        public Color[] getColors(int baseLengthCount, Color start, Color end) {
            final Color[] colors = new Color[baseLengthCount];
            for (int i = 0; i < baseLengthCount; i++) {
                colors[i] = start.interpolate(end, (double) i / baseLengthCount);
            }
            return colors;
        }
    };

    public static final int VISIBLE_PERIOD = 1000 / 5;
    int code;
    String cssClass;

    PixelEffect(int code, String cssClass) {
        this.code = code;
        this.cssClass = cssClass;
    }

    public static PixelEffect byIndex(int index) {
        return Arrays.stream(values())
                .filter(e -> e.code == index)
                .findFirst()
                .orElseThrow(() -> new InvalidParameterException("Отсутствует пиксельный эффект с индексом: " + index));
    }

    public static String cssByIndex(int index) {
        return Arrays.stream(values())
                .filter(e -> e.code == index)
                .findFirst()
                .orElseThrow(() -> new InvalidParameterException("Отсутствует пиксельный эффект с индексом: " + index))
                .cssClass();
    }


    public String cssClass() {
        return cssClass;
    }

    public int index() {
        return code;
    }

    /**
     * Возвращает интерполяцию эффекта
     *
     * @param baseLengthCount количество шагов за которое закончится кадр
     * @param start           начальный цвет
     * @param end             конечный цвет
     * @return интерполяцию цветового ряда в зависимости от выбранного эфекта длиной baseLengthCount
     */
    public abstract Color[] getColors(int baseLengthCount, Color start, Color end);
}
