package ru.isled.smartcontrol.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ru.isled.smartcontrol.Constants.*;

public class Project {
    private double gamma = 2.2;
    private BooleanProperty hasChanges;
    private ObjectProperty<File> file;
    private List<Pixel> pixels;
    private List<LedFrame> frames;
    private int frameCount;
    private int pixelCount;

    public Project() {
        hasChanges = new SimpleBooleanProperty(false);
        frames = new ArrayList<>(MAX_FRAMES);
        pixels = new ArrayList<>(MAX_CHANNELS_COUNT);
        for (int i = 0; i < MAX_CHANNELS_COUNT; i++) {
            pixels.add(new Pixel());
        }
    }

    public double getGamma() {
        return gamma;
    }

    public Project setGamma(double gamma) {
        this.gamma = gamma;
        return this;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public Project setFrameCount(int frameCount) {
        this.frameCount = frameCount;
        return this;
    }

    public int getPixelCount() {
        return pixelCount;
    }

    public Project setPixelCount(int pixelCount) {
        this.pixelCount = pixelCount;
        return this;
    }

    public BooleanProperty hasChangesProperty() {
        return hasChanges;
    }

    public boolean hasUnsavedChanges() {
        return hasChanges.get();
    }

    public String getName() {
        return file == null ? UNSAVED_FILE_NAME : file.getValue().getName();
    }

    public boolean hasName() {
        return file != null;
    }

    public Project setFileName(File newName) {
        if (file == null)
            file = new SimpleObjectProperty<>(newName);
        else
            file.setValue(newName);
        return this;
    }

    public Project setHasChanges(boolean changed) {
        hasChanges.setValue(changed);
        return this;
    }

    public LedFrame getRow(int row) {
        return frames.get(row);
    }

    public boolean addRow(LedFrame frame) {
        return frames.add(frame);
    }

    public File getFile() {
        return file == null ? null : file.getValue();
    }

    public int size() {
        return frames.size();
    }

    public List<LedFrame> getFrames() {
        return frames;
    }

    public Project setFrames(List<LedFrame> frames) {
        this.frames = frames;
        return this;
    }

    public List<Pixel> getPixels() {
        return pixels;
    }

    public Project setPixels(List<Pixel> list) {
        pixels = list;
        return this;
    }

    /**
     * @return количество пикселей с учётом квантификаторов (кратных каналов)
     */
    public int getChannelsCount() {
        int result = 0;
        for (Pixel pixel : pixels) {
            result += pixel.getQuantity();
        }
        return result;
    }
}
