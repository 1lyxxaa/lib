package ru.example.libraryserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.libraryserver.model.Reader;
import ru.example.libraryserver.repository.ReaderRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ReaderService {
    private final ReaderRepository readerRepository;

    @Autowired
    public ReaderService(ReaderRepository readerRepository) {
        this.readerRepository = readerRepository;
    }

    @Transactional(readOnly = true)
    public List<Reader> getAllReaders() {
        return readerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Reader getReaderById(Long id) {
        return readerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Читатель не найден"));
    }

    @Transactional
    public Reader createReader(Reader reader) {
        if (readerRepository.existsByEmail(reader.getEmail())) {
            throw new IllegalArgumentException("Читатель с таким email уже существует");
        }
        if (readerRepository.existsByPhone(reader.getPhone())) {
            throw new IllegalArgumentException("Читатель с таким номером телефона уже существует");
        }
        return readerRepository.save(reader);
    }

    @Transactional
    public Reader updateReader(Long id, Reader reader) {
        Reader existingReader = getReaderById(id);
        
        if (!existingReader.getEmail().equals(reader.getEmail()) && 
            readerRepository.existsByEmail(reader.getEmail())) {
            throw new IllegalArgumentException("Читатель с таким email уже существует");
        }
        if (!existingReader.getPhone().equals(reader.getPhone()) && 
            readerRepository.existsByPhone(reader.getPhone())) {
            throw new IllegalArgumentException("Читатель с таким номером телефона уже существует");
        }

        existingReader.setFullName(reader.getFullName());
        existingReader.setEmail(reader.getEmail());
        existingReader.setPhone(reader.getPhone());

        return readerRepository.save(existingReader);
    }

    @Transactional
    public void deleteReader(Long id) {
        if (!readerRepository.existsById(id)) {
            throw new EntityNotFoundException("Читатель не найден");
        }
        readerRepository.deleteById(id);
    }
} 