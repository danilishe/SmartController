package ru.isled.controlit.model;

import javafx.beans.property.IntegerProperty;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Random;

import static ru.isled.controlit.Constants.MAX_BRIGHT;
import static ru.isled.controlit.Constants.MIN_BRIGHT;

public enum Effect {
    Разгорание {
        @Override
        public void apply(List<IntegerProperty> list,
                          Integer col, Integer row,
                          Integer startVal, Integer endVal,
                          int var) {

            if (startVal == null) startVal = MIN_BRIGHT;
            if (endVal == null) endVal = MAX_BRIGHT;

            int range = endVal - startVal;
            double step = row > 1 ? (double) range / (row - 1)
                    : range;

            for (int r = 0; r < row; r++) {
                for (int c = 0; c < col; c++) {
                    int currentPixelNum = r * col + c;
                    if (currentPixelNum < list.size()) {
                        list.get(currentPixelNum).setValue(
                                startVal + (int) (r * step)
                        );
                    }
                }
            }
        }
    },

    Угасание {
        @Override
        public void apply(List<IntegerProperty> list,
                          Integer col, Integer row,
                          Integer startVal, Integer endVal, int variant) {

            if (startVal == null) startVal = MAX_BRIGHT;
            if (endVal == null) endVal = MIN_BRIGHT;

            Разгорание.apply(list, col, row, startVal, endVal, variant);

        }
    },

    Случайно {
        @Override
        public void apply(List<IntegerProperty> list,
                          Integer col, Integer row,
                          Integer min, Integer max,
                          int variant) {

            if (min == null) min = MIN_BRIGHT;
            int range = max == null ? MAX_BRIGHT - min : max - min;

            Random random = new Random();
            for (IntegerProperty i : list) {
                i.setValue(random.nextInt(range) + min);
            }
        }
    },

    Блик {
        @Override
        public void apply(List<IntegerProperty> list, Integer col, Integer row, Integer startVal, Integer endVal, int variant) {
            throw new NotImplementedException();
        }
    },

    Наполнение {
        public static final int ВЛЕВО = 1;
        public static final int ВПРАВО = 2;
        public static final int В_ЦЕНТР = 3;
        public static final int К_КРАЯМ = 4;

        @Override
        public void apply(List<IntegerProperty> list, Integer col, Integer row, Integer startVal, Integer endVal, int effectVariant) {
            throw new NotImplementedException();
        }
    };

    public abstract void apply(List<IntegerProperty> list,
                               Integer col, Integer row,
                               Integer startVal, Integer endVal,
                               int effectVariant);
}
