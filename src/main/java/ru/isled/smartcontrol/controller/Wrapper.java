package ru.isled.smartcontrol.controller;

import ru.isled.smartcontrol.model.LedFrame;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.model.WrappedLedFrame;
import ru.isled.smartcontrol.model.WrappedProject;

import java.util.stream.Collectors;

public class Wrapper {
    public static WrappedProject wrap(Project project) {
        return new WrappedProject()
                .setFrameCount(project.getFrameCount())
                .setPixelCount(project.getPixelCount())
                .setQuantifiers(project.getPixels())
                .setGamma(project.getGamma())
                .setFrames(project.getFrames().stream()
                        .map(Wrapper::wrapLedFrame)
                        .collect(Collectors.toList()));
    }

    public static Project unwrap(WrappedProject wrappedProject) {
        return new Project()
                .setPixelCount(wrappedProject.getPixelCount())
                .setFrameCount(wrappedProject.getFrameCount())
                .setPixels(wrappedProject.getQuantifiers())
                .setFrames(
                        wrappedProject.getFrames().stream()
                                .map(Wrapper::unwrapLedFrame)
                                .collect(Collectors.toList())
                );
    }

    private static WrappedLedFrame wrapLedFrame(LedFrame ledFrame) {
        return new WrappedLedFrame()
                .setFrameCycles(ledFrame.getCycles().get())
                .setFrameLength(ledFrame.getFrameLength().get())
                .setPixels(ledFrame.getPixels());
    }

    private static LedFrame unwrapLedFrame(WrappedLedFrame wrappedLedFrame) {
        return new LedFrame()
                .setCycles(wrappedLedFrame.getFrameCycles())
                .setFrameLength(wrappedLedFrame.getFrameLength())
                .setPixels(wrappedLedFrame.getPixels());
    }
}
