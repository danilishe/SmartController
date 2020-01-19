package ru.isled.smartcontrol.model;

import java.util.List;

public class WrappedProject {
    private List<Integer> quantifiers;
    private List<WrappedLedFrame> frames;
    private int frameCount;
    private int pixelCount;
    private double gamma;

    public List<Integer> getQuantifiers() {
        return quantifiers;
    }

    public WrappedProject setQuantifiers(List<Integer> quantifiers) {
        this.quantifiers = quantifiers;
        return this;
    }

    public List<WrappedLedFrame> getFrames() {
        return frames;
    }

    public WrappedProject setFrames(List<WrappedLedFrame> frames) {
        this.frames = frames;
        return this;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public WrappedProject setFrameCount(int frameCount) {
        this.frameCount = frameCount;
        return this;
    }

    public int getPixelCount() {
        return pixelCount;
    }

    public WrappedProject setPixelCount(int pixelCount) {
        this.pixelCount = pixelCount;
        return this;
    }

    public double getGamma() {
        return gamma;
    }

    public WrappedProject setGamma(double gamma) {
        this.gamma = gamma;
        return this;
    }
}
