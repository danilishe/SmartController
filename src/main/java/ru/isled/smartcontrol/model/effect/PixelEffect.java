package ru.isled.smartcontrol.model.effect;

import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.security.InvalidParameterException;
import java.util.Arrays;

import static ru.isled.smartcontrol.Constants.*;

public enum PixelEffect {
    FadeIn(12345, "fadeIn") {
        @Override
        protected BackgroundFill getGradient() {
            return new BackgroundFill(
                    new LinearGradient(0, 0, 0, 1, true, null,
                            new Stop(0, Color.rgb(0, 0, 0, 1)),
                            new Stop(1, Color.rgb(0, 0, 0, 0))
                    ), null, null);
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
        protected BackgroundFill getGradient() {
            return new BackgroundFill(
                    new LinearGradient(0, 0, 0, 1, true, null,
                            new Stop(0, Color.rgb(0, 0, 0, 0)),
                            new Stop(.5, Color.rgb(0, 0, 0, 1)),
                            new Stop(1, Color.rgb(0, 0, 0, 0))
                    ), null, null);
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
        protected BackgroundFill getGradient() {
            return new BackgroundFill(
                    new LinearGradient(0, 0, 0, 1, true, null,
                            new Stop(0, Color.rgb(0, 0, 0, 1)),
                            new Stop(.5, Color.rgb(0, 0, 0, 0)),
                            new Stop(1, Color.rgb(0, 0, 0, 1))
                    ), null, null);
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
        protected BackgroundFill getGradient() {
            return new BackgroundFill(
                    new LinearGradient(0, 0, 0, 1, true, null,
                            new Stop(0, Color.rgb(0, 0, 0, 0)),
                            new Stop(1, Color.rgb(0, 0, 0, 1))
                    ), null, null);
        }

        @Override
        public Color[] interpolate(int iterations, Color start, Color end) {
            return PixelEffect.Solid.interpolate(iterations, start,
                    Color.hsb(end.getHue(), end.getSaturation(), MIN_BRIGHT));
        }
    },

    BlinkingFadeIn(19395, "mFadeIn") {
        @Override
        protected BackgroundFill getGradient() {
            return new BackgroundFill(
                    new LinearGradient(0, 0, 0, 1, true, null,
                            new Stop(0, Color.rgb(0, 0, 0, 1)),

                            new Stop(.1, Color.rgb(0, 0, 0, .9)),
                            new Stop(.13, Color.rgb(0, 0, 0, 0)),
                            new Stop(.15, Color.rgb(0, 0, 0, .85)),

                            new Stop(.3, Color.rgb(0, 0, 0, .7)),
                            new Stop(.33, Color.rgb(0, 0, 0, 0)),
                            new Stop(.35, Color.rgb(0, 0, 0, .65)),

                            new Stop(.5, Color.rgb(0, 0, 0, 0.5)),
                            new Stop(.53, Color.rgb(0, 0, 0, 0)),
                            new Stop(.55, Color.rgb(0, 0, 0, 0.45)),

                            new Stop(.7, Color.rgb(0, 0, 0, .3)),
                            new Stop(.73, Color.rgb(0, 0, 0, 1)),
                            new Stop(.75, Color.rgb(0, 0, 0, .25)),

                            new Stop(.9, Color.rgb(0, 0, 0, .1)),
                            new Stop(.93, Color.rgb(0, 0, 0, 1)),
                            new Stop(.95, Color.rgb(0, 0, 0, .05)),

                            new Stop(1, Color.rgb(0, 0, 0, 0))
                    ), null, null);
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
        protected BackgroundFill getGradient() {
            return new BackgroundFill(
                    new LinearGradient(0, 0, 0, 1, true, null,
                            new Stop(1, Color.rgb(0, 0, 0, 1)),

                            new Stop(.95, Color.rgb(0, 0, 0, .9)),
                            new Stop(.93, Color.rgb(0, 0, 0, 0)),
                            new Stop(.9, Color.rgb(0, 0, 0, .85)),

                            new Stop(.75, Color.rgb(0, 0, 0, .7)),
                            new Stop(.73, Color.rgb(0, 0, 0, 0)),
                            new Stop(.7, Color.rgb(0, 0, 0, .65)),

                            new Stop(.55, Color.rgb(0, 0, 0, 0.5)),
                            new Stop(.53, Color.rgb(0, 0, 0, 0)),
                            new Stop(.5, Color.rgb(0, 0, 0, 0.45)),

                            new Stop(.35, Color.rgb(0, 0, 0, .3)),
                            new Stop(.33, Color.rgb(0, 0, 0, 1)),
                            new Stop(.3, Color.rgb(0, 0, 0, .25)),

                            new Stop(.15, Color.rgb(0, 0, 0, .1)),
                            new Stop(.13, Color.rgb(0, 0, 0, 1)),
                            new Stop(.1, Color.rgb(0, 0, 0, .05)),

                            new Stop(0, Color.rgb(0, 0, 0, 0))
                    ), null, null);
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
        protected BackgroundFill getGradient() {
            return new BackgroundFill(
                    new LinearGradient(0, 0, 0, 1, true, null,
                            new Stop(.95, Color.rgb(0, 0, 0, 0)),
                            new Stop(.93, Color.rgb(0, 0, 0, 1)),
                            new Stop(.9, Color.rgb(0, 0, 0, 0)),

                            new Stop(.75, Color.rgb(0, 0, 0, 0)),
                            new Stop(.73, Color.rgb(0, 0, 0, 1)),
                            new Stop(.7, Color.rgb(0, 0, 0, 0)),

                            new Stop(.55, Color.rgb(0, 0, 0, 0)),
                            new Stop(.53, Color.rgb(0, 0, 0, 1)),
                            new Stop(.5, Color.rgb(0, 0, 0, 0)),

                            new Stop(.35, Color.rgb(0, 0, 0, 0)),
                            new Stop(.33, Color.rgb(0, 0, 0, 1)),
                            new Stop(.3, Color.rgb(0, 0, 0, 0)),

                            new Stop(.15, Color.rgb(0, 0, 0, 0)),
                            new Stop(.13, Color.rgb(0, 0, 0, 1)),
                            new Stop(.1, Color.rgb(0, 0, 0, 0))
                    ), null, null);
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
        protected BackgroundFill getGradient() {
            return new BackgroundFill(
                    new LinearGradient(0, 0, 0, 1, true, null,
                            new Stop(0, Color.rgb(0, 0, 0, .2)),
                            new Stop(.1, Color.rgb(0, 0, 0, .0)),
                            new Stop(.2, Color.rgb(0, 0, 0, .1)),
                            new Stop(.3, Color.rgb(0, 0, 0, .9)),
                            new Stop(.4, Color.rgb(0, 0, 0, .1)),
                            new Stop(.5, Color.rgb(0, 0, 0, .7)),
                            new Stop(.6, Color.rgb(0, 0, 0, .4)),
                            new Stop(.7, Color.rgb(0, 0, 0, .1)),
                            new Stop(.8, Color.rgb(0, 0, 0, .9)),
                            new Stop(.9, Color.rgb(0, 0, 0, .3)),
                            new Stop(1, Color.rgb(0, 0, 0, .7))
                    ), null, null);
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
        protected BackgroundFill getGradient() {
            return new BackgroundFill(Color.rgb(0, 0, 0, 0), null, null);
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

    public BackgroundFill gradient = getGradient();

    protected abstract BackgroundFill getGradient();
}
