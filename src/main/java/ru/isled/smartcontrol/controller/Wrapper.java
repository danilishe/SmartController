/*
package ru.isled.smartcontrol.controller;

import ru.isled.smartcontrol.model.LedFrame;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.model.WrappedLedFrame;
import ru.isled.smartcontrol.model.wraps.ProjectWrapper;

import java.util.stream.Collectors;

public class Wrapper {
    public static ProjectWrapper wrap(Project project) {
        return new ProjectWrapper()
                .setFrameCount(project.getFrameCount())
                .setPixelCount(project.getPixelCount())
                .setQuantifiers(project.getPixels())
                .setGamma(project.getGamma())
                .setFrames(project.getFrames().stream()
                        .map(Wrapper::wrapLedFrame)
                        .collect(Collectors.toList()));
    }

    public static Project unwrap(ProjectWrapper projectWrapper) {
        return new Project()
                .setPixelCount(projectWrapper.getPixelCount())
                .setFrameCount(projectWrapper.getFrameCount())
                .setPixels(projectWrapper.getQuantifiers())
                .setFrames(
                        projectWrapper.getFrames().stream()
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
*/
