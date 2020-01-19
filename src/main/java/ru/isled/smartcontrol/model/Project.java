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
    private List<Integer> quantifiers;
    private List<LedFrame> data;
    //todo переделать в Enum
    private int frameCount;
    private int chanelCount;

    public Project() {
        hasChanges = new SimpleBooleanProperty(false);
        data = new ArrayList<>(MAX_FRAMES);
        quantifiers = new ArrayList<>(MAX_PIXELS_COUNT);
        for (int i = 0; i < MAX_PIXELS_COUNT; i++) {
            quantifiers.add(1);
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

    public int getChanelCount() {
        return chanelCount;
    }

    public Project setChanelCount(int chanelCount) {
        this.chanelCount = chanelCount;
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
        return data.get(row);
    }

    public boolean addRow(LedFrame frame) {
        return data.add(frame);
    }

    public File getFile() {
        return file == null ? null : file.getValue();
    }

    public int size() {
        return data.size();
    }

    public List<LedFrame> getData() {
        return data;
    }

    public Project setData(List<LedFrame> data) {
        this.data = data;
        return this;
    }

    public List<Integer> getQuantifiers() {
        return quantifiers;
    }

    public Project setQuantifiers(List<Integer> list) {
        quantifiers.clear();
        quantifiers.addAll(list);
        return this;
    }

    /**
     * @return количество пикселей с учётом квантификаторов (кратных каналов)
     */
    public int getTotalPixelCount() {
        return getQuantifiers().stream()
                .limit(chanelCount)
                .mapToInt(q -> q)
                .sum();
    }
}
