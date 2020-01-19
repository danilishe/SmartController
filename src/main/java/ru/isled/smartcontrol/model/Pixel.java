package ru.isled.smartcontrol.model;

import javafx.scene.paint.Color;

public class Pixel {
    private boolean rgb = false;
    private Color color = Color.WHITE;
    private RgbOrder rgbOrder = RgbOrder.RGB;
    private int quantifier = 1;

    public boolean isRgb() {
        return rgb;
    }

    public Pixel setRgb(boolean rgb) {
        this.rgb = rgb;
        return this;
    }

    public Pixel setColor(Color color) {
        this.color = color;
        return this;
    }

    public RgbOrder getRgbOrder() {
        return rgbOrder;
    }

    public Pixel setRgbOrder(RgbOrder rgbOrder) {
        this.rgbOrder = rgbOrder;
        return this;
    }

    public int getQuantifier() {
        return quantifier;
    }

    public Pixel setQuantifier(int quantifier) {
        this.quantifier = quantifier;
        return this;
    }

    public int getQuantity() {
        return rgb ? quantifier : quantifier * 3;
    }
}
