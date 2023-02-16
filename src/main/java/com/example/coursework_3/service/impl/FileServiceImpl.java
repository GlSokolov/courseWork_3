package com.example.coursework_3.service.impl;


import com.example.coursework_3.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileServiceImpl implements FileService {

    @Value("path.to.data.file")
    private String dataFilePath;
    @Value("name.of.socks.data.file")
    private String dataFileName;

    @Override
    public boolean saveToFile(String json) {
        try {
            Files.writeString(Path.of(dataFilePath,dataFileName), json);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String readFromFile() {
        try{
            return Files.readString(Path.of(dataFilePath,dataFileName));
        } catch (IOException e) {
            e.printStackTrace();
            throw new NotFoundException("<Файл не найден>");
        }
    }

    @Override
    public Path createTempFile(String suffix) {
        try {
            return Files.createTempFile(Path.of(dataFilePath), "tempFile", suffix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
