package com.example.coursework_3.controllers;

import com.example.coursework_3.model.Socks;
import com.example.coursework_3.service.FileService;
import com.example.coursework_3.service.SocksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/socks")
@RequiredArgsConstructor
@Tag(name = "Склад носков", description = "Автоматизированный учет товаров:")
public class SocksController {

    private final SocksService socksService;

    private final FileService fileService;

    @PostMapping
    @Operation(summary = "Приход товара", description = "Добавление новой пары носков на склад")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "HTTP 200" , description = "Удалось добавить приход"),
            @ApiResponse(responseCode = "HTTP 400" , description = "Параметры запроса отсутствуюит или имеют некорректный формат"),
            @ApiResponse(responseCode = "HTTP 500" , description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public ResponseEntity<Socks> addSocks (@Valid @RequestBody Socks socks) {
        socksService.addSocks(socks);
        return ResponseEntity.ok(socks);
    }

    @GetMapping
    @Operation(summary = "Список товара", description = "Показывает список носков по вводимым параметрам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "HTTP 200" , description = "Список получен"),
            @ApiResponse(responseCode = "HTTP 400" , description = "Ошибка 400"),
            @ApiResponse(responseCode = "HTTP 500" , description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public ResponseEntity<Object> getSocks (@RequestParam (required = false) Socks.Color color,
                                            @RequestParam (required = false) Socks.Size size,
                                            @RequestParam (required = false) Integer cottonMin,
                                            @RequestParam (required = false) Integer cottonMax) {
        return ResponseEntity.ok(socksService.getSocks(color, size, cottonMin, cottonMax));
    }

    @PutMapping
    @Operation(summary = "Отпуск товара", description = "Ищет нужную пару на складе и производит ее отпуск в нужном количестве")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "HTTP 200" , description = "Товар отпущен со склада"),
            @ApiResponse(responseCode = "HTTP 400" , description = "Ошибка 400"),
            @ApiResponse(responseCode = "HTTP 500" , description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public ResponseEntity<Boolean> releaseSocks (@Valid @RequestBody Socks socks) {
        return ResponseEntity.ok(socksService.releaseOrDeleteSocks(socks));
    }

    @DeleteMapping
    @Operation(summary = "Удаление бракованного товара", description = "Убирает со склада бракованные носки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "HTTP 200" , description = "Товар удален со склада"),
            @ApiResponse(responseCode = "HTTP 400" , description = "Ошибка 400"),
            @ApiResponse(responseCode = "HTTP 500" , description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public ResponseEntity<Boolean> deleteDefectiveSocks (@Valid @RequestBody Socks socks) {
        return ResponseEntity.ok(socksService.releaseOrDeleteSocks(socks));
    }

    @GetMapping(value = "/socks.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Список всего товара в формате txt", description = "Показывает список всех носков на данный момент")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "HTTP 200" , description = "Список получен"),
            @ApiResponse(responseCode = "HTTP 400" , description = "Ошибка 400"),
            @ApiResponse(responseCode = "HTTP 500" , description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public ResponseEntity<InputStreamResource> getSocksTxt () {
        try {
            Path path = socksService.createSocksReport();
            if (Files.size(path) == 0){
                return ResponseEntity.noContent().build();
            }
            InputStreamResource resource = new InputStreamResource(new FileInputStream(path.toFile()));
            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
                    .contentLength(Files.size(path))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "Socks\"")
                    .body(resource);
        } catch (IOException e){
            return ResponseEntity.internalServerError().build();
        }
    }


}
