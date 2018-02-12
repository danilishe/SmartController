package ru.isled.controlit.model;

import javafx.beans.property.IntegerProperty;
import ru.isled.controlit.view.MainController;

import java.util.List;
import java.util.Random;

public enum Effect {
    Разгорание {
        @Override
        public void apply(List<IntegerProperty> list,
                           Integer col,  Integer row,
                           Integer startVal,  Integer endVal) {

            if (startVal == null) startVal = MainController.MIN_BRIGHT;
            if (endVal == null) endVal = MainController.MAX_BRIGHT;

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
                           Integer col,  Integer row,
                           Integer startVal,  Integer endVal) {

            if (startVal == null) startVal = MainController.MAX_BRIGHT;
            if (endVal == null) endVal = MainController.MIN_BRIGHT;

            Разгорание.apply(list, col, row, startVal, endVal);

        }
    },

    Случайно {
        @Override
        public void apply(List<IntegerProperty> list,
                           Integer col,  Integer row,
                           Integer min,  Integer max) {

            if (min == null) min = MainController.MIN_BRIGHT;
            int range = max == null ? MainController.MAX_BRIGHT - min : max - min;

            Random random = new Random();
            for (IntegerProperty i : list) {
                i.setValue(random.nextInt(range) + min);
            }
        }
    },

    Блик;

    public void apply(List<IntegerProperty> list,
                       Integer col,  Integer row,
                       Integer startVal,  Integer endVal) {
        throw new UnsupportedOperationException("This effect didn't ready yet... :(");
    }
}
