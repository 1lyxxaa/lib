package ru.example.libraryserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.libraryserver.model.Reader;
import ru.example.libraryserver.service.ReaderService;
import ru.example.libraryserver.dto.ReaderDto;
import ru.example.libraryserver.mapper.ReaderMapper;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/readers")
@CrossOrigin(origins = "http://localhost:8081")
public class ReaderController {
    private final ReaderService readerService;

    @Autowired
    public ReaderController(ReaderService readerService) {
        this.readerService = readerService;
    }

    @GetMapping
    public ResponseEntity<List<ReaderDto>> getAllReaders() {
        List<ReaderDto> dtos = readerService.getAllReaders().stream()
            .map(ReaderMapper::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReaderDto> getReaderById(@PathVariable Long id) {
        Reader reader = readerService.getReaderById(id);
        return ResponseEntity.ok(ReaderMapper.toDto(reader));
    }

    @PostMapping
    public ResponseEntity<ReaderDto> createReader(@RequestBody Reader reader) {
        Reader created = readerService.createReader(reader);
        return ResponseEntity.ok(ReaderMapper.toDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReaderDto> updateReader(@PathVariable Long id, @RequestBody Reader reader) {
        Reader updated = readerService.updateReader(id, reader);
        return ResponseEntity.ok(ReaderMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReader(@PathVariable Long id) {
        readerService.deleteReader(id);
        return ResponseEntity.ok().build();
    }
} 