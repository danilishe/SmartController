package ru.isled.smartcontrol.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.isled.smartcontrol.model.LedFrame;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.model.WrappedProject;

import java.util.Arrays;

class WrapperTest {

    @Test
    void unwrap() {
        final Project expected = new Project()
                .setChanelCount(5)
                .setQuantifiers(Arrays.asList(1, 2, 3))
                .setFrameCount(8)
                .setData(Arrays.asList(
                        new LedFrame().setCycles(4).setLength(1),
                        new LedFrame().setLength(5).setCycles(1)))
                .setGamma(2.2);
        final WrappedProject wrapped = Wrapper.wrap(expected);
        Assertions.assertEquals(expected, Wrapper.unwrap(wrapped));

    }
}