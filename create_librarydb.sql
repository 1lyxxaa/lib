-- Скрипт для создания базы данных и пользователя для library-system

-- Выполнить от имени администратора PostgreSQL (например, пользователя postgres)

CREATE DATABASE librarydb;

CREATE USER libraryuser WITH ENCRYPTED PASSWORD 'librarypass';

GRANT ALL PRIVILEGES ON DATABASE librarydb TO libraryuser; 