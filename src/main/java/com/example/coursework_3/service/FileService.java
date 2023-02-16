package com.example.coursework_3.service;

import java.nio.file.Path;

public interface FileService {

    boolean saveToFile(String json);
    String readFromFile();
    Path createTempFile(String suffix);

}
