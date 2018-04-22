package ru.isled.smartcontrol.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ru.isled.smartcontrol.Constants.MAX_FRAMES;
import static ru.isled.smartcontrol.Constants.MAX_PIXELS_COUNT;
import static ru.isled.smartcontrol.Constants.UNSAVED_FILE_NAME;

public class Project {
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

    public int getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    public int getChanelCount() {
        return chanelCount;
    }

    public void setChanelCount(int chanelCount) {
        this.chanelCount = chanelCount;
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

    public void setFileName(File newName) {
        if (file == null)
            file = new SimpleObjectProperty<>(newName);
        else
            file.setValue(newName);
    }

    public void setHasChanges(boolean changed) {
        hasChanges.setValue(changed);
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

    public void setData(List<LedFrame> data) {
        this.data = data;
    }

    public List<Integer> getQuantifiers() {
        return quantifiers;
    }
    public void setQuantifiers(List<Integer> list) {
        quantifiers.clear();
        quantifiers.addAll(list);
    }

    public int getTotalPixelCount() {
        int sum = getQuantifiers().stream().limit(chanelCount).mapToInt(q -> q).sum();
        return sum > MAX_PIXELS_COUNT ? MAX_PIXELS_COUNT : sum;
    }
}
