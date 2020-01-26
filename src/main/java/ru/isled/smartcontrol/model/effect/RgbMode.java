package ru.isled.smartcontrol.model.effect;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import static ru.isled.smartcontrol.Constants.MAX_BRIGHT;

public enum RgbMode {
    RGB {
        @Override
        byte[] exportChannels(Color color) {
            return new byte[]{
                    toByte(color.getRed()),
                    toByte(color.getGreen()),
                    toByte(color.getBlue()),
            };
        }
    },
    RBG {
        @Override
        byte[] exportChannels(Color color) {
            return new byte[]{
                    toByte(color.getRed()),
                    toByte(color.getBlue()),
                    toByte(color.getGreen()),
            };
        }
    },
    GBR {
        @Override
        byte[] exportChannels(Color color) {
            return new byte[]{
                    toByte(color.getGreen()),
                    toByte(color.getBlue()),
                    toByte(color.getRed()),
            };
        }
    },
    GRB {
        @Override
        byte[] exportChannels(Color color) {
            return new byte[]{
                    toByte(color.getGreen()),
                    toByte(color.getRed()),
                    toByte(color.getBlue()),
            };
        }
    },
    BGR {
        @Override
        byte[] exportChannels(Color color) {
            return new byte[]{
                    toByte(color.getBlue()),
                    toByte(color.getGreen()),
                    toByte(color.getRed()),
            };
        }
    },
    BRG {
        @Override
        byte[] exportChannels(Color color) {
            return new byte[]{
                    toByte(color.getBlue()),
                    toByte(color.getRed()),
                    toByte(color.getGreen()),
            };
        }
    },
    MONO_WHITE {
        @Override
        byte[] exportChannels(Color color) {
            return new byte[]{
                    toByte(color.getBrightness())
            };
        }

        @Override
        public Background getBackground() {
            return new Background(new BackgroundFill(Color.WHITE, null, null));
        }

        @Override
        public Color getVisibleColor(Color startColor) {
            return startColor.grayscale();
        }
    };

    private static byte toByte(double bright) {
        return (byte) (bright * MAX_BRIGHT);
    }

    abstract byte[] exportChannels(Color color);

    public Color[] getColors() {
        Color[] colors = new Color[3];
        for (int i = 0; i < name().length(); i++) {
            colors[i] = getColorByLetter(name().charAt(i));
        }
        return colors;
    }

    // for rgb colors
    public Background getBackground() {
        final Color[] colors = getColors();
        return new Background(new BackgroundFill(
                new LinearGradient(0, 0,
                        1, 0,
                        true, null,
                        new Stop(.20, colors[0]),
                        new Stop(.5, colors[1]),
                        new Stop(.80, colors[2])),
                null, null));
    }

    private Color getColorByLetter(char c) {
        switch (c) {
            case 'R':
                return Color.RED;
            case 'G':
                return Color.LIME;
            default:
                return Color.BLUE;
        }
    }

    // for rgb color
    public Color getVisibleColor(Color startColor) {
        return startColor;
    }
}
