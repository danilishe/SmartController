package ru.isled.controlit.controller;

import ru.isled.controlit.model.LedFrame;
import ru.isled.controlit.model.Project;
import ru.isled.controlit.model.WrappedLedFrame;
import ru.isled.controlit.model.WrappedProject;

import java.util.stream.Collectors;

public class Wrapper {
    public WrappedProject wrap(Project project) {
        WrappedProject wrappedProject = new WrappedProject();
        wrappedProject.setFrameCount(project.getFrameCount());
        wrappedProject.setPixelCount(project.getPixelCount());

        wrappedProject.setFrames(project.getData().stream()
                .map(this::wrapLedFrame)
                .collect(Collectors.toList()));
        return new WrappedProject();
    }

    public Project unwrap(WrappedProject wrappedProject) {
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

    private WrappedLedFrame wrapLedFrame(LedFrame ledFrame) {
        WrappedLedFrame wrappedLedFrame = new WrappedLedFrame();
        wrappedLedFrame.setFrameCycles(ledFrame.getCycles().get());
        wrappedLedFrame.setFrameLength(ledFrame.getFrameLength().get());
        wrappedLedFrame.setPixels(ledFrame.getPixels());
        return wrappedLedFrame;
    }

    private LedFrame unwrapLedFrame(WrappedLedFrame wrappedLedFrame) {
        LedFrame ledFrame = new LedFrame();
        ledFrame.setCycles(wrappedLedFrame.getFrameCycles());
        ledFrame.setFrameLength(wrappedLedFrame.getFrameLength());
        ledFrame.setPixels(wrappedLedFrame.getPixels());
        return ledFrame;
    }
}
