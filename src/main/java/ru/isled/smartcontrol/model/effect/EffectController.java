package ru.isled.smartcontrol.model.effect;

import ru.isled.smartcontrol.model.effect.multiframe.EffectParameters;

import java.util.Optional;

public interface EffectController {
    Optional<EffectParameters> getParameters();
}
