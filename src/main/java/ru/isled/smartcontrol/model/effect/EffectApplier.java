package ru.isled.smartcontrol.model.effect;

import ru.isled.smartcontrol.model.Area;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.model.effect.multiframe.EffectParameters;
import ru.isled.smartcontrol.model.effect.multiframe.MultiFrameEffect;

public class EffectApplier {
    public static void applyStructure(Project project, MultiFrameEffect effect, Area area, EffectParameters params) {
        if (params.isAutoAdjustLength() && effect.length() > project.framesCount() - area.getY1())
            project.setFramesCount(area.getY1() + effect.length());

    }

    public static void applyColor(Project project, MultiFrameEffect effect, Area area, EffectParameters params) {
        // todo
    }
}
