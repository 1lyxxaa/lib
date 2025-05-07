package ru.example.libraryserver.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import ru.example.libraryserver.model.Author;
import ru.example.libraryserver.model.Book;
import ru.example.libraryserver.repository.AuthorRepository;
import ru.example.libraryserver.repository.BookRepository;

/**
 * Класс для инициализации базы данных начальными данными.
 * Реализует интерфейс CommandLineRunner для выполнения инициализации при запуске приложения.
 */
@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;

    /**
     * Выполняет инициализацию базы данных начальными данными, если она пуста.
     * @param args аргументы командной строки
     */
    @Override
    public void run(String... args) {
        if (authorRepository.count() == 0 && bookRepository.count() == 0) {
            Author tolstoy = new Author();
            tolstoy.setName("Лев Толстой");
            tolstoy.setBirthYear(1828);
            authorRepository.save(tolstoy);

            Author dostoevsky = new Author();
            dostoevsky.setName("Фёдор Достоевский");
            dostoevsky.setBirthYear(1821);
            authorRepository.save(dostoevsky);

            Book warAndPeace = new Book();
            warAndPeace.setTitle("Война и мир");
            warAndPeace.setYear(1869);
            warAndPeace.setGenre("Роман");
            warAndPeace.setPages(1300);
            warAndPeace.setAuthor(tolstoy);
            bookRepository.save(warAndPeace);

            Book annaKarenina = new Book();
            annaKarenina.setTitle("Анна Каренина");
            annaKarenina.setYear(1877);
            annaKarenina.setGenre("Роман");
            annaKarenina.setPages(900);
            annaKarenina.setAuthor(tolstoy);
            bookRepository.save(annaKarenina);

            Book idiot = new Book();
            idiot.setTitle("Идиот");
            idiot.setYear(1869);
            idiot.setGenre("Роман");
            idiot.setPages(700);
            idiot.setAuthor(dostoevsky);
            bookRepository.save(idiot);
        }
    }
} 