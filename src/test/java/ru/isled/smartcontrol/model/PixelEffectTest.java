
package ru.isled.smartcontrol.model;

import javafx.scene.paint.Color;
import org.testng.annotations.Test;
import ru.isled.smartcontrol.model.effect.PixelEffect;

import static org.testng.AssertJUnit.assertEquals;

public class PixelEffectTest {
    @Test
    public void testFlashLength() {
        assertEquals(2, PixelEffect.FadeInOut.interpolate(2, Color.BLACK, Color.WHITE).length);
        assertEquals(3, PixelEffect.FadeInOut.interpolate(3, Color.BLACK, Color.WHITE).length);
    }

    @Test
    public void testFlashExtrems() {
        Color[] effect = PixelEffect.FadeInOut.interpolate(5, Color.WHITE, Color.WHITE);
        assertEquals("start", Color.BLACK, effect[0]);
        assertEquals("middle", Color.WHITE, effect[2]);
        assertEquals("end", Color.BLACK, effect[4]);
    }

    @Test
    public void testSolidExtrems2() {
        Color[] effect = PixelEffect.Solid.interpolate(2, Color.WHITE, Color.BLACK);
        assertEquals("start", Color.WHITE, effect[0]);
        assertEquals("end", Color.BLACK, effect[1]);
    }

    @Test
    public void testSolidExtrems3() {
        Color[] effect = PixelEffect.Solid.interpolate(3, Color.WHITE, Color.BLACK);
        assertEquals("start", Color.WHITE, effect[0]);
        assertEquals("end", Color.BLACK, effect[2]);
    }
}
