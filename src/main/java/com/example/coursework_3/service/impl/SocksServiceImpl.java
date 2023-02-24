package com.example.coursework_3.service.impl;

import com.example.coursework_3.model.Socks;
import com.example.coursework_3.service.FileService;
import com.example.coursework_3.service.SocksService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SocksServiceImpl implements SocksService {

    private static List<Socks> socksMap = new ArrayList<>();
    final private FileService fileService;


    @Override
    public Object getSocks(Socks.Color color, Socks.Size size, Integer cottonMin, Integer cottonMax) {
        List<Object> socksList = new ArrayList<>();
        for (Socks oldSocks : socksMap) {
            if (oldSocks.getColor().equals(color) || color == null) {

                if (oldSocks.getSize().equals(size) || size == null) {

                    if ((cottonMin !=null && cottonMax != null) && (oldSocks.getCottonPart() <= cottonMax && oldSocks.getCottonPart() >= cottonMin)){

                        socksList.add(oldSocks);

                    } else if (cottonMin == null || cottonMax == null) {

                        if (cottonMin == null && cottonMax != null && oldSocks.getCottonPart() <= cottonMax) {
                            socksList.add(oldSocks);
                        }
                        if (cottonMax == null && cottonMin != null && oldSocks.getCottonPart() >= cottonMin) {
                            socksList.add(oldSocks);
                        }
                        if (cottonMin == null && cottonMax == null) {
                            cottonMax = 100;
                            cottonMin = 0;
                            socksList.add(oldSocks);
                        }
                    }
                }
            }
        }

        if (color == null && size == null && cottonMax == null && cottonMin == null) {
            return socksMap;
        }

        if (socksList.isEmpty()) {
            throw new NotFoundException("<Товар с таким параметрами не найдены>");
        }

        return socksList;
    }

    @Override
    public Socks addSocks(Socks socks) {
        if (socks == null) {
            throw new IllegalArgumentException("<Неверно указана пара носков>");
        } else {
            for (Socks oldSocks : socksMap){
                if (oldSocks.getColor().equals(socks.getColor())
                        && oldSocks.getSize().equals(socks.getSize())
                        && oldSocks.getCottonPart() == socks.getCottonPart()){
                    oldSocks.setQuantity(oldSocks.getQuantity() + socks.getQuantity());
                    saveToFile();
                    return oldSocks;

                }
            }
        }
        socksMap.add(socks);
        saveToFile();
        return socks;
    }

    @Override
    public boolean releaseOrDeleteSocks(Socks socks) {
        for (Socks neededSocks : socksMap) {
            if (neededSocks.getColor().equals(socks.getColor())
                    && neededSocks.getSize().equals(socks.getSize())
                    && neededSocks.getCottonPart() == socks.getCottonPart()
                    && neededSocks.getQuantity() > socks.getQuantity()) {
                neededSocks.setQuantity(neededSocks.getQuantity() - socks.getQuantity());
                saveToFile();
                return true;

            }  else if (neededSocks.getColor().equals(socks.getColor())
                    && neededSocks.getSize().equals(socks.getSize())
                    && neededSocks.getCottonPart() == socks.getCottonPart()
                    && neededSocks.getQuantity() == socks.getQuantity()) {
                socksMap.remove(neededSocks);
                return true;

            } else if (neededSocks.getColor().equals(socks.getColor())
                    && neededSocks.getSize().equals(socks.getSize())
                    && neededSocks.getCottonPart() == socks.getCottonPart()
                    && neededSocks.getQuantity() < socks.getQuantity()) {
                throw new NegativeArraySizeException("<Указано недостаточное количество носков>");

            }
        }
        throw new NotFoundException("<Не найдена нужная пара носков>");
    }

    private void saveToFile() {
        try {
            String json = new ObjectMapper().writeValueAsString(socksMap);
            fileService.saveToFile(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("<Не удалось сохранить json-файл>");
        }
    }

    private void readFromFile(){
        String json = fileService.readFromFile();
        try{
            socksMap = new ObjectMapper().readValue(json, new TypeReference<List<Socks>>() {
            });
        } catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void init(){
        try {
            readFromFile();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("<Не удалось прочесть существующий json-файл>");
        }
    }

    @Override
    public Path createSocksReport() {
        Path socks = fileService.createTempFile("Socks");
        try (Writer writer = Files.newBufferedWriter(socks, StandardCharsets.UTF_8)){
            for (Socks sock : socksMap) {
                writer.append("Цвет носков: ").append(sock.getColor().getNameColor()).append("\r\n")
                        .append("Размер: ").append(sock.getSize().getRussianSize()).append("\r\n")
                        .append("Доля хлопка: ").append(String.valueOf(sock.getCottonPart())).append("\r\n")
                        .append("Количество на складе: ").append(String.valueOf(sock.getQuantity()));
                writer.append("\r\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return socks;
    }
}

