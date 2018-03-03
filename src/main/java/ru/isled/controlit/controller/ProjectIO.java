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

        Project project = Wrapper.unwrap(wrappedProject);
        project.setFileName(file);
        return project;
    }

    public static void save(Project project, File file) {
        WrappedProject wrappedProject = Wrapper.wrap(project);
        String json = JSON.toJSONString(wrappedProject);
        boolean result = FileHelper.save(file, json.getBytes());
        project.setFileName(file);
        project.setHasChanges(false);
    }

    public static void export(Project project, File file) {
        WrappedProject wrappedProject = Wrapper.wrap(project);
        byte[] export = Converter.encode(wrappedProject);
        FileHelper.save(file, export);
    }
}
