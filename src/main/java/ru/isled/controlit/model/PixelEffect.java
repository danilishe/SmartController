package ru.isled.controlit.model;

import java.security.InvalidParameterException;

public enum PixelEffect {
    Разгорание, Мерцание, Угасание, МерцающееРазгорание, Хаос, МерцающееУгасание;

    public static final int РазгораниеКод = 12345;
    public static final int МерцаниеКод = 90909;
    public static final int УгасаниеКод = 54321;
    public static final int МерцающееРазгораниеКод = 19395;
    public static final int МерцающееУгасаниеКод = 59391;
    public static final int ХаосКод = 75381;

    public static PixelEffect byIndex(int cellValue) {
        switch (cellValue) {
            case РазгораниеКод: return Разгорание;
            case МерцаниеКод: return Мерцание;
            case УгасаниеКод: return Угасание;
            case МерцающееРазгораниеКод: return МерцающееРазгорание;
            case ХаосКод: return Хаос;
            case МерцающееУгасаниеКод: return МерцающееУгасание;
            default:
                throw new InvalidParameterException("Отсутствует пиксельный эффект с индексом: " + cellValue);
        }
    }
}
