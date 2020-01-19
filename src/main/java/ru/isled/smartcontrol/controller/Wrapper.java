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
                .setPixelCount(project.getChanelCount())
                .setQuantifiers(project.getQuantifiers())
                .setGamma(project.getGamma())
                .setFrames(project.getData().stream()
                        .map(Wrapper::wrapLedFrame)
                        .collect(Collectors.toList()));
    }

    public static Project unwrap(WrappedProject wrappedProject) {
        return new Project()
                .setChanelCount(wrappedProject.getPixelCount())
                .setFrameCount(wrappedProject.getFrameCount())
                .setQuantifiers(wrappedProject.getQuantifiers())
                .setData(
                        wrappedProject.getFrames().stream()
                                .map(Wrapper::unwrapLedFrame)
                                .collect(Collectors.toList())
                );
    }

    private static WrappedLedFrame wrapLedFrame(LedFrame ledFrame) {
        return new WrappedLedFrame().setFrameCycles(ledFrame.getCycles().get())
                .setFrameLength(ledFrame.getFrameLength().get())
                .setPixels(ledFrame.getPixels());
    }

    private static LedFrame unwrapLedFrame(WrappedLedFrame wrappedLedFrame) {
        LedFrame ledFrame = new LedFrame();
        ledFrame.setCycles(wrappedLedFrame.getFrameCycles());
        ledFrame.setFrameLength(wrappedLedFrame.getFrameLength());
        ledFrame.setPixels(wrappedLedFrame.getPixels());
        return ledFrame;
    }
}
