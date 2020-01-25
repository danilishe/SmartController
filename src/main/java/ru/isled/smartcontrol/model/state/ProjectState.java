package ru.isled.smartcontrol.model.state;

import ru.isled.smartcontrol.model.Project;

//TODO
public abstract class ProjectState {
    private final Project project;

    public ProjectState(Project project) {
        this.project = project;
    }

    public abstract void onClose();

    public abstract void onSave();

    public abstract void onSaveAs();

    public abstract void onExport();

    public abstract void onNew();
}
