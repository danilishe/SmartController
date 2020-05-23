package ru.isled.smartcontrol.model.effect.multiframe;

import lombok.Builder;
import lombok.Getter;
import ru.isled.smartcontrol.model.Direction;

@Builder
@Getter
public class EffectParameters {
    Direction direction;
    int width;
    int traceBefore;
    int traceAfter;
    int blockWidth;
    boolean autoAdjustLength;
}
