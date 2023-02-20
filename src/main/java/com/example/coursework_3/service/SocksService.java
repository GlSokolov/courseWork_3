package com.example.coursework_3.service;

import com.example.coursework_3.model.Socks;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SocksService {

    Map<Integer, Socks> getAllSocks();
    Socks getCertainSocksMax(String color, String size ,int maxCottonPart);
    Socks getCertainSocksMin(String color, String size ,int mixCottonPart);
    Socks addSocks(Socks socks);
    boolean releaseSocks(Socks socks);
    boolean deleteDefectiveSocks(Socks socks);

    Path createSocksReport();


}
