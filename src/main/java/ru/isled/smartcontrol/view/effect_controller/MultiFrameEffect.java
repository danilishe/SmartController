package ru.isled.smartcontrol.view.effect_controller;

import ru.isled.smartcontrol.model.Project;

public interface MultiFrameEffect {
    void apply(Project project, int x1, int y1, int x2, int y2);
}
