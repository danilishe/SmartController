package ru.isled.controlit.model;

import java.security.InvalidParameterException;
import java.util.Arrays;

public enum PixelEffect {

    Разгорание(12345, "fadeIn"),
    Мерцание(90909, "blink"),
    Угасание(54321, "fadeOut"),
    МерцающееРазгорание(19395, "mFadeIn"),
    Хаос(75381, "chaos"),
    МерцающееУгасание(59391, "mFadeOut");

    int code;
    String cssClass;

    PixelEffect(int code, String cssClass) {
        this.code = code;
        this.cssClass = cssClass;
    }

    public static PixelEffect byIndex(int index) {
        return Arrays.stream(values())
                .filter(e -> e.code == index)
                .findFirst()
                .orElseThrow(() -> new InvalidParameterException("Отсутствует пиксельный эффект с индексом: " + index));
    }

    public static String cssByIndex(int index) {
        return Arrays.stream(values())
                .filter(e -> e.code == index)
                .findFirst()
                .orElseThrow(() -> new InvalidParameterException("Отсутствует пиксельный эффект с индексом: " + index))
                .cssClass();
    }


    public String cssClass() {
        return cssClass;
    }

    public int index() {
        return code;
    }
}
