package ru.isled.smartcontrol.model.effect;

import javafx.scene.paint.Color;
import ru.isled.smartcontrol.util.Convert;

import static ru.isled.smartcontrol.Constants.MAX_BRIGHT;

public enum RgbMode {
    RGB {
        @Override
        byte[] export(Color color) {
            return new byte[]{
                    toByte(color.getRed()),
                    toByte(color.getGreen()),
                    toByte(color.getBlue()),
            };
        }
    },
    RBG {
        @Override
        byte[] export(Color color) {
            return new byte[]{
                    toByte(color.getRed()),
                    toByte(color.getBlue()),
                    toByte(color.getGreen()),
            };
        }
    },
    GBR {
        @Override
        byte[] export(Color color) {
            return new byte[]{
                    toByte(color.getGreen()),
                    toByte(color.getBlue()),
                    toByte(color.getRed()),
            };
        }
    },
    GRB {
        @Override
        byte[] export(Color color) {
            return new byte[]{
                    toByte(color.getGreen()),
                    toByte(color.getRed()),
                    toByte(color.getBlue()),
            };
        }
    },
    BGR {
        @Override
        byte[] export(Color color) {
            return new byte[]{
                    toByte(color.getBlue()),
                    toByte(color.getGreen()),
                    toByte(color.getRed()),
            };
        }
    },
    BRG {
        @Override
        byte[] export(Color color) {
            return new byte[]{
                    toByte(color.getBlue()),
                    toByte(color.getRed()),
                    toByte(color.getGreen()),
            };
        }
    },
    MONO_WHITE {
        @Override
        public int channels() {
            return 1;
        }

        @Override
        byte[] export(Color color) {
            return new byte[]{
                    toByte(color.getBrightness())
            };
        }

        @Override
        public String getBackground() {
            return "-fx-background-color: white;";
        }

        @Override
        public Color getVisibleColor(Color startColor) {
            return startColor.grayscale();
        }
    };

    private static byte toByte(double bright) {
        return (byte) (bright * MAX_BRIGHT);
    }

    abstract byte[] export(Color color);

    public Color[] getColors() {
        Color[] colors = new Color[3];
        for (int i = 0; i < name().length(); i++) {
            colors[i] = getColorByLetter(name().charAt(i));
        }
        return colors;
    }

    // for rgb colors
    public String getBackground() {
        final Color[] colors = getColors();
        return String.format("-fx-background-color: linear-gradient(from 0%% 0%% to 100%% 0%%, #%s 20%%, #%s 50%%, #%s 80%%);",
                Convert.toHex(colors[0]),
                Convert.toHex(colors[1]),
                Convert.toHex(colors[2]));
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

    public boolean isMultichannel() {
        return channels() > 1;
    }

    // for rgb color
    public Color getVisibleColor(Color startColor) {
        return startColor;
    }

    public int channels() {
        return 3;
    }
}
