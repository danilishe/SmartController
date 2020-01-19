package ru.isled.smartcontrol.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.isled.smartcontrol.Constants.MAX_BRIGHT;
import static ru.isled.smartcontrol.Constants.MIN_BRIGHT;

class PixelEffectTest {
    @Test
    public void testFlashLength() {
        assertEquals(PixelEffect.FadeInOut.getColors(2).length, 2);
        assertEquals(PixelEffect.FadeInOut.getColors(3).length, 3);
    }
    @Test
    public void testFlashExtrems() {
        assertEquals(PixelEffect.FadeInOut.getColors(2)[0], MIN_BRIGHT);
        assertEquals(PixelEffect.FadeInOut.getColors(2)[1], MIN_BRIGHT);
    }
    @Test
    public void testFlashMiddle() {
        assertEquals(PixelEffect.FadeInOut.getColors(3)[1], MAX_BRIGHT);
        assertEquals(PixelEffect.FadeInOut.getColors(7)[3], MAX_BRIGHT);
        assertEquals(PixelEffect.FadeInOut.getColors(9)[4], MAX_BRIGHT);
    }
    @Test
    public void testNegaFlashLength() {
        assertEquals(PixelEffect.FadeOutIn.getColors(2).length, 2);
        assertEquals(PixelEffect.FadeOutIn.getColors(3).length, 3);
    }
    @Test
    public void testNegaFlashExtrems() {
        assertEquals(PixelEffect.FadeOutIn.getColors(2)[0], MAX_BRIGHT);
        assertEquals(PixelEffect.FadeOutIn.getColors(2)[1], MAX_BRIGHT);
    }
    @Test
    public void testNegaFlashMiddle() {
        assertEquals(PixelEffect.FadeOutIn.getColors(3)[1], MIN_BRIGHT);
        assertEquals(PixelEffect.FadeOutIn.getColors(7)[3], MIN_BRIGHT);
        assertEquals(PixelEffect.FadeOutIn.getColors(9)[4], MIN_BRIGHT);
    }
}