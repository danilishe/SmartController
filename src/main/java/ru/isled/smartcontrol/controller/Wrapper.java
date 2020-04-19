package ru.isled.smartcontrol.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import ru.isled.smartcontrol.model.LedFrame;
import ru.isled.smartcontrol.model.Pixel;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.model.effect.RgbMode;
import ru.isled.smartcontrol.model.wraps.LedFrameWrapper;
import ru.isled.smartcontrol.model.wraps.PixelFrameWrapper;
import ru.isled.smartcontrol.model.wraps.PixelWrapper;
import ru.isled.smartcontrol.model.wraps.ProjectWrapper;
import ru.isled.smartcontrol.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Wrapper {
    public static ProjectWrapper wrap(Project project) {
        return new ProjectWrapper()
                .setFrameCount(project.framesCount())
                .setPixelCount(project.pixelsCount())
                .setGamma(project.getGamma())
                .setFrames(project.getFrames().stream()
                        .map(Wrapper::wrap)
                        .collect(Collectors.toList()))
                .setPixels(project.getPixels().stream()
                        .map(Wrapper::wrap)
                        .collect(Collectors.toList()));
    }

    public static Project unwrap(ProjectWrapper projectWrapper) {
        List<Pixel> pixels = projectWrapper.getPixels().stream()
                .map(Wrapper::unwrap)
                .collect(Collectors.toList());
        List<LedFrame> ledFrames = new ArrayList<>();
        for (int i = 0; i < projectWrapper.getFrames().size(); i++) {
            int finalI = i;
            ledFrames.add(
                    unwrap(projectWrapper.getFrames().get(i), pixels.stream()
                            .map(pixel -> pixel.getFrames().get(finalI).backgroundProperty())
                            .collect(Collectors.toList())));
        }
        return new Project(projectWrapper.getFrameCount(), projectWrapper.getPixelCount(), pixels, ledFrames, projectWrapper.getGamma(), null);
    }

    private static LedFrameWrapper wrap(LedFrame ledFrame) {
        return new LedFrameWrapper()
                .setCycles(ledFrame.getCycles())
                .setFrameLength(ledFrame.getLength())
                .setNumber(ledFrame.getNumber());
    }

    private static LedFrame unwrap(LedFrameWrapper wrappedLedFrame, List<StringProperty> values) {
        return new LedFrame(
                wrappedLedFrame.getNumber(),
                wrappedLedFrame.getFrameLength(),
                wrappedLedFrame.getCycles(),
                values
        );
    }

    private static PixelWrapper wrap(Pixel pixel) {
        return new PixelWrapper()
                .setNumber(pixel.getNumber())
                .setQuantifier(pixel.getQuantifier())
                .setRgbMode(pixel.getRgbMode())
                .setFrames(pixel.getFrames().stream()
                        .map(Wrapper::wrap)
                        .collect(Collectors.toList()));
    }

    private static Pixel unwrap(PixelWrapper wrappedPixel) {
        Pixel pixel = new Pixel(wrappedPixel.getNumber(), wrappedPixel.getRgbMode(), wrappedPixel.getQuantifier(), null);
        return pixel.setFrames(wrappedPixel.getFrames().stream()
                .map(wFrame -> unwrap(wFrame, pixel.rgbModeProperty()))
                .collect(Collectors.toList()));
    }

    private static PixelFrameWrapper wrap(Pixel.Frame pixelFrame) {
        return new PixelFrameWrapper()
                .setEffect(pixelFrame.getEffect())
                .setStartColor(Util.toHex(pixelFrame.getStartColor()))
                .setEndColor(Util.toHex(pixelFrame.getEndColor()));
    }

    private static Pixel.Frame unwrap(PixelFrameWrapper wrappedPixelFrame, ObjectProperty<RgbMode> rgbModeObjectProperty) {
        return new Pixel.Frame(
                Color.valueOf("#" + wrappedPixelFrame.getStartColor()),
                Color.web("#" + wrappedPixelFrame.getEndColor()),
                wrappedPixelFrame.getEffect(),
                rgbModeObjectProperty);
    }
}
