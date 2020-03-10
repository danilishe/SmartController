package ru.isled.smartcontrol.util;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GammaTest {
    @DataProvider
    public Object[][] gammas() {
        return new Object[][]{
                {0, 100d, 0},
                {255, 1.2d, 255},
                {102, 2.2d, 168},
                {153, .5d, 91},
                {255, .9d, 255},
                {123, 1d, 123}
        };
    }

    @Test(dataProvider = "gammas")
    public void testGammas(int original, double gamma, int expected) {
        Assert.assertEquals(Byte.toUnsignedInt(
                Util.withGamma((byte) original, gamma)), expected);
    }
}