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

        frames = frames.subList(0, framesCount);

        List<Byte> allData = new ArrayList<>();
        for (WrappedLedFrame frame : frames) {
            int baseLengthCount = frame.getFrameLength() / BASE_FRAME_LENGTH;
            int[][] subFrames = new int[baseLengthCount][MAX_CHANNELS_COUNT];
            int channelsHead = 0;

            // перебираем каждый пиксель текущего кадра
            mainCycle:
            for (int pixel = 0; pixel < MAX_CHANNELS_COUNT; pixel++) {
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
                        int[] currentPixelFrames = effect.interpolate(baseLengthCount);
                        for (int i = 0; i < baseLengthCount; i++) {
                            subFrames[i][channelsHead] = currentPixelFrames[i];
                        }
                    }

                    channelsHead++;
                    if (channelsHead == MAX_CHANNELS_COUNT) break mainCycle;

                }
            }

            for (int i = 0; i < frame.getFrameCycles(); i++) {
                copyArrayToList(subFrames, allData);
            }
        }

        byte[] result = new byte[allData.size()];
        for (int i = 0; i < allData.size(); i++) {
            // copy list to raw array and apply gamma value
            result[i] = (byte) (Math.pow((double) allData.get(i) / MAX_BRIGHT, wrappedProject.getGamma()) * MAX_BRIGHT);
        }

        return result;
    }

    private static void copyArrayToList(int[][] subFrames, List<Byte> allData) {
        for (int[] subFrame : subFrames) {
            for (int j = 0; j < subFrame.length; j++) {
                allData.add((byte) subFrame[j]);
            }
        }
    }
}
