package ru.isled.smartcontrol.model.effect.multiframe;

public class BuildingEffectCreator implements MultiFrameEffectCreator {
    @Override
    public MultiFrameEffect create(EffectParameters parameters) {
        final MultiFrameEffect effect = new MultiFrameEffect(parameters.getWidth());
        for (int i = 0; i < parameters.getTraceBefore(); i++) {

        }

        return effect;
    }
}
