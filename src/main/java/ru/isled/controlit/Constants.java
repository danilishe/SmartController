package ru.isled.controlit;

public interface Constants {
    int MIN_FRAMES = 1;
    int MAX_FRAMES = 200;
    int MIN_PIXELS_COUNT = 1;
    int MAX_PIXELS_COUNT = 40;
    int DEFAULT_PIXEL_COUNT = 8;
    int MIN_BRIGHT = 0;
    int MAX_BRIGHT = 255;

    // минимально возможная для контроллера длина кадра
    int BASE_FRAME_LENGTH = 25;

    // дефолтная длина кадра в программе
    int DEFAULT_FRAME_LENGTH = 100;

    // минимальная длина кадра в программе
    int MIN_FRAME_LENGTH = BASE_FRAME_LENGTH * 2;

    // шаг длины кадра
    int FRAME_LENGTH_STEP = MIN_FRAME_LENGTH;

    int MAX_FRAME_LENGTH = 20000;
    String DEFAULT_PROJECT_FILE_NAME = "project";
    String DEFAULT_EXPORT_FILE_NAME = "data";

    String UNSAVED_FILE_NAME = " <несохранённый проект> ";
    String DEFAULT_WORK_DIRECTORY = System.getProperty("user.home");// + "/Documents/";
    int DEFAULT_FRAMES_COUNT = 10;
    int SYS_COLS = 3;
    String DEFAULT_CELL_STYLE = "-fx-alignment: CENTER;";
    int INIT_COL_WIDTH = 40;
    int MIN_COL_WIDTH = 25;
    int MAX_COL_WIDTH = 80;
}
