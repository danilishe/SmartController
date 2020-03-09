package ru.isled.smartcontrol.model.wraps;

import java.util.List;

public class ProjectWrapper {
    private List<LedFrameWrapper> frames;
    private List<PixelWrapper> pixels;
    private int frameCount;
    private int pixelCount;
    private double gamma;

    public List<LedFrameWrapper> getFrames() {
        return frames;
    }

    public ProjectWrapper setFrames(List<LedFrameWrapper> frames) {
        this.frames = frames;
        return this;
    }

    public List<PixelWrapper> getPixels() {
        return pixels;
    }

    public ProjectWrapper setPixels(List<PixelWrapper> pixels) {
        this.pixels = pixels;
        return this;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public ProjectWrapper setFrameCount(int frameCount) {
        this.frameCount = frameCount;
        return this;
    }

    public int getPixelCount() {
        return pixelCount;
    }

    public ProjectWrapper setPixelCount(int pixelCount) {
        this.pixelCount = pixelCount;
        return this;
    }

    public double getGamma() {
        return gamma;
    }

    public ProjectWrapper setGamma(double gamma) {
        this.gamma = gamma;
        return this;
    }
}
