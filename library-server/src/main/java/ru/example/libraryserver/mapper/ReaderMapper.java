package ru.example.libraryserver.mapper;

import ru.example.libraryserver.model.Reader;
import ru.example.libraryserver.dto.ReaderDto;

public class ReaderMapper {
    public static ReaderDto toDto(Reader reader) {
        return new ReaderDto(
            reader.getId(),
            reader.getFullName(),
            reader.getPhone(),
            reader.getEmail()
        );
    }
} 