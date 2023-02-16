package com.example.coursework_3.service;

import com.example.coursework_3.model.Socks;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public interface SocksService {

    HashMap<Integer, Socks> getAllSocks();                   //Get
    Socks getCertainSocks(String color, int cottonPart);    //Get
    List<Socks> getSocksTxt(Socks socks);                    //Get
    Socks addSocks(Socks socks);                               //Post
    boolean releaseSocks(Socks socks);                        //Put
    boolean deleteDefectiveSocks(Socks socks);                //Delete

    Path createSocksReport();


}
