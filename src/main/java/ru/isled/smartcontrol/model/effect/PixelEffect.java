package ru.isled.smartcontrol.model.effect;

import javafx.scene.paint.Color;

import java.security.InvalidParameterException;
import java.util.Arrays;

import static ru.isled.smartcontrol.Constants.*;

public enum PixelEffect {
    FadeIn(12345, "fadeIn") {
        @Override
        protected String effectOverlay() {
            return "linear-gradient(from 0% 0% to 0% 100%, " +
                    "#000f 0%, " +
                    "#0000 100%" +
                    ")";
        }

        @Override
        public Color[] interpolate(int iterations, Color start, Color end) {
            return PixelEffect.Solid.interpolate(
                    iterations,
                    Color.hsb(start.getHue(), start.getSaturation(), 0),
                    end);
        }
    },

    FadeOutIn(53135, "fadeOutIn") {
        @Override
        protected String effectOverlay() {
            return "linear-gradient(from 0% 0% to 0% 100%, " +
                    "#0000 1%, black 50%, #0000 100%" +
                    ")";
        }

        @Override
        public Color[] interpolate(int iterations, Color start, Color end) {
            int middle = iterations / 2;
            final Color middleColor = start.interpolate(end, .5);
            final Color[] fadeOut = FadeOut.interpolate(middle + 1, start, middleColor);
            final Color[] fadeIn = FadeIn.interpolate(iterations - middle, middleColor, end);
            Color[] colors = Arrays.copyOf(fadeOut, iterations);
            for (Color i : fadeIn) {
                colors[middle++] = i;
            }
            return colors;
        }
    },
    FadeInOut(13531, "fadeInOut") {
        @Override
        protected String effectOverlay() {
            return "linear-gradient(from 0% 0% to 0% 100%, " +
                    "black 0%, #0000 50%, black 100%" +
                    ")";
        }

        @Override
        public Color[] interpolate(int iterations, Color start, Color end) {
            int middle = iterations / 2;
            final Color middleColor = start.interpolate(end, .5);
            final Color[] fadeIn = FadeIn.interpolate(middle + 1, start, middleColor);
            final Color[] fadeOut = FadeOut.interpolate(iterations - middle, middleColor, end);
            Color[] colors = Arrays.copyOf(fadeIn, iterations);
            for (Color i : fadeOut) {
                colors[middle++] = i;
            }
            return colors;
        }
    },


    FadeOut(54321, "fadeOut") {
        @Override
        protected String effectOverlay() {
            return "linear-gradient(from 0% 0% to 0% 100%, " +
                    "#0000 0%, black 100%" +
                    ")";
        }

        @Override
        public Color[] interpolate(int iterations, Color start, Color end) {
            return PixelEffect.Solid.interpolate(iterations, start,
                    Color.hsb(end.getHue(), end.getSaturation(), MIN_BRIGHT));
        }
    },

    BlinkingFadeIn(19395, "mFadeIn") {
        @Override
        protected String effectOverlay() {
            return "linear-gradient(from 0% 0% to 0% 100%, " +
                    "#000f 0%," +
                    "#000e 10%, #0000 15%, #000e 17%," +
                    "#000c 20%, #0000 25%, #000c 27%," +
                    "#000a 30%, #0000 35%, #000a 37%," +
                    "#0009 40%, #0000 45%, #0009 47%," +
                    "#0007 60%, #000f 65%, #0007 67%," +
                    "#0006 70%, #000f 75%, #0006 77%," +
                    "#0003 80%, #000f 85%, #0003 87%," +
                    "#0001 90%, #000f 95%, #0001 97%," +
                    "#0000 100%" +
                    ")";
        }

        @Override
        public Color[] interpolate(int iterations, Color start, Color end) {
            Color[] colors = FadeIn.interpolate(iterations, start, end);
            for (int i = 0; i < iterations; i++) {
                if (i % 4 == 0) {
                    colors[i] = i < iterations / 2
                            ? Color.hsb(colors[i].getHue(), colors[i].getSaturation(), MAX_BRIGHT)
                            : Color.hsb(colors[i].getHue(), colors[i].getSaturation(), MIN_BRIGHT);
                }
            }
            return colors;
        }
    },
    BlinkingFadeOut(59391, "mFadeOut") {
        @Override
        protected String effectOverlay() {
            return "linear-gradient(from 0% 0% to 0% 100%, " +
                    "#0000 0%," +
                    "#0001 10%, #000f 14%, #0001 17%," +
                    "#0002 20%, #000f 24%, #0002 27%," +
                    "#0004 30%, #000f 34%, #0004 37%," +
                    "#0005 40%, #000f 44%, #0005 47%," +
                    "#0009 60%, #0000 64%, #0009 67%," +
                    "#000a 70%, #0000 74%, #000a 77%," +
                    "#000c 80%, #0000 84%, #000c 87%," +
                    "#000e 90%, #0000 94%, #000e 97%," +
                    "#000f 100%" +
                    ")";
        }

        @Override
        public Color[] interpolate(int iterations, Color start, Color end) {
            Color[] colors = FadeOut.interpolate(iterations, start, end);
            for (int i = 0; i < iterations; i++) {
                if (i % 4 == 0) {
                    colors[i] = i < iterations / 2
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
        protected String effectOverlay() {
            return "linear-gradient(from 0% 0% to 0% 100%, " +
                    "#0000 09%, black 10%,black 20%, #0000 21%," +
                    "#0000 29%, black 30%,black 40%, #0000 41%," +
                    "#0000 49%, black 50%,black 60%, #0000 61%," +
                    "#0000 69%, black 70%,black 80%, #0000 81%," +
                    "#0000 89%, black 90%" +
                    ")";
        }

        @Override
        public Color[] interpolate(int iterations, Color start, Color end) {
            if (iterations < 3) return new Color[]{Color.hsb(start.getHue(), start.getSaturation(), 0), end};
            final Color[] colors = PixelEffect.Solid.interpolate(iterations, start, end);
            int step = iterations * BASE_FRAME_LENGTH > VISIBLE_PERIOD
                    ? VISIBLE_PERIOD / BASE_FRAME_LENGTH  // переключение в с видимым мерцанием
                    : 1; // переключение с минимально возможной скоростью = MIN_FRAME_LENGTH
            int phaseChanger = 0;
            boolean darkPhase = true;
            for (int i = 0; i < iterations; i++) {
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
        protected String effectOverlay() {
            return "linear-gradient(from 0% 0% to 0% 100%, " +
                    "#0001 0%,  #0001 5%," +
                    "#000b 5%,  #000b 15%," +
                    "#000c 15%, #000c 35%," +
                    "#000e 35%, #000e 45%," +
                    "#0000 45%, #0000 55%," +
                    "#0006 55%, #0006 65%," +
                    "#0005 65%, #0005 75%," +
                    "#000a 75%, #000a 85%," +
                    "#0007 85%, #0007 95%," +
                    "#0009 95%, #0009 100%" +
                    ")";
        }

        @Override
        public Color[] interpolate(int iterations, Color start, Color end) {
            final Color[] colors = PixelEffect.Solid.interpolate(iterations, start, end);
            final int changeBrightTimeThreshold = VISIBLE_PERIOD / iterations;
            int counter = changeBrightTimeThreshold;
            double currentBright = 0;
            for (int i = 0; i < iterations; i++) {
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
        public Color[] interpolate(int iterations, Color start, Color end) {
            final Color[] colors = new Color[iterations];
            for (int i = 0; i < iterations; i++) {
                colors[i] = start.interpolate(end, (double) i / iterations);
            }
            return colors;
        }

        @Override
        protected String effectOverlay() {
            return "#0000";
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
     * Возвращает видимую на дисплее интерполяцию эффекта
     *
     * @param iterations количество шагов за которое закончится кадр
     * @param start      начальный цвет
     * @param end        конечный цвет
     * @return интерполяцию цветового ряда в зависимости от выбранного эфекта длиной iterations
     */
    public abstract Color[] interpolate(int iterations, Color start, Color end);

    public String overlay = effectOverlay();

    protected abstract String effectOverlay();
}
