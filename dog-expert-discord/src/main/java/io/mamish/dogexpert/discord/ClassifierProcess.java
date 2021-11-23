package io.mamish.dogexpert.discord;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;

public class ClassifierProcess {

    private static final String PYTHON_SYSTEM_PATH = "/usr/bin/python3";
    private static final String PYTHON_LOCAL_SUBDIR = "python";
    private static final String PYTHON_LOCAL_MAIN_FILE = "server.py";

    public ClassifierProcess() {
        try {
            File workdir = new File(PYTHON_LOCAL_SUBDIR);
            List<String> command = List.of(PYTHON_SYSTEM_PATH, PYTHON_LOCAL_MAIN_FILE);
            new ProcessBuilder(command)
                    .directory(workdir)
                    .redirectOutput(Redirect.INHERIT)
                    .redirectError(Redirect.INHERIT)
                    .start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start Python server", e);
        }
    }
}
