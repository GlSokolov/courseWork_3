package com.example.coursework_3.service;

import com.example.coursework_3.model.Socks;

import java.nio.file.Path;
import java.util.List;

public interface SocksService {

    List<Socks> getAllSocks();
    Socks getCertainSocksMinMax(String color, String size, Integer cottonMin, Integer cottonMax);
    Socks addSocks(Socks socks);
    boolean releaseSocks(Socks socks);
    boolean deleteDefectiveSocks(Socks socks);

    Path createSocksReport();


}
