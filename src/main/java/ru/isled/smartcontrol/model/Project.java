package ru.isled.smartcontrol.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import ru.isled.smartcontrol.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.isled.smartcontrol.Constants.*;
import static ru.isled.smartcontrol.util.Util.framesAt;

public class Project {
    private final IntegerProperty framesCount;
    private final IntegerProperty pixelsCount;
    private final ObservableList<Pixel> pixels;
    private final ObservableList<LedFrame> frames;
    private final List<LedFrame> framesCache = new ArrayList<>();
    private final DoubleProperty gamma;
    private final BooleanProperty hasChanges;
    private final ObjectProperty<File> file;

    public Project() {
        this(DEFAULT_FRAMES_COUNT, DEFAULT_PIXEL_COUNT, new ArrayList<>(MAX_CHANNELS_COUNT), new ArrayList<>(MAX_FRAMES), DEFAULT_GAMMA, false, null);
        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {
            pixels.add(new Pixel(i + 1, DEFAULT_FRAMES_COUNT));
        }
        for (int i = 0; i < DEFAULT_FRAMES_COUNT; i++) {
            LedFrame frame = new LedFrame(i + 1, getPixelsBackgroundProperties(i));
            frames.add(frame);
        }
        framesCache.addAll(frames);
    }

    private List<StringProperty> getPixelsBackgroundProperties(final int index) {
        return pixels.stream().map(pixel -> pixel.getFrames().get(index).backgroundProperty()).collect(Collectors.toList());
    }

    public Project(int framesCount, int pixelsCount, List<Pixel> pixels, List<LedFrame> frames, double gamma, boolean hasChanges, File file) {
        this.framesCount = new SimpleIntegerProperty(framesCount);
        this.pixelsCount = new SimpleIntegerProperty(pixelsCount);
        this.pixels = FXCollections.observableList(pixels);
        this.frames = FXCollections.observableList(frames);
        this.gamma = new SimpleDoubleProperty(gamma);
        this.hasChanges = new SimpleBooleanProperty(hasChanges);
        this.file = new SimpleObjectProperty<>(file);
        this.framesCount.addListener(c -> onFramesChanged());
        this.framesCache.addAll(frames);
    }

    public Pixel.Frame getPixelFrame(int frameNo, int pixelNo) {
        return getPixel(pixelNo).getFrames().get(frameNo);
    }

    public double getGamma() {
        return gamma.get();
    }

    public Project setGamma(double gamma) {
        this.gamma.set(gamma);
        return this;
    }

    public int framesCount() {
        return framesCount.get();
    }

    public Project setFramesCount(int framesCount) {
        this.framesCount.set(framesCount);
        return this;
    }

    public int pixelsCount() {
        return pixelsCount.get();
    }

    public Project setPixelsCount(int pixelsCount) {
        this.pixelsCount.set(pixelsCount);
        return this;
    }

    public BooleanProperty hasChangesProperty() {
        return hasChanges;
    }

    public boolean hasUnsavedChanges() {
        return hasChanges.get();
    }

    public String getName() {
        return UNSAVED_FILE_NAME;
//        return file == null ? UNSAVED_FILE_NAME : file.getValue().getName();
    }

    public boolean hasName() {
        return file.get() != null;
    }

    public Project setFileName(File file) {
        this.file.setValue(file);
        return this;
    }

    public Project setHasChanges(boolean changed) {
        hasChanges.setValue(changed);
        return this;
    }

    public LedFrame getFrame(int frameNo) {
        return frames.get(frameNo);
    }

    public File getFile() {
        return file.get();
    }

    public int size() {
        return frames.size();
    }

    public Project setFrames(List<LedFrame> frames) {
        this.frames.clear();
        this.frames.addAll(frames);
        return this;
    }

    public Project setPixels(List<Pixel> list) {
        pixels.clear();
        pixels.addAll(list);
        return this;
    }

    /**
     * @return количество пикселей с учётом квантификаторов (кратных каналов)
     */
    public int getChannelsCount() {
        int result = 0;
        for (int i = 0; i < pixelsCount(); i++) {
            result += getPixel(i).getChannelsCount();
        }
        return result;
    }

    public Pixel getPixel(int pixelNo) {
        return pixels.get(pixelNo);
    }

    /**
     * Return interpolated frame without using cycles information and pixel quantifier.
     * frameNo starts from 0!!!
     */
    public List<Color[]> getInterpolatedFrame(int frameNo) {
        final List<Color[]> interpolatedFrame = new ArrayList<>(pixelsCount());
        final int frameLength = getFrame(frameNo).getLength();
        for (int i = 0; i < pixelsCount(); i++) {
            interpolatedFrame.add(getPixel(i).getInterpolatedFrame(frameNo, frameLength));
        }
        return interpolatedFrame;
    }

    public final List<List<Color[]>> getInterpolated() {
        final List<List<Color[]>> interpolated = new ArrayList<>(framesCount());
        for (int i = 0; i < framesCount(); i++) {
            final List<Color[]> interpolatedFrame = getInterpolatedFrame(i);
            for (int j = 0; j < getFrame(i).getCycles(); j++) {
                interpolated.add(interpolatedFrame);
            }
        }
        return interpolated;
    }

    public final byte[] export() {
        final int pixelFrames = getFrames().stream()
                .mapToInt(frame -> framesAt(frame.getLength()) * frame.getCycles())
                .sum() * MAX_CHANNELS_COUNT;
        assert pixelFrames > 0;
        byte[] data = new byte[pixelFrames];

        int subframesCounter = 0;
        for (LedFrame frame : getFrames()) {
            int channelsCounter = 0;
            for (int pixelNo = 0; pixelNo < pixelsCount(); pixelNo++) {

                final byte[][] pixelExport = getPixel(pixelNo).exportFrame(frame.getNumber(), frame.getLength());
                for (int frameCycle = 0; frameCycle < frame.getCycles(); frameCycle++) {
                    for (int subFrame = 0; subFrame < pixelExport.length; subFrame++) {
                        for (int channel = 0; channel < pixelExport[0].length; channel++) {
                            final int i = (subframesCounter + subFrame) * MAX_CHANNELS_COUNT + channelsCounter + channel;
                            // fixme wrong counters
                            data[i] = Util.withGamma(pixelExport[subFrame][channel], getGamma());
                        }
                    }
                    subframesCounter += pixelExport.length;
                }
                channelsCounter += pixelExport[0].length;
                assert channelsCounter < MAX_CHANNELS_COUNT;
            }

        }
        return data;
    }

    public long getLength() {
        return frames.stream()
                .limit(framesCount())
                .mapToLong(f -> f.getLength() * f.getCycles())
                .sum();
    }

    public void onFramesChanged() {
        // framesCache.size() must be == frames count at each pixel
        if (framesCache.size() < framesCount()) { // if project has no that frames before
            for (Pixel pixel : pixels) { // Creating new Frames in Pixel lists
                for (int i = framesCache.size(); i < framesCount(); i++) {
                    pixel.getFrames().add(new Pixel.Frame(pixel.rgbModeProperty()));
                }
            }
            for (int i = framesCache.size(); i < framesCount(); i++) {
                framesCache.add(new LedFrame(i + 1, getPixelsBackgroundProperties(i)));
            }
        }
        if (frames.size() < framesCount()) {
            frames.addAll(framesCache.subList(frames.size(), framesCount()));
        } else {
            frames.remove(framesCount(), frames.size());
        }
    }

    public ObservableList<LedFrame> getFrames() {
        return frames;
    }

    public List<Pixel> getPixels() {
        return pixels;
    }
}
