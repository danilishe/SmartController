package ru.isled.smartcontrol.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import ru.isled.smartcontrol.model.effect.PixelEffect;
import ru.isled.smartcontrol.model.effect.RgbMode;
import ru.isled.smartcontrol.util.ColorToHex;

import java.util.ArrayList;
import java.util.List;

import static ru.isled.smartcontrol.Constants.*;

public class Pixel implements ColorToHex {
    private final ObjectProperty<Color> color;
    private final ObjectProperty<RgbMode> rgbMode;
    private final IntegerProperty quantifier;
    private final ObservableList<Frame> frames;

    private final BooleanProperty visible;

    public Pixel() {
        this(Color.WHITE, RgbMode.MONO, 1, new ArrayList<>(MAX_FRAMES), false);
    }

    public Pixel(Color color, RgbMode rgbMode, int quantifier, List<Frame> frames, boolean visible) {
        this.color = new SimpleObjectProperty<>(color);
        this.rgbMode = new SimpleObjectProperty<>(rgbMode);
        this.quantifier = new SimpleIntegerProperty(quantifier);
        this.frames = FXCollections.observableArrayList(frames);
        this.visible = new SimpleBooleanProperty(visible);
    }

    /**
     * Returns color presentation of current pixel. If it monocolor will return solid color.
     * In case of RGB it will return representation of current RGB mode
     */
    public String getCss() {
        if (isRgb()) {
            final Color[] colors = getRgbMode().getColors();
            return String.format("linear-gradient(from 0%% 0%% to 100%% 0%%, #%s 25%%,  #%s 50%%, #%s 75%%)",
                    toHex(colors[0]),
                    toHex(colors[1]),
                    toHex(colors[2]));
        } else {
            return String.format("#%s", toHex(getColor()));
        }
    }

    public boolean isRgb() {
        return getRgbMode() == RgbMode.MONO;
    }

    public int getChannelsCount() {
        return isRgb() ? getQuantifier() : getQuantifier() * 3;
    }

    public boolean isVisible() {
        return visible.get();
    }

    public Pixel setVisible(boolean visible) {
        this.visible.set(visible);
        return this;
    }

    public BooleanProperty visibleProperty() {
        return visible;
    }

    public Color getColor() {
        return color.get();
    }

    public Pixel setColor(Color color) {
        this.color.set(color);
        return this;
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public ObservableList<Frame> getFrames() {
        return frames;
    }

    public Pixel setFrames(List<Frame> frames) {
        this.frames.clear();
        this.frames.addAll(frames);
        return this;
    }

    public RgbMode getRgbMode() {
        return rgbMode.get();
    }

    public Pixel setRgbMode(RgbMode rgbMode) {
        this.rgbMode.set(rgbMode);
        return this;
    }

    public ObjectProperty<RgbMode> rgbModeProperty() {
        return rgbMode;
    }

    public int getQuantifier() {
        return quantifier.get();
    }

    public Pixel setQuantifier(int quantifier) {
        this.quantifier.set(quantifier);
        return this;
    }

    public IntegerProperty quantifierProperty() {
        return quantifier;
    }

    public Color[] getInterpolatedFrame(int frameNo, int frameLength) {
        return getFrames().get(frameNo).getInterpolated(frameLength / MIN_FRAME_LENGTH);
    }

    /**
     * Frame Pixel is logical unit of each frame of program. Each Frame Pixel knows only own colors and effect
     */
    public static class Frame {
        private final ObjectProperty<Color> startColor;
        private final ObjectProperty<Color> endColor;
        private final ObjectProperty<PixelEffect> effect;

        public Frame(Color startColor, Color endColor, PixelEffect effect) {
            this.startColor = new SimpleObjectProperty<>(startColor);
            this.endColor = new SimpleObjectProperty<>(endColor);
            this.effect = new SimpleObjectProperty<>(effect);
        }

        public Frame(Color oneColor, PixelEffect effect) {
            this.startColor = new SimpleObjectProperty<>(oneColor);
            this.endColor = new SimpleObjectProperty<>(oneColor);
            this.effect = new SimpleObjectProperty<>(effect);
        }

        public Frame setColor(Color startColor, Color endColor) {
            this.startColor.set(startColor);
            this.endColor.set(endColor);
            return this;
        }

        public Frame setColor(Color oneColor) {
            startColor.set(oneColor);
            endColor.set(oneColor);
            return this;
        }

        public Color getStartColor() {
            return startColor.get();
        }

        public Color getEndColor() {
            return endColor.get();
        }

        public PixelEffect getEffect() {
            return effect.get();
        }

        public Frame setEffect(PixelEffect effect) {
            this.effect.set(effect);
            return this;
        }

        private Frame setBright(double bright) {
            return setColor(
                    Color.hsb(getStartColor().getHue(), getStartColor().getSaturation(), bright),
                    Color.hsb(getEndColor().getHue(), getEndColor().getSaturation(), bright)
            );
        }

        public Frame setBright(int bright) {
            return setBright((double) bright / MAX_BRIGHT);
        }

        private Color[] getInterpolated(int frameLength) {
            return getEffect().interpolate(frameLength, getStartColor(), getEndColor());
        }
    }

}
