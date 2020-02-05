package ru.isled.smartcontrol.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import ru.isled.smartcontrol.model.effect.PixelEffect;
import ru.isled.smartcontrol.model.effect.RgbMode;
import ru.isled.smartcontrol.util.ColorToHex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.isled.smartcontrol.Constants.*;

public class Pixel implements ColorToHex {
    private final IntegerProperty number;
    private final ObjectProperty<RgbMode> rgbMode;
    private final IntegerProperty quantifier;
    private final ObservableList<Frame> frames;
    public final ObjectProperty<Background> background;

    public Pixel(int number) {
        this(number, RgbMode.MONO_WHITE, 1, new ArrayList<>(MAX_FRAMES));
        for (int i = 0; i < DEFAULT_FRAMES_COUNT; i++) {
            frames.add(new Frame(rgbMode));
        }
    }

    public Pixel(int number, RgbMode rgbMode, int quantifier, List<Pixel.Frame> frames) {
        this.number = new SimpleIntegerProperty(number);
        this.rgbMode = new SimpleObjectProperty<>(rgbMode);
        this.quantifier = new SimpleIntegerProperty(quantifier);
        this.frames = FXCollections.observableArrayList(frames);
        background = new SimpleObjectProperty<>(rgbMode.getBackground());
        // if rgbMode changes, background changes too
        this.rgbMode.addListener((v, o, n) -> background.set(getRgbMode().getBackground()));
    }

    public boolean isMultichannel() {
        return getRgbMode().isMultichannel();
    }

    public int channels() {
        return getRgbMode().channels();
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
        return getRgbMode().channels() * getQuantifier();
    }

    public List<Frame> getFrames() {
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

// fixme delete this if all background values are from pixel frames
//    public String getFrameStyle(Integer number) {
//        final Frame frame = getFrames().get(number);
//        return String.format("-fx-background-color: linear-gradient(from 0%% 0%% to 0%% 100%%, #%s 0%%, #%s 100%%);"
////                        + "-fx-background-insets: 5px;"
//                ,
//                toHex(getRgbMode().getVisibleColor(frame.getStartColor())),
//                toHex(getRgbMode().getVisibleColor(frame.getEndColor())));
//    }

    /**
     * Frame Pixel is logical unit of each frame of program. Each Frame Pixel knows only own colors, rgbMode and effect
     */
    public static class Frame {
        private Color startColor;
        private Color endColor;
        private PixelEffect effect;
        private final ObjectProperty<RgbMode> rgbMode;
        private final ObjectProperty<Background> background;
        private final ChangeListener<RgbMode> changeModeListener = (v, o, n) -> {
            if (o.channels() != n.channels()) this.updateBackground();
        };

        private void updateBackground() {
            background.setValue(
                    new Background(Arrays.asList(
                            effect.gradient, // effect
                            new BackgroundFill( // color mode
                                    new LinearGradient(0, 0, 0, 1, true, null,
                                            new Stop(0, rgbMode.get().getVisibleColor(startColor)),
                                            new Stop(1, rgbMode.get().getVisibleColor(endColor))), null, null)
                    ), null));
        }

        public Frame(ObjectProperty<RgbMode> rgbMode) {
            this(Color.BLACK, PixelEffect.Solid, rgbMode);
        }

        public Frame(Color startColor, Color endColor, PixelEffect effect, ObjectProperty<RgbMode> rgbMode) {
            this.startColor = startColor;
            this.endColor = endColor;
            this.effect = effect;
            this.background = new SimpleObjectProperty<>();
            this.rgbMode = rgbMode;
            rgbMode.addListener(changeModeListener);
        }

        public Frame(Color oneColor, PixelEffect effect, ObjectProperty<RgbMode> rgbMode) {
            this(oneColor, oneColor, effect, rgbMode);
        }

        public Frame setColor(Color startColor, Color endColor) {
            if (startColor != null) this.startColor = startColor;
            if (endColor != null) this.endColor = endColor;
            updateBackground();
            return this;
        }

        public Frame setColor(Color oneColor) {
            return setColor(oneColor, oneColor);
        }

        public Color getStartColor() {
            return startColor;
        }

        public Color getEndColor() {
            return endColor;
        }

        public PixelEffect getEffect() {
            return effect;
        }

        public Frame setEffect(PixelEffect effect) {
            this.effect = effect;
            updateBackground();
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

        public ObjectProperty<Background> backgroundProperty() {
            return background;
        }
    }
}
