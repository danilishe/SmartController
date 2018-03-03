package ru.isled.controlit.controller;

import ru.isled.controlit.model.PixelEffect;
import ru.isled.controlit.model.WrappedLedFrame;
import ru.isled.controlit.model.WrappedProject;

import java.util.ArrayList;
import java.util.List;

import static ru.isled.controlit.Constants.*;

public class Converter {

    public static byte[] encode(WrappedProject wrappedProject) {
        int framesCount = wrappedProject.getFrameCount();
        List<WrappedLedFrame> frames = wrappedProject.getFrames();

        removeUnusedFrames(framesCount, frames);

        List<Byte> allData = new ArrayList<>();
        for (WrappedLedFrame frame : frames) {
            int baseLengthCount = frame.getFrameLength() / BASE_FRAME_LENGTH;
            int[][] frameInterpolatedData = new int[baseLengthCount][MAX_PIXELS_COUNT];

            // перебираем каждый пиксель текущего кадра
            for (int pixel = 0; pixel < MAX_PIXELS_COUNT; pixel++) {
                int pixelBright = frame.getPixels().get(pixel);
                if (pixelBright <= MAX_BRIGHT) {
                    // заполняем статичной яркостью
                    for (int i = 0; i < baseLengthCount; i++) {
                        frameInterpolatedData[i][pixel] = pixelBright;
                    }
                } else {
                    // применяем интерполированную цепочку из класса пиксельных эффектов
                    PixelEffect effect = PixelEffect.byIndex(pixelBright);
                    int[] inerpolatedPixel = effect.getInterpolatedPixel(baseLengthCount);
                    for (int i = 0; i < baseLengthCount; i++) {
                        frameInterpolatedData[i][pixel] = inerpolatedPixel[i];
                    }
                }
            }

            copyArrayToList(frameInterpolatedData, allData);
        }

        byte[] result = new byte[allData.size()];
        for (int i = 0; i < allData.size(); i++) {
            result[i] = (byte) allData.get(i);
        }

        return result;
    }

    private static void removeUnusedFrames(int framesCount, List<WrappedLedFrame> frames) {
        while (framesCount < frames.size())
            frames.remove(framesCount);
    }

    private static void copyArrayToList(int[][] frameInterpolatedData, List<Byte> allData) {
        for (int i = 0; i < frameInterpolatedData.length; i++) {
            for (int j = 0; j < frameInterpolatedData[i].length; j++) {
                allData.add((byte) frameInterpolatedData[i][j]);
            }
        }
    }
}
