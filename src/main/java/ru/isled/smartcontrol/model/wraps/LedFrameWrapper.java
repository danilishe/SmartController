package ru.isled.smartcontrol.model.wraps;

public class LedFrameWrapper {
    private int number;
    private int frameLength;
    private int cycles;

    public int getNumber() {
        return number;
    }

    public LedFrameWrapper setNumber(int number) {
        this.number = number;
        return this;
    }

    public int getFrameLength() {
        return frameLength;
    }

    public LedFrameWrapper setFrameLength(int frameLength) {
        this.frameLength = frameLength;
        return this;
    }

    public int getCycles() {
        return cycles;
    }

    public LedFrameWrapper setCycles(int cycles) {
        this.cycles = cycles;
        return this;
    }
}
