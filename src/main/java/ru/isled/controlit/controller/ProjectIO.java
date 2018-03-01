package ru.isled.controlit.controller;

import com.alibaba.fastjson.JSON;
import ru.isled.controlit.model.Project;
import ru.isled.controlit.model.WrappedProject;

import java.io.File;

public class ProjectIO {
    public static Project load(File file) {
        byte[] data = FileHelper.load(file);
//        String projectData = new String(data);
        WrappedProject wrappedProject = JSON.parseObject(data, WrappedProject.class);

        Wrapper wrapper = new Wrapper();
        Project project = wrapper.unwrap(wrappedProject);
        project.setFileName(file);
        return project;
    }

    public static Project importPrj(File file) {
        throw new RuntimeException("Метод не реализован");
    }

    public static void save(Project project, File file) {
        Wrapper wrapper = new Wrapper();
        WrappedProject wrappedProject = wrapper.wrap(project);
        String json = JSON.toJSONString(wrappedProject);
        boolean result = FileHelper.save(file, json.getBytes());
        project.setFileName(file);
        project.setHasChanges(false);
    }

    public void export(Project project, File file) {
        throw new RuntimeException("Метод не реализован");
    }
}
