package ru.isled.smartcontrol;

import javafx.stage.FileChooser;

public interface Constants {

    String PROGRAM_VERSION = "2.3.2";
    int MIN_FRAMES = 1;
    int MAX_FRAMES = 5_000;
    int MIN_PIXELS_COUNT = 1;
    int MAX_CHANNELS_COUNT = 45;
    int DEFAULT_PIXEL_COUNT = 8;
    int MIN_BRIGHT = 0;
    int MAX_BRIGHT = 255;

    // минимально возможная для контроллера длина кадра в мс
    int BASE_FRAME_LENGTH = 25;

    // дефолтная длина кадра в программе
    int DEFAULT_FRAME_LENGTH = 100;

    // минимальная длина кадра в программе
    int MIN_FRAME_LENGTH = BASE_FRAME_LENGTH * 2;

    // шаг длины кадра
    int FRAME_LENGTH_STEP = MIN_FRAME_LENGTH;

    int MAX_FRAME_LENGTH = 20000;
    String DEFAULT_PROJECT_FILE_NAME = "project.isc";
    String DEFAULT_EXPORT_FILE_NAME = "0.bin";
    String DEFAULT_TIME_FILE_NAME = "time.cfg";

    String UNSAVED_FILE_NAME = " <несохранённый проект> ";
    String DEFAULT_WORK_DIRECTORY = System.getProperty("user.home");// + "/Documents/";
    int DEFAULT_FRAMES_COUNT = 10;
    int HEADER_COLUMNS = 3;
    String DEFAULT_CELL_STYLE = "-fx-alignment: CENTER;";
    int INIT_COL_WIDTH = 70;
    int MIN_COL_WIDTH = 60;
    int MAX_COL_WIDTH = 140;
    FileChooser.ExtensionFilter PROJECT_EXT = new FileChooser.ExtensionFilter("ISLed проект для контроллера", "*.isc");
    FileChooser.ExtensionFilter BIN_EXT = new FileChooser.ExtensionFilter("ISLed файл для контроллера", "*.bin");
    FileChooser.ExtensionFilter TIME_CFG = new FileChooser.ExtensionFilter("Настройки времени для контроллера ISLed", "time.cfg");
    int MAX_CYCLES = 100;
    int MIN_CYCLES = 0;
    String PROPS_PATH = System.getProperty("user.home") + "/smartcontrol/properties.ini";
    int MIN_HEIGHT = 900;
    int MIN_WIDTH = 930;
    int MAX_LAST_FILES_COUNT = 10;
    String CONTROLLER_PAGE_URL = "http://is-led.ru/";
    String HINT_PREVIEW = "Просмотреть как будет выглядеть соозданная программа. Скорость выполнения может отличаться от скорости на контроллере в зависимости от настроек скорости на контроллере.";
    String HINT_ZOOM = "Регулировка ширины колонок таблицы. Поддерживается скролл мыши.";
    String HINT_SHOW_BRIGHT = "При активации переключателя в ячейке отображается яркость пиксела в виде заполнения. Чем шире заполнение ячейки, тем ярче будет работать пиксел";
    String HINT_SHOW_DIGITS = "При активации переключателя в ячейке отображается цифровое значение яркости пиксела. Чем больше значение, Тем ярче будет работать пиксел";
    String HINT_CHANNEL_COUNTER = "Общее количество каналов с учётом квантификации в пикселе. При превышении максимального количества каналов индикатор будет выделен и будет отображено предупреждающее сообщение.";
    String HINT_PROGRAM_LENGTH = "Общая длина созданной программы в чч:мм:сс с учётом всех повтором. Может отличаться от реальной длины программы в контроллере в зависимости от установленной скорости";
    String HINT_PIXELS_COUNT = "Количество пикселей в программе. Один пиксель может состоять из нескольких каналов, например в случае RGB пикселя или усиленного монохромного";
    String HINT_FRAME_COUNT = "Количество кадров в программе. Общая длина программы зависит как от количества кадров, так и их длины, повторов и скорости установленной на контроллере";
    String HINT_SELECT_ALL = "Выбрать все доступные ячейки-пикселы и кадры. Удобно для быстрого создания много-кадрового эффекта";
    String HINT_DESELECT = "Отменить выбор ячеек";
    String HINT_FRAME_PIXEL_TABE = "Для выбора нескольких ячеек квадратом зажмите SHIFT, либо CTRL для простого";
    String HINT_MULTIFRAME_EFFECT = "Многокадровые эффекты используются, когда невозможно реализовать эффект в рамках одного кадра или когда программа зависит от соседних пикселей.";
    String HINT_QUANTIFIER = "Квантификация пиксела. Пиксел может дублироваться на несколько соседних каналов (квантифицироваться). Необходимо в случаях, когда недостаточно стандартной мощности одного пиксела IS-Led";
    String HINT_REPEATS = "Количество повторений выбранных кадров. Каждый кадр будет повторяться в отдельности";
    String HINT_FRAME_LENGTH = "Длина кадра в миллисекундах";
    String HINT_SET_BRIGHT = "Указать конкретное значение яркости для выбранных пикселей. При помощи кнопок можно задать градации, а при помощи ползунка выбрать более точное значение. Поддерживается скролл мыши";
    String HINT_FRAME_EFFECT = "\nОднокадровые эффекты длятся столько, сколько указано в длине кадра, при этом эффект распределён по всему этому времени.";
    String HINT_IN_EFFECT = "Однокадровый эффект плавного разгорания." + HINT_FRAME_EFFECT;
    String HINT_BLINK_IN_EFFECT = "Однокадровый эффект плавного разгорания с мерцанием." + HINT_FRAME_EFFECT;
    String HINT_BLINK_EFFECT = "Однокадровый эффект мерцания. Быстрое переключение от минимальной яркости к максимальной." + HINT_FRAME_EFFECT;
    String HINT_OUT_IN_EFFECT = "Однокадровый эффект с плавным угасанием и затем разгоранием." + HINT_FRAME_EFFECT;
    String HINT_OUT_EFFECT = "Однокадровый эффект с плавным угасанием." + HINT_FRAME_EFFECT;
    String HINT_BLINK_OUT_EFFECT = "Однокадровый эффект с плавным угасанием и мерцанием с максимальной яркостью." + HINT_FRAME_EFFECT;
    String HINT_RANDOM_EFFECT = "Однокадровый эффект с произвольной сменой яркости. Яркость меняется у каждого пикселя индивидуально." + HINT_FRAME_EFFECT;
    String HINT_IN_OUT_EFFECT = "Однокадровый эффект с плавным разгоранием и затем плавным угасанием." + HINT_FRAME_EFFECT;
    String TOO_MUCH_CHANNELS_ERROR_HINT = "Экспортируется максимум " + MAX_CHANNELS_COUNT + " пикселей, остальные пиксели будут проигнорированы";
    String CHANNELS_COUNTER_HINT = "Общее количество пикселей, включая кратные каналы";
    double CHANNEL_PREVIEW_SIZE = 15.0;
    double DEFAULT_GAMMA = 2.2;
    int COLOR_PALETTE_GRAYS = 13;
    int COLOR_PALETTE_COLORS = 60;
    double PALETTE_COLOR_SIZE = 20;
    int MAX_QUANTIFIER = 5;
    int CUSTOM_COLORS_COUNT = 16;
    int CUSTOM_PALETTE_SIZE = 10;
    String ERROR_WHILE_SAVING_PROJECT = "Ошибка при сохранении проекта! Попробуйте снова.";
    String ERROR_WHILE_LOADING_PROJECT = "Ошибка при загрузке проекта!";
    String NOT_CORRECT_PROJECT_VERSION_WHILE_LOAD = "При чтении файла обнаружено несовпадение версии. Невозможно загрузить проект.";
    String WARNING_DIALOG_TITLE = "Внимание!";
    String ERROR_DIALOG_TITLE = "Ошибка!";
    String TITLE = "ISLed SMART Control (%s) %s";
    String EXPORT_ERROR_MESSAGE = "Не удалось экспортировать файл. Попробуйте ещё раз :(";
    String ERROR_WHILE_LOAD = "Ошибка загрузки файла!";
    String ERROR_WHILE_SAVE = "Ошибка при записи файла!";
    String FILE_NOT_EXISTS = "Файл не существует!";
    int MAX_TIME_RECORDS = 11;
}
