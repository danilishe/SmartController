package ru.isled.smartcontrol.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.isled.smartcontrol.Constants.MAX_BRIGHT;
import static ru.isled.smartcontrol.Constants.MIN_BRIGHT;

class PixelEffectTest {
    @Test
    public void testFlashLength() {
        assertEquals(PixelEffect.Вспышка.getInterpolatedPixel(2).length, 2);
        assertEquals(PixelEffect.Вспышка.getInterpolatedPixel(3).length, 3);
    }
    @Test
    public void testFlashExtrems() {
        assertEquals(PixelEffect.Вспышка.getInterpolatedPixel(2)[0], MIN_BRIGHT);
        assertEquals(PixelEffect.Вспышка.getInterpolatedPixel(2)[1], MIN_BRIGHT);
    }
    @Test
    public void testFlashMiddle() {
        assertEquals(PixelEffect.Вспышка.getInterpolatedPixel(3)[1], MAX_BRIGHT);
        assertEquals(PixelEffect.Вспышка.getInterpolatedPixel(7)[3], MAX_BRIGHT);
        assertEquals(PixelEffect.Вспышка.getInterpolatedPixel(9)[4], MAX_BRIGHT);
    }
    @Test
    public void testNegaFlashLength() {
        assertEquals(PixelEffect.Миг.getInterpolatedPixel(2).length, 2);
        assertEquals(PixelEffect.Миг.getInterpolatedPixel(3).length, 3);
    }
    @Test
    public void testNegaFlashExtrems() {
        assertEquals(PixelEffect.Миг.getInterpolatedPixel(2)[0], MAX_BRIGHT);
        assertEquals(PixelEffect.Миг.getInterpolatedPixel(2)[1], MAX_BRIGHT);
    }
    @Test
    public void testNegaFlashMiddle() {
        assertEquals(PixelEffect.Миг.getInterpolatedPixel(3)[1], MIN_BRIGHT);
        assertEquals(PixelEffect.Миг.getInterpolatedPixel(7)[3], MIN_BRIGHT);
        assertEquals(PixelEffect.Миг.getInterpolatedPixel(9)[4], MIN_BRIGHT);
    }
}