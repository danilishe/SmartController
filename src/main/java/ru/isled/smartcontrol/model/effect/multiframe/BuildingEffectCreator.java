package ru.isled.smartcontrol.model.effect.multiframe;

import ru.isled.smartcontrol.model.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuildingEffectCreator implements MultiFrameEffectCreator {
    @Override
    public MultiFrameEffect create(EffectParameters parameters) {
        if (parameters.getWidth() == 0) throw new IllegalArgumentException("Ширина эффекта не может быть 0!");
        final MultiFrameEffect effect = new MultiFrameEffect(parameters.getWidth());

        final List<MicroPixel> full = getFullBlock(parameters.getTraceBefore(), parameters.getBlockWidth(), parameters.getTraceAfter());


        for (int firstPixel = 0; firstPixel < parameters.getWidth(); firstPixel += parameters.getBlockWidth()) {
            for (int pos = parameters.getWidth() - 1; pos > firstPixel - full.size() + parameters.getBlockWidth() - 1; pos--) {
                List<MicroPixel> frame = getVisibleFrame(firstPixel, full, pos, parameters.getWidth(), parameters.getBlockWidth(), parameters.getTraceBefore());
                if (parameters.getDirection() == Direction.RIGHT) {
                    Collections.reverse(frame);
                }
                effect.addFrame(frame);
            }
        }

        return effect;
    }

    private List<MicroPixel> getVisibleFrame(int filledPixels, List<MicroPixel> fullTrace, int pos, int width, int blockWidth, int traceBefore) {
        List<MicroPixel> result = new ArrayList<>(width);
        for (int i = 0; i < width; i++) {
            if (i < filledPixels) { // filled blocks
                result.add(MicroPixel.FULL);
            } else if (i < pos) { // before fullTrace
                result.add(MicroPixel.EMPTY);
            } else { // safely puts parts of full trace
                int idx = i - pos;
                if (idx < fullTrace.size()) {
                    if (pos + traceBefore < filledPixels && i < blockWidth + filledPixels) {
                        result.add(MicroPixel.FULL);
                    } else {
                        result.add(fullTrace.get(idx));
                    }
                } else {
                    result.add(MicroPixel.EMPTY);
                }
            }
        }
        return result;
    }

    private List<MicroPixel> getFullBlock(int before, int blockWidth, int after) {
        List<MicroPixel> full = new ArrayList<>();
        // trace before
        if (before > 0) {
            final int beforeStep = 255 / before;
            for (int i = 0; i < before; i++) {
                full.add(MicroPixel.get(
                        i * beforeStep,
                        (i + 1) * beforeStep
                ));
            }
        }

        // main block
        for (int i = 0; i < blockWidth; i++) {
            full.add(MicroPixel.FULL);
        }

        // trace after
        if (after > 0) {
            final int afterStep = 255 / after;
            for (int i = 0; i < after; i++) {
                full.add(MicroPixel.get(
                        (after - i) * afterStep,
                        (after - i - 1) * afterStep
                ));
            }
        }
        return full;
    }
}
