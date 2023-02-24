package com.example.coursework_3.service;

import com.example.coursework_3.model.Socks;

import java.nio.file.Path;

public interface SocksService {

    Object getSocks(Socks.Color color, Socks.Size size, Integer cottonMin, Integer cottonMax);
    Socks addSocks(Socks socks);
    boolean releaseOrDeleteSocks(Socks socks);
    Path createSocksReport();


}
