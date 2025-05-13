package ru.example.libraryserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.example.libraryserver.model.BookLoan;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    List<BookLoan> findByReaderId(Long readerId);
    List<BookLoan> findByBookId(Long bookId);
    List<BookLoan> findByLibrarianId(Long librarianId);
    List<BookLoan> findByDueDateBeforeAndReturnDateIsNull(LocalDate date);
    List<BookLoan> findByOverdueTrue();
} 