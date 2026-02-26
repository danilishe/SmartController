package ru.isled.smartcontrol.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.isled.smartcontrol.Constants;
import ru.isled.smartcontrol.view.Dialogs;

import java.io.*;

public class FileHelper {
    private static final Logger log = LogManager.getLogger(FileHelper.class);

    public static boolean save(File file, byte[] data) {
        try (OutputStream bw = new BufferedOutputStream(new FileOutputStream(file))) {
            bw.write(data);
            bw.flush();
        } catch (IOException ioe) {
            Dialogs.showErrorAlert(Constants.ERROR_WHILE_SAVE);
            log.error(Constants.ERROR_WHILE_SAVE, ioe);
            return false;
        }
        return true;
    }

    public static byte[] load(File file) {
        byte[] data = new byte[(int) file.length()];
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            is.read(data);
        } catch (IOException ioe) {
            Dialogs.showErrorAlert(Constants.ERROR_WHILE_LOAD);
            log.error(Constants.ERROR_WHILE_LOAD, ioe);
            return null;
        }
        return data;
    }
}
