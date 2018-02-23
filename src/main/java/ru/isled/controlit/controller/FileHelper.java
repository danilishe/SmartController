package ru.isled.controlit.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {
    public static void save(File file, List<Byte> data) throws Exception {
        OutputStream bw = new BufferedOutputStream(new FileOutputStream(file));
        for (int i = 0; i < data.size(); i++) {
            bw.write(data.get(i));
        }
        bw.flush();
        bw.close();
    }

    public static List<Byte> load(File file) throws Exception {
        byte[] data = new byte[(int) file.length()];
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        is.read(data);
        is.close();

        List<Byte> result  = new ArrayList<>();
        for (byte b : data) {
            result.add(b);
        }
        return result;
    }
}
