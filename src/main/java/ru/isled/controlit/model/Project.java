package ru.isled.controlit.model;

import ru.isled.controlit.Constants;

import java.util.ArrayList;
import java.util.List;

public class Project implements Constants {
    private boolean hasChanges = false;
    private String name;
    private List<LedFrame> data = new ArrayList<>();

    public boolean hasUnsavedChanges() {
        return hasChanges;
    }

    public String getName() {
        return name == null ? "несохранённый проект" : name;
    }

    public boolean hasName() {
        return name != null;
    }

    public void setName(String newName) {
        name = newName;
    }


    public void setHasChanges(boolean hasChanges) {
        this.hasChanges = hasChanges;
    }
}
