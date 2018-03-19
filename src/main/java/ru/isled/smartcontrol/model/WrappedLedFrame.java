package ru.isled.smartcontrol.model;

import java.util.List;

public class WrappedLedFrame {
    private int frameLength;
    private int frameCycles;
    private List<Integer> pixels;

    public int getFrameLength() {
        return frameLength;
    }

    public void setFrameLength(int frameLength) {
        this.frameLength = frameLength;
    }

    public int getFrameCycles() {
        return frameCycles;
    }

    public void setFrameCycles(int frameCycles) {
        this.frameCycles = frameCycles;
    }

    public List<Integer> getPixels() {
        return pixels;
    }

    public void setPixels(List<Integer> pixels) {
        this.pixels = pixels;
    }
}
