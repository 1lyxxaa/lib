CREATE DATABASE librarydbtest;

\c librarydbtest

CREATE TABLE author (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE reader (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100)
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    email VARCHAR(100)
);

CREATE TABLE book (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author_id INTEGER NOT NULL REFERENCES author(id) ON DELETE CASCADE,
    genre VARCHAR(50),
    year INTEGER,
    pages INTEGER NOT NULL,
    available BOOLEAN DEFAULT TRUE
);

CREATE TABLE book_loan (
    id SERIAL PRIMARY KEY,
    book_id INTEGER NOT NULL REFERENCES book(id) ON DELETE CASCADE,
    reader_id INTEGER NOT NULL REFERENCES reader(id),
    librarian_id INTEGER NOT NULL REFERENCES users(id),
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE
);

ALTER TABLE book_loan DROP CONSTRAINT IF EXISTS book_loan_reader_id_fkey;
ALTER TABLE book_loan DROP CONSTRAINT IF EXISTS fk_book_loans_reader;

ALTER TABLE book_loan
ADD CONSTRAINT fk_book_loans_reader
FOREIGN KEY (reader_id) REFERENCES reader(id) ON DELETE CASCADE;
