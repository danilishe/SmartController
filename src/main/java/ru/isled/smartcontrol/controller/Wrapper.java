package ru.isled.smartcontrol.controller;

import ru.isled.smartcontrol.model.LedFrame;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.model.WrappedLedFrame;
import ru.isled.smartcontrol.model.WrappedProject;

import java.util.stream.Collectors;

public class Wrapper {
    public static WrappedProject wrap(Project project) {
        WrappedProject wrappedProject = new WrappedProject();
        wrappedProject.setFrameCount(project.getFrameCount());
        wrappedProject.setPixelCount(project.getPixelCount());

        wrappedProject.setFrames(project.getData().stream()
                .map(Wrapper::wrapLedFrame)
                .collect(Collectors.toList()));
        return wrappedProject;
    }

    public static Project unwrap(WrappedProject wrappedProject) {
        Project project = new Project();
        project.setPixelCount(wrappedProject.getPixelCount());
        project.setFrameCount(wrappedProject.getFrameCount());
        project.setData(
                wrappedProject.getFrames().stream()
                        .map(wrappedLedFrame -> unwrapLedFrame(wrappedLedFrame))
                        .collect(Collectors.toList())
        );
        return project;
    }

    private static WrappedLedFrame wrapLedFrame(LedFrame ledFrame) {
        WrappedLedFrame wrappedLedFrame = new WrappedLedFrame();
        wrappedLedFrame.setFrameCycles(ledFrame.getCycles().get());
        wrappedLedFrame.setFrameLength(ledFrame.getFrameLength().get());
        wrappedLedFrame.setPixels(ledFrame.getPixels());
        return wrappedLedFrame;
    }

    private static LedFrame unwrapLedFrame(WrappedLedFrame wrappedLedFrame) {
        LedFrame ledFrame = new LedFrame();
        ledFrame.setCycles(wrappedLedFrame.getFrameCycles());
        ledFrame.setFrameLength(wrappedLedFrame.getFrameLength());
        ledFrame.setPixels(wrappedLedFrame.getPixels());
        return ledFrame;
    }
}
