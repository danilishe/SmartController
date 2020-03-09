package ru.isled.smartcontrol.controller;

import com.alibaba.fastjson.JSON;
import ru.isled.smartcontrol.model.Project;
import ru.isled.smartcontrol.model.wraps.ProjectWrapper;

import java.io.File;

public class ProjectIO {
    public static Project load(File file) {
        byte[] data = FileHelper.load(file);
        String projectData = new String(data);
        ProjectWrapper projectWrapper = null;
        try {
            projectWrapper = JSON.parseObject(data, ProjectWrapper.class);
        } catch (RuntimeException ignore) {
        }
        if (projectWrapper == null) return null;
        Project project = null; //Wrapper.unwrap(projectWrapper);
        project.setFileName(file);
        return project;
    }

    public static void save(Project project, File file) {
        ProjectWrapper projectWrapper = null; // Wrapper.wrap(project);
        String json = JSON.toJSONString(projectWrapper);
        boolean result = FileHelper.save(file, json.getBytes());
        project.setFileName(file);
        project.setHasChanges(false);
    }

    public static boolean export(Project project, File file) {
        return FileHelper.save(file, project.export());
    }
}
