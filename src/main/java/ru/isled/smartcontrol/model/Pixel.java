package ru.isled.smartcontrol.model;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import ru.isled.smartcontrol.model.effect.PixelEffect;
import ru.isled.smartcontrol.model.effect.RgbMode;
import ru.isled.smartcontrol.util.Util;

import java.util.ArrayList;
import java.util.List;

import static ru.isled.smartcontrol.Constants.MAX_BRIGHT;
import static ru.isled.smartcontrol.Constants.MAX_FRAMES;

public class Pixel {
    public final StringProperty background;
    private final IntegerProperty number;
    private final ObjectProperty<RgbMode> rgbMode;
    private final ObjectProperty<Integer> quantifier;
    private final ObservableList<Frame> frames;

    public Pixel(int number, int framesCount) {
        this(number, RgbMode.WHITE, 1, new ArrayList<>(MAX_FRAMES));
        for (int i = 0; i < framesCount; i++) {
            frames.add(new Frame(rgbMode));
        }
    }

    public Pixel(int number, RgbMode rgbMode, int quantifier, List<Pixel.Frame> frames) {
        this.number = new SimpleIntegerProperty(number);
        this.rgbMode = new SimpleObjectProperty<>(rgbMode);
        this.quantifier = new SimpleObjectProperty<>(quantifier);
        this.frames = frames == null ? FXCollections.observableArrayList() : FXCollections.observableArrayList(frames);
        background = new SimpleStringProperty(rgbMode.getBackground());
        // if rgbMode changes, background changes too
        this.rgbMode.addListener((v, o, n) -> {
            background.set(getRgbMode().getBackground());
            this.getFrames().forEach(Frame::updateBackground);
        });
    }

    public boolean isMultichannel() {
        return getRgbMode().isMultichannel();
    }

    /**
     * @return channels count of selected rgbMode
     */
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
        return quantifier;
    }

    /**
     * frameNo starts from 0!!!
     */
    public Color[] getInterpolatedFrame(int frameNo, int frameLengthMsec) {
        Color[] interpolated = getFrames().get(frameNo).getInterpolated(Util.framesAt(frameLengthMsec));
        for (int i = 0; i < interpolated.length; i++) {
            interpolated[i] = getRgbMode().getVisibleColor(interpolated[i]);
        }
        return interpolated;
    }

    /**
     * exports pixelFrame with quantifiers and respectively which rgbMode has the channel (this pixel)
     *
     * @param frameNo frame number
     * @param frameLengthMsec desired frame length in milliseconds
     * @return byte array of subframes. all channels are flatten and repeated
     */
    public byte[][] exportFrame(int frameNo, int frameLengthMsec) {
        final int subFrames = Util.framesAt(frameLengthMsec);
        byte[][] data = new byte[subFrames][getChannelsCount()];
        final Color[] interpolated = getFrames().get(frameNo).getInterpolated(subFrames);
        for (int subFrame = 0; subFrame < subFrames; subFrame++) {
            for (int quantifier = 0; quantifier < getQuantifier(); quantifier++) {
                System.arraycopy(
                        getRgbMode().export(interpolated[subFrame]), 0,
                        data[subFrame], quantifier * channels(), channels());
            }
        }
        return data;
    }

    /**
     * Frame Pixel is logical unit of each frame of program. Each Frame Pixel knows only own colors, rgbMode and effect
     */
    public static class Frame {
        private final ObjectProperty<RgbMode> rgbMode;
        private final StringProperty background;
        private Color startColor;
        private Color endColor;
        private PixelEffect effect;
        private final ChangeListener<RgbMode> changeModeListener = (v, o, n) -> {
            if (o.channels() != n.channels()) this.updateBackground();
        };

        public Frame(ObjectProperty<RgbMode> rgbMode) {
            this(Color.BLACK, PixelEffect.Solid, rgbMode);
        }

        public Frame(Color startColor, Color endColor, PixelEffect effect, ObjectProperty<RgbMode> rgbMode) {
            this.startColor = startColor;
            this.endColor = endColor;
            this.effect = effect;
            this.background = new SimpleStringProperty("");
            this.rgbMode = rgbMode;
            rgbMode.addListener(changeModeListener);
            updateBackground();
        }

        public Frame(Color oneColor, PixelEffect effect, ObjectProperty<RgbMode> rgbMode) {
            this(oneColor, oneColor, effect, rgbMode);
        }

        private void updateBackground() {
            Project.setHasChanges(true);
            String start = Util.toHex(rgbMode.get().getVisibleColor(startColor));
            String end = Util.toHex(rgbMode.get().getVisibleColor(endColor));
            background.setValue(
                    "-fx-background-color: linear-gradient(from 0% 0% to 0% 100%, " +
                            "#" + start + " 1%, #" + end + " 99%), " +
                            effect.overlay + ";");

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

        private Color[] getInterpolated(int subframesAmount) {
            return getEffect().interpolate(subframesAmount, getStartColor(), getEndColor());
        }

        public StringProperty backgroundProperty() {
            return background;
        }
    }
}
