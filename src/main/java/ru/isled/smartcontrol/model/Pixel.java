package ru.isled.smartcontrol.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import ru.isled.smartcontrol.model.effect.PixelEffect;
import ru.isled.smartcontrol.model.effect.RgbMode;
import ru.isled.smartcontrol.util.BgCache;
import ru.isled.smartcontrol.util.ColorToHex;

import java.util.ArrayList;
import java.util.List;

import static ru.isled.smartcontrol.Constants.*;

public class Pixel implements ColorToHex {
    private final IntegerProperty number;
    private final ObjectProperty<Color> color;
    private final ObjectProperty<RgbMode> rgbMode;
    private final IntegerProperty quantifier;
    private final ObservableList<Pixel.Frame> frames;
    private final BooleanProperty visible;
    private final ObjectProperty<Background> background;

    public Pixel(int number) {
        this(number, Color.WHITE, RgbMode.MONO, 1, new ArrayList<>(MAX_FRAMES), false);
        for (int i = 0; i < DEFAULT_FRAMES_COUNT; i++) {
            frames.add(new Frame(i));
        }
    }

    public Pixel(int number, Color color, RgbMode rgbMode, int quantifier, List<Pixel.Frame> frames, boolean visible) {
        this.number = new SimpleIntegerProperty(number);
        this.color = new SimpleObjectProperty<>(color);
        this.rgbMode = new SimpleObjectProperty<>(rgbMode);
        this.quantifier = new SimpleIntegerProperty(quantifier);
        this.frames = FXCollections.observableArrayList(frames);
        this.visible = new SimpleBooleanProperty(visible);
        background = new SimpleObjectProperty<>(getBackground());
        this.color.addListener((observable, oldValue, newValue) -> background.set(getBackground()));
        this.rgbMode.addListener((observable, oldValue, newValue) -> background.set(getBackground()));
    }

    private Background getBackground() {
        if (isRgb()) { // todo optimize this
            final Color[] colors = getRgbMode().getColors();
            return new Background(new BackgroundFill(
                    new LinearGradient(0, 0,
                            1, 0,
                            true, null,
                            new Stop(.20, colors[0]),
                            new Stop(.5, colors[1]),
                            new Stop(.70, colors[2]))
                    , null, null));
        }
        return BgCache.INSTANCE.get(color.get());
    }

    public ObjectProperty<Background> backgroundProperty() {
        return background;
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
        return getRgbMode() != RgbMode.MONO;
    }

    public int getNumber() {
        return number.get();
    }

    public void setNumber(int number) {
        this.number.set(number);
    }

    public ObjectProperty<Integer> numberProperty() {
        return number.asObject();
    }

    public int getChannelsCount() {
        return isRgb() ? getQuantifier() * 3 : getQuantifier();
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

    public ObjectProperty<Integer> quantifierProperty() {
        return quantifier.asObject();
    }

    public Color[] getInterpolatedFrame(int frameNo, int frameLength) {
        return getFrames().get(frameNo).getInterpolated(frameLength / MIN_FRAME_LENGTH);
    }

    public String getFrameStyle(Integer number) {
        if (isRgb()) { // todo add effect style
            return String.format("linear-gradient(from 0%% 0%% to 0%% 100%%, #%s 0%%, #%s 100%%)",
                    toHex(getFrames().get(number).getStartColor()),
                    toHex(getFrames().get(number).getEndColor()));
        }
        return String.format("#%s", toHex(getColor()));
    }

    /**
     * Frame Pixel is logical unit of each frame of program. Each Frame Pixel knows only own colors and effect
     */
    public static class Frame {
        private final ObjectProperty<Integer> number;
        private final ObjectProperty<Color> startColor;
        private final ObjectProperty<Color> endColor;
        private final ObjectProperty<PixelEffect> effect;

        public Frame(int number) {
            this(number, Color.BLACK, PixelEffect.Solid);
        }

        public Frame(int number, Color startColor, Color endColor, PixelEffect effect) {
            this.number = new SimpleObjectProperty<>(number);
            this.startColor = new SimpleObjectProperty<>(startColor);
            this.endColor = new SimpleObjectProperty<>(endColor);
            this.effect = new SimpleObjectProperty<>(effect);
        }

        public Frame(int number, Color oneColor, PixelEffect effect) {
            this(number, oneColor, oneColor, effect);
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

        public Integer getNumber() {
            return number.get();
        }

        public void setNumber(Integer number) {
            this.number.set(number);
        }

        public ObjectProperty<Integer> numberProperty() {
            return number;
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
