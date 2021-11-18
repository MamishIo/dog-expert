package io.mamish.dogexpert.discord;

import java.io.File;
import java.io.IOException;

public class ClassifierProcess {

    public ClassifierProcess() {
        try {
            new ProcessBuilder("python3 -m server")
                    .directory(new File("/opt/dogexpert/python"))
                    .start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to start Python server", e);
        }
    }
}
