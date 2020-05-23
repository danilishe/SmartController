package ru.isled.smartcontrol.model.effect;

import ru.isled.smartcontrol.model.Area;
import ru.isled.smartcontrol.model.Project;

public interface ProjectAdjuster {
    @Deprecated
    void applyEffect(Project project, int x1, int y1, int x2, int y2);
//    void applyEffect(Project project, Area area);
}
