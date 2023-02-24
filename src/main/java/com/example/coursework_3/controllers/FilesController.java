package com.example.coursework_3.controllers;

import com.example.coursework_3.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;


@RestController
@RequestMapping("/files")
@AllArgsConstructor
public class FilesController {

    private final FileService fileService;



}
