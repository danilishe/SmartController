package ru.isled.smartcontrol.controller;

import java.util.List;

public class ProgramProperties {
    private double width;
    private double height;
    private boolean showDigits;
    private boolean showBright;
    private double zoom;
    private List<String> lastFiles;

    public List<String> getLastFiles() {
        return lastFiles;
    }

    public void setLastFiles(List<String> lastFiles) {
        this.lastFiles = lastFiles;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public boolean isShowDigits() {
        return showDigits;
    }

    public void setShowDigits(boolean showDigits) {
        this.showDigits = showDigits;
    }

    public boolean isShowBright() {
        return showBright;
    }

    public void setShowBright(boolean showBright) {
        this.showBright = showBright;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }
}
