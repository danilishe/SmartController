package ru.isled.smartcontrol.model;

import javafx.beans.property.IntegerProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static ru.isled.smartcontrol.Constants.MAX_BRIGHT;
import static ru.isled.smartcontrol.Constants.MIN_BRIGHT;

public enum Effect {
    Разгорание {
        @Override
        public void apply(List<IntegerProperty> cells,
                          Integer col, Integer row,
                          Integer min, Integer max,
                          Options... options) {

            if (min == null) min = MIN_BRIGHT;
            if (max == null) max = MAX_BRIGHT;

            int range = max - min;
            double step = row > 1 ? (double) range / (row - 1)
                    : range;

            for (int r = 0; r < row; r++) {
                for (int c = 0; c < col; c++) {
                    int currentPixelNum = r * col + c;
                    if (currentPixelNum < cells.size()) {
                        cells.get(currentPixelNum).setValue(
                                min + (int) (r * step)
                        );
                    }
                }
            }
        }
    },

    Угасание {
        @Override
        public void apply(List<IntegerProperty> cells,
                          Integer col, Integer row,
                          Integer min, Integer max, Options... options) {

            if (min == null) min = MAX_BRIGHT;
            if (max == null) max = MIN_BRIGHT;

            Разгорание.apply(cells, col, row, min, max, options);

        }
    },

    Случайно {
        @Override
        public void apply(List<IntegerProperty> cells,
                          Integer col, Integer row,
                          Integer min, Integer max,
                          Options... options) {

            if (min == null) min = MIN_BRIGHT;
            int range = max == null ? MAX_BRIGHT - min : max - min;

            Random random = new Random();
            for (IntegerProperty i : cells) {
                i.setValue(random.nextInt(range) + min);
            }
        }
    },

    Блик {
        @Override
        public void apply(List<IntegerProperty> cells, Integer col, Integer row, Integer min, Integer max, Options... options) {
            int glare;
            boolean inverted = Arrays.stream(options).anyMatch(o -> o == Options.Негативный);
            boolean onlyGlare = Arrays.stream(options).anyMatch(o -> o == Options.Поверх);

            if (inverted) {
                glare = min;
                if (!onlyGlare)
                    cells.forEach(cell -> cell.set(max));
            } else {
                if (!onlyGlare)
                    cells.forEach(cell -> cell.set(min));
                glare = max;
            }

            boolean rightDirection = Arrays.stream(options).anyMatch(o -> o == Options.Вправо);

            for (int i = 0; i < col; i++) {
                int cellNumber = rightDirection ? i + i * col
                        : i + (col - i - 1) * col;
                if (cellNumber < cells.size()) {
                    cells.get(cellNumber).set(glare);
                }
            }
        }
    },

    Наполнение {
        @Override
        public void apply(List<IntegerProperty> cells, Integer col, Integer row, Integer min, Integer max, Options... options) {
            int fill;
            boolean inverted = Arrays.stream(options).anyMatch(o -> o == Options.Негативный);
            boolean onlyFill = Arrays.stream(options).anyMatch(o -> o == Options.Поверх);

            if (inverted) {
                fill = min;
                if (!onlyFill)
                    cells.forEach(cell -> cell.set(max));
            } else {
                if (!onlyFill)
                    cells.forEach(cell -> cell.set(min));
                fill = max;
            }

            int last = 0;
            switch (options[0]) {
                case Вправо:
                    for (int i = 0; i < col; i++) {
                        for (int j = 0; j <= i; j++) {
                            int cellNum = i * col + j;
                            last = Math.max(cellNum, last);
                            if (cellNum < cells.size())
                                cells.get(cellNum).set(fill);
                        }
                    }
                    break;
                case Влево:
                    for (int i = 0; i < col; i++) {
                        for (int j = 0; j <= i; j++) {
                            int cellNum = (i + 1) * col - j - 1;
                            last = Math.max(cellNum, last);
                            if (cellNum < cells.size())
                                cells.get(cellNum).set(fill);
                        }
                    }
                    break;
                case В_центр:
                    for (int i = 0; i < col / 2; i++) {
                        for (int j = 0; j <= i; j++) {
                            int cellNum = (i + 1) * col - j - 1;
                            last = Math.max(cellNum, last);
                            if (cellNum < cells.size())
                                cells.get(cellNum).set(fill);
                            cellNum = i * col + j;
                            last = Math.max(cellNum, last);
                            if (cellNum < cells.size())
                                cells.get(cellNum).set(fill);
                        }
                    }
                    break;
                case Из_центра: //TODO FIXME глюк при некоторых значениях пикселей
                    int middle = col / 2;
                    for (int i = 0; i < middle; i++) {
                        for (int j = 0; j <= i; j++) {
                            int cellNum = i * col - j + middle;
                            last = Math.max(cellNum, last);
                            if (cellNum < cells.size())
                                cells.get(cellNum).set(fill);
                            cellNum = i * col + j + middle;
                            last = Math.max(cellNum, last);
                            if (cellNum < cells.size())
                                cells.get(cellNum).set(fill);
                        }
                    }
                    break;
            }
            for (int i = last; i < cells.size(); i++) {
                cells.get(i).set(fill);
            }
        }
    };

    public abstract void apply(List<IntegerProperty> cells,
                               Integer col, Integer row,
                               Integer min, Integer max,
                               Options... options);

    public enum Options {
        Влево, Вправо, В_центр, Из_центра, Поверх, Негативный
    }
}
