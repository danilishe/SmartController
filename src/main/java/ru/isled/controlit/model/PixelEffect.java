package ru.isled.controlit.model;

import java.security.InvalidParameterException;
import java.util.Arrays;

import static ru.isled.controlit.Constants.*;

public enum PixelEffect {
    Разгорание(12345, "fadeIn") {
        @Override
        public int[] getInterpolatedPixel(int baseLengthCount) {
            int[] ints = new int[baseLengthCount];
            ints[0] = MIN_BRIGHT;
            ints[ints.length - 1] = MAX_BRIGHT;
            double step = (double) MAX_BRIGHT / baseLengthCount;
            for (int i = 1; i < ints.length - 1; i++) {
                ints[i] = (int) (ints[i - 1] + step);
            }
            return ints;
        }
    },

    Мерцание(90909, "blink") {
        @Override
        public int[] getInterpolatedPixel(int baseLengthCount) {
            int[] ints = new int[baseLengthCount];
            int step = baseLengthCount * BASE_FRAME_LENGTH > VISIBLE_PERIOD ?
                    VISIBLE_PERIOD / BASE_FRAME_LENGTH :
                    1;
            int phaseChanger = 0;
            int currentPhase = MIN_BRIGHT;
            for (int i = 0; i < ints.length; i++) {

                ints[i] = currentPhase;
                if (phaseChanger++ > step) {
                    phaseChanger = 0;
                    currentPhase = currentPhase > MIN_BRIGHT ? MIN_BRIGHT : MAX_BRIGHT;
                }
            }
            return ints;
        }
    },
    Угасание(54321, "fadeOut") {
        @Override
        public int[] getInterpolatedPixel(int baseLengthCount) {
            int[] ints = new int[baseLengthCount];
            ints[0] = MAX_BRIGHT;
            ints[ints.length - 1] = MIN_BRIGHT;
            double step = (double) MAX_BRIGHT / baseLengthCount;
            for (int i = 1; i < ints.length - 1; i++) {
                ints[i] = (int) (ints[i - 1] - step);
            }
            return ints;

        }
    },
    МерцающееРазгорание(19395, "mFadeIn") {
        @Override
        public int[] getInterpolatedPixel(int baseLengthCount) {
            int[] ints = Разгорание.getInterpolatedPixel(baseLengthCount);
            for (int i = 0; i < ints.length; i++) {
                if (i % 3 == 0) {
                    ints[i] = i > ints.length / 2 ? MIN_BRIGHT : MAX_BRIGHT;
                }
            }
            return ints;
        }
    },
    Хаос(75381, "chaos") {
        @Override
        public int[] getInterpolatedPixel(int baseLengthCount) {
            int[] ints = new int[baseLengthCount];
            int[] sample = Мерцание.getInterpolatedPixel(baseLengthCount);
            ints[0] = (int) (Math.random() * (MAX_BRIGHT + 1));
            for (int i = 1; i < ints.length; i++) {
                if (sample[i] != sample[i - 1]) {
                    ints[i] = (int) (Math.random() * (MAX_BRIGHT + 1));
                } else {
                    ints[i] = ints[i - 1];
                }
            }
            return ints;
        }
    },
    МерцающееУгасание(59391, "mFadeOut") {
        @Override
        public int[] getInterpolatedPixel(int baseLengthCount) {
            int[] ints = Угасание.getInterpolatedPixel(baseLengthCount);
            for (int i = 0; i < ints.length; i++) {
                if (i % 3 == 0) {
                    ints[i] = i < ints.length / 2 ? MIN_BRIGHT : MAX_BRIGHT;
                }
            }
            return ints;
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

    public abstract int[] getInterpolatedPixel(int baseLengthCount);
}
