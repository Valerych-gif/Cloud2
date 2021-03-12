package ru.valerych.cloud2.exceptions;

public class CantToCreateStorageException extends Exception{
    @Override
    public String toString() {
        return "Невозможно создать папку сетевого хранилища. Работа сервера прервана";
    }
}
