package ru.isled.smartcontrol.controller;

import ru.isled.smartcontrol.model.PixelEffect;
import ru.isled.smartcontrol.model.WrappedLedFrame;
import ru.isled.smartcontrol.model.WrappedProject;

import java.util.ArrayList;
import java.util.List;

import static ru.isled.smartcontrol.Constants.*;

public class Converter {

    public static byte[] encode(WrappedProject wrappedProject) {
        int framesCount = wrappedProject.getFrameCount();
        List<WrappedLedFrame> frames = wrappedProject.getFrames();

        removeUnusedFrames(framesCount, frames);

        List<Byte> allData = new ArrayList<>();
        for (WrappedLedFrame frame : frames) {
            int baseLengthCount = frame.getFrameLength() / BASE_FRAME_LENGTH;
            int[][] subFrames = new int[baseLengthCount][MAX_PIXELS_COUNT];
            int channelsHead = 0;

            // перебираем каждый пиксель текущего кадра
            mainCycle:
            for (int pixel = 0; pixel < MAX_PIXELS_COUNT; pixel++) {
                int pixelBright = frame.getPixels().get(pixel);
                int pixelQuantifier = wrappedProject.getQuantifiers().get(pixel);

                for (int pixelCycle = 0; pixelCycle < pixelQuantifier; pixelCycle++) {

                    if (pixelBright <= MAX_BRIGHT) {
                        // заполняем статичной яркостью
                        for (int i = 0; i < baseLengthCount; i++) {
                            subFrames[i][channelsHead] = pixelBright;
                        }
                    } else {
                        // применяем интерполированную цепочку из класса пиксельных эффектов
                        PixelEffect effect = PixelEffect.byIndex(pixelBright);
                        int[] interpolatedPixel = effect.getInterpolatedPixel(baseLengthCount);
                        for (int i = 0; i < baseLengthCount; i++) {
                            subFrames[i][channelsHead] = interpolatedPixel[i];
                        }
                    }

                    channelsHead++;
                    if (channelsHead == MAX_PIXELS_COUNT) break mainCycle;

                }
            }

            for (int i = 0; i < frame.getFrameCycles(); i++) {
                copyArrayToList(subFrames, allData);
            }
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

    private static void copyArrayToList(int[][] subFrames, List<Byte> allData) {
        for (int[] subFrame : subFrames) {
            for (int j = 0; j < subFrame.length; j++) {
                allData.add((byte) subFrame[j]);
            }
        }
    }
}
