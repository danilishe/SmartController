package ru.isled.smartcontrol.model.effect.multiframe;

import lombok.Builder;
import lombok.Getter;
import ru.isled.smartcontrol.model.Direction;

@Builder
@Getter
public class EffectParameters {
    private final Direction direction;
    private final int width;
    private final int traceBefore;
    private final int traceAfter;
    private final int blockWidth;
    private final boolean autoAdjustLength;
}
