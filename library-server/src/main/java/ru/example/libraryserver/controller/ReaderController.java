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
import ru.example.libraryserver.auth.RequireRole;

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
    @RequireRole({"ADMIN", "LIBRARIAN", "USER"})
    public List<ReaderDto> getAllReaders(@RequestHeader("X-Auth-Token") String token) {
        return readerService.getAllReaders().stream()
            .map(ru.example.libraryserver.mapper.ReaderMapper::toDto)
            .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "LIBRARIAN", "USER"})
    public ResponseEntity<ReaderDto> getReaderById(@PathVariable Long id) {
        Reader reader = readerService.getReaderById(id);
        return ResponseEntity.ok(ReaderMapper.toDto(reader));
    }

    @PostMapping
    @RequireRole({"ADMIN", "LIBRARIAN"})
    public ResponseEntity<ReaderDto> createReader(@RequestHeader("X-Auth-Token") String token, @RequestBody Reader reader) {
        Reader created = readerService.createReader(reader);
        return ResponseEntity.ok(ReaderMapper.toDto(created));
    }

    @PutMapping("/{id}")
    @RequireRole({"ADMIN", "LIBRARIAN"})
    public ResponseEntity<ReaderDto> updateReader(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id, @RequestBody Reader reader) {
        Reader updated = readerService.updateReader(id, reader);
        return ResponseEntity.ok(ReaderMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN", "LIBRARIAN"})
    public ResponseEntity<Void> deleteReader(@RequestHeader("X-Auth-Token") String token, @PathVariable Long id) {
        readerService.deleteReader(id);
        return ResponseEntity.ok().build();
    }
} 