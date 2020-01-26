package ru.isled.smartcontrol.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ru.isled.smartcontrol.Constants.*;

public class Project {
    private final IntegerProperty framesCount;
    private final IntegerProperty pixelsCount;
    private final ObservableList<Pixel> pixels;
    private final ObservableList<LedFrame> frames;
    private final DoubleProperty gamma;
    private final BooleanProperty hasChanges;
    private final ObjectProperty<File> file;

    public Project() {
        this(DEFAULT_FRAMES_COUNT, DEFAULT_PIXEL_COUNT, new ArrayList<>(MAX_CHANNELS_COUNT), new ArrayList<>(MAX_FRAMES), DEFAULT_GAMMA, false, null);
        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {
            pixels.add(new Pixel(i + 1)
                    .setVisible(i < DEFAULT_PIXEL_COUNT));
        }
        for (int i = 0; i < DEFAULT_FRAMES_COUNT; i++) {
            frames.add(new LedFrame(i + 1));
        }
    }

    public Project(int framesCount, int pixelsCount, ArrayList<Pixel> pixels, ArrayList<LedFrame> frames, double gamma, boolean hasChanges, File file) {
        this.framesCount = new SimpleIntegerProperty(framesCount);
        this.pixelsCount = new SimpleIntegerProperty(pixelsCount);
        this.pixels = FXCollections.observableList(pixels);
        this.frames = FXCollections.observableList(frames);
        this.gamma = new SimpleDoubleProperty(gamma);
        this.hasChanges = new SimpleBooleanProperty(hasChanges);
        this.file = new SimpleObjectProperty<>(file);
        this.framesCount.addListener(c -> onFramesChanged());
        this.pixelsCount.addListener(c -> onPixelChange());
    }


    public double getGamma() {
        return gamma.get();
    }

    public Project setGamma(double gamma) {
        this.gamma.set(gamma);
        return this;
    }

    public int getFramesCount() {
        return framesCount.get();
    }

    public Project setFramesCount(int framesCount) {
        this.framesCount.set(framesCount);
        return this;
    }

    public int getPixelsCount() {
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

    public boolean addFrame(LedFrame frame) {
        return frames.add(frame);
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
        for (Pixel pixel : pixels) {
            result += pixel.getChannelsCount();
        }
        return result;
    }

    public Pixel getPixel(int pixelNo) {
        return pixels.get(pixelNo);
    }

    /**
     * Return interpolated frame without using cycles information and pixel quantifier
     */
    public List<Color[]> getInterpolatedFrame(int frameNo) {
        final List<Color[]> interpolatedFrame = new ArrayList<>(getPixelsCount());
        final int frameLength = getFrame(frameNo).getFrameLength();
        for (int i = 0; i < getPixelsCount(); i++) {
            interpolatedFrame.add(getPixel(i).getInterpolatedFrame(frameNo, frameLength));
        }
        return interpolatedFrame;
    }

    public List<List<Color[]>> getInterpolated() {
        final List<List<Color[]>> interpolated = new ArrayList<>(getFramesCount());
        for (int i = 0; i < getFramesCount(); i++) {
            if (getFrame(i).getFrameLength() == 0) continue;
            final List<Color[]> interpolatedFrame = getInterpolatedFrame(i);
            for (int j = 0; j < getFrame(i).getCycles(); j++) {
                interpolated.add(interpolatedFrame);
            }
        }
        return interpolated;
    }

    public long getLength() {
        return frames.stream()
                .limit(getFramesCount())
                .mapToLong(f -> f.getFrameLength() * f.getCycles())
                .sum();
    }

    private void onPixelChange() {
        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {
            getPixel(i).setVisible(i < getPixelsCount());
        }
    }

    public void onFramesChanged() {
        if (frames.size() < getFramesCount()) {
            for (int i = frames.size() - 1; i < getFramesCount(); i++) {
                addFrame(new LedFrame(i + 1).setVisible(true));
            }
        } else {
            for (int i = frames.size() - 1; i < getFramesCount(); i++) {
                getFrame(i).setVisible(false);
            }
        }
    }

    public ObservableList<LedFrame> getFrames() {
        return frames;
    }
}
