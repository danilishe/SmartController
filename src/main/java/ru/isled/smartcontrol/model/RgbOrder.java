package ru.isled.smartcontrol.model;

import javafx.scene.paint.Color;

import static ru.isled.smartcontrol.Constants.MAX_BRIGHT;

public enum RgbOrder {
    RGB {
        @Override
        byte[] getChannels(Color color) {
            return new byte[]{
                    toByte(color.getRed()),
                    toByte(color.getGreen()),
                    toByte(color.getBlue()),
            };
        }
    },
    RBG {
        @Override
        byte[] getChannels(Color color) {
            return new byte[]{
                    toByte(color.getRed()),
                    toByte(color.getBlue()),
                    toByte(color.getGreen()),
            };
        }
    },
    GBR {
        @Override
        byte[] getChannels(Color color) {
            return new byte[]{
                    toByte(color.getGreen()),
                    toByte(color.getBlue()),
                    toByte(color.getRed()),
            };
        }
    },
    GRB {
        @Override
        byte[] getChannels(Color color) {
            return new byte[]{
                    toByte(color.getGreen()),
                    toByte(color.getRed()),
                    toByte(color.getBlue()),
            };
        }
    },
    BGR {
        @Override
        byte[] getChannels(Color color) {
            return new byte[]{
                    toByte(color.getBlue()),
                    toByte(color.getGreen()),
                    toByte(color.getRed()),
            };
        }
    },
    BRG {
        @Override
        byte[] getChannels(Color color) {
            return new byte[]{
                    toByte(color.getBlue()),
                    toByte(color.getRed()),
                    toByte(color.getGreen()),
            };
        }
    };

    private static byte toByte(double bright) {
        return (byte) (bright * MAX_BRIGHT);
    }

    abstract byte[] getChannels(Color color);
}
