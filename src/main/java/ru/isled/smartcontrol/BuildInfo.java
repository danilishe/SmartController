package ru.isled.smartcontrol;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class BuildInfo {
    public static final String PROGRAM_VERSION = loadVersion();

    private BuildInfo() {
    }

    private static String loadVersion() {
        String version = null;

        Properties props = new Properties();
        try (InputStream in = BuildInfo.class.getClassLoader().getResourceAsStream("build.properties")) {
            if (in != null) {
                props.load(in);
                version = props.getProperty("program.version");
            }
        } catch (IOException ignored) {
        }

        if (version == null || version.trim().isEmpty()) {
            Package p = BuildInfo.class.getPackage();
            if (p != null) {
                version = p.getImplementationVersion();
            }
        }

        if (version == null || version.trim().isEmpty()) {
            version = "dev";
        }

        return version;
    }
}
