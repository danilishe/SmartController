package ru.isled.smartcontrol.controller;

import ru.isled.smartcontrol.view.Dialogs;

import java.io.*;

public class FileHelper {

    public static boolean save(File file, byte[] data) {
        try (OutputStream bw = new BufferedOutputStream(new FileOutputStream(file))) {
            bw.write(data);
            bw.flush();
        } catch (IOException ioe) {
            System.err.println();
            Dialogs.showErrorAlert("Ошибка при записи файла!");
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    public static byte[] load(File file) {
        byte[] data = new byte[(int) file.length()];
        try(InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            is.read(data);
        } catch (IOException ioe) {
            Dialogs.showErrorAlert("Ошибка загрузки файла!");
            ioe.printStackTrace();
            return null;
        }
        return data;
    }
}
