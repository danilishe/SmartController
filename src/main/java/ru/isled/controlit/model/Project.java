package ru.isled.controlit.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ru.isled.controlit.Constants.MAX_FRAMES;
import static ru.isled.controlit.Constants.UNSAVED_FILE_NAME;

public class Project {
    public BooleanProperty hasChangesProperty() {
        return hasChanges;
    }

    private BooleanProperty hasChanges;
    private ObjectProperty<File> file;
    private List<LedFrame> data;

    public Project() {
        hasChanges = new SimpleBooleanProperty(false);
        data = new ArrayList<>(MAX_FRAMES);
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
}
