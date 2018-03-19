package ru.isled.smartcontrol.model;

import java.util.List;

public class WrappedProject {
    private List<WrappedLedFrame> frames;
    private int frameCount;
    private int pixelCount;

    public List<WrappedLedFrame> getFrames() {
        return frames;
    }

    public void setFrames(List<WrappedLedFrame> frames) {
        this.frames = frames;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    public int getPixelCount() {
        return pixelCount;
    }

    public void setPixelCount(int pixelCount) {
        this.pixelCount = pixelCount;
    }
}
