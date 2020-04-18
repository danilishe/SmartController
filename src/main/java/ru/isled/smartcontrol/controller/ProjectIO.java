package ru.isled.smartcontrol.controller;

import com.alibaba.fastjson.JSON;
import ru.isled.smartcontrol.Constants;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.model.wraps.ProjectWrapper;
import ru.isled.smartcontrol.view.Dialogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ProjectIO {

    private static final String ENTRY_NAME = "wrappedLedProject";

    public static Project load(File file) {
        assert file != null;
        try (
                FileInputStream input = new FileInputStream(file);
                ZipInputStream zipInputStream = new ZipInputStream(input);
        ) {
            ZipEntry nextEntry = zipInputStream.getNextEntry();
            if (nextEntry == null || !nextEntry.getName().equals(ENTRY_NAME)) {
                Dialogs.warn(Constants.NOT_CORRECT_PROJECT_VERSION_WHILE_LOAD);
            }
            ProjectWrapper wrappedProject = JSON.parseObject(zipInputStream, ProjectWrapper.class);
            Project project = Wrapper.unwrap(wrappedProject);
            project.setFileName(file);
            return project;
        } catch (Exception e) {
            Dialogs.showErrorAlert(Constants.ERROR_WHILE_LOADING_PROJECT + "\n\n" + e);
        }
        return null;
    }

    public static void save(Project project, File file) {
        assert file != null;
        ProjectWrapper wrappedProject = Wrapper.wrap(project);
        String json = JSON.toJSONString(wrappedProject);
        try (
                FileOutputStream out = new FileOutputStream(file);
                ZipOutputStream zipOutputStream = new ZipOutputStream(out)
        ) {
            zipOutputStream.putNextEntry(new ZipEntry(ENTRY_NAME));
            zipOutputStream.write(json.getBytes());
            zipOutputStream.closeEntry();
            zipOutputStream.flush();
            project.setFileName(file);
            project.setHasChanges(false);
        } catch (Exception e) {
            Dialogs.showErrorAlert(Constants.ERROR_WHILE_SAVING_PROJECT + "\n\n" + e);
        }
    }

    public static boolean export(Project project, File file) {
        return FileHelper.save(file, project.export());
    }
}
