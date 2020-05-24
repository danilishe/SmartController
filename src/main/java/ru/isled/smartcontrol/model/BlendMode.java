package ru.isled.smartcontrol.model;

public enum BlendMode {

    FULL_BLEND ("Залить фоновым цветом"),
    EFFECT("Только эффект"),
    ON_EMPTY("Только на пустых (с заливкой)"),
    EFFECT_ON_EMPTY("Только на пустых (без заливки)"),
    ON_FULL("Только на использованных (с заливкой)"),
    EFFECT_ON_FULL("Только на использованных (без заливки)");

    String description;

    BlendMode(String description) {
        this.description = description;
    }

    String getDescription() {
        return description;
    }

    public BlendMode byDescription(String description) {
        for (BlendMode value : BlendMode.values()) {
            if (value.description.equals(description))
                return value;
        }
        return FULL_BLEND;
    }


    @Override
    public String toString() {
        return description;
    }
}
