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
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class SocksServiceImpl implements SocksService {

    private static HashMap<Integer, Socks> socksMap = new HashMap<>();
    private static int id=0;
    final private FileService fileService;


    @Override
    public HashMap<Integer, Socks> getAllSocks() {
        if (socksMap.isEmpty()){
            throw new NotFoundException("<Список пуст>");
        }
        return socksMap;
    }

    @Override
    public Socks getCertainSocks(String color, int cottonPart) {
        if (color==null || cottonPart<0) {
            throw new IllegalArgumentException("<Неверно указан цвет/доля хлопка>");
        }
        for (Socks oldSocks : socksMap.values()){
            if (oldSocks.getColor().getNameColor().equals(color) && oldSocks.getCottonPart() == cottonPart){
                return oldSocks;
            }
        }
        throw new NotFoundException("<Товар с таким цветом и долей хлопка не найден>");
    }

    @Override
    public List<Socks> getSocksTxt(Socks socks) {

        return null;
    }

    @Override
    public Socks addSocks(Socks socks) {
        if (socks == null) {
            throw new IllegalArgumentException("<Неверно указана пара носков>");
        } else {
            for (Socks oldSocks : socksMap.values()){
                if (oldSocks.getColor().equals(socks.getColor())
                        && oldSocks.getSize().equals(socks.getSize())
                        && oldSocks.getCottonPart() == socks.getCottonPart()){
                    oldSocks.setQuantity(oldSocks.getQuantity() + socks.getQuantity());
                    socksMap.put(id, oldSocks);
                    saveToFile();
                    return oldSocks;

                }
            }
        }
        socksMap.put(++id,socks);
        saveToFile();
        return socks;
    }

    @Override
    public boolean releaseSocks(Socks socks) {
        for (Socks neededSocks : socksMap.values()) {
            if (neededSocks.getColor().equals(socks.getColor())
                    && neededSocks.getSize().equals(socks.getSize())
                    && neededSocks.getCottonPart() == socks.getCottonPart()
                    && neededSocks.getQuantity() > socks.getQuantity()) {
                socksMap.remove(id, neededSocks);
                neededSocks.setQuantity(neededSocks.getQuantity() - socks.getQuantity());
                socksMap.put(id, neededSocks); // не уверен что тут правильно реализовал замену, путем удаления старого и добавления нового
                saveToFile();
                return true;

            }  else if (neededSocks.getColor().equals(socks.getColor())
                    && neededSocks.getSize().equals(socks.getSize())
                    && neededSocks.getCottonPart() == socks.getCottonPart()
                    && neededSocks.getQuantity() == socks.getQuantity()) {
                socksMap.remove(id, neededSocks);
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

    @Override
    public boolean deleteDefectiveSocks(Socks socks) {
        for (Socks defectiveSocks : socksMap.values()) {
            if (defectiveSocks.getColor().equals(socks.getColor())
                    && defectiveSocks.getSize().equals(socks.getSize())
                    && defectiveSocks.getCottonPart() == socks.getCottonPart()
                    && defectiveSocks.getQuantity() > socks.getQuantity()){
                socksMap.remove(id, defectiveSocks);
                defectiveSocks.setQuantity(defectiveSocks.getQuantity() - socks.getQuantity());
                socksMap.put(id, defectiveSocks);
                saveToFile();
                return true;

            } else if (defectiveSocks.getColor().equals(socks.getColor())
                    && defectiveSocks.getSize().equals(socks.getSize())
                    && defectiveSocks.getCottonPart() == socks.getCottonPart()
                    && defectiveSocks.getQuantity() == socks.getQuantity()) {
                socksMap.remove(id, defectiveSocks);
                return true;

            } else if (defectiveSocks.getColor().equals(socks.getColor())
                    && defectiveSocks.getSize().equals(socks.getSize())
                    && defectiveSocks.getCottonPart() == socks.getCottonPart()
                    && defectiveSocks.getQuantity() < socks.getQuantity()) {
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
            socksMap = new ObjectMapper().readValue(json, new TypeReference<HashMap<Integer, Socks>>() {
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
        socksMap.getOrDefault(id, null);
        Path socks = fileService.createTempFile("Socks");
        try (Writer writer = Files.newBufferedWriter(socks, StandardCharsets.UTF_8)){
            for (Socks sock : socksMap.values()) {
                writer.append("Цвет носков: ").append(sock.getColor().getNameColor()).append("\r\n")
                        .append("Размер: ").append(sock.getSize().getRussianSize()).append("\r\n")
                        .append("Доля хлопка: ").append(String.valueOf(sock.getCottonPart())).append("\r\n")
                        .append("Количество на складе: ").append(String.valueOf(sock.getQuantity()));
                writer.append("\r\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}

