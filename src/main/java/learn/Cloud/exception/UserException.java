package learn.Cloud.exception;

import org.springframework.http.HttpStatus;

public class UserException extends RuntimeException {

    private final HttpStatus status; // Добавьте это поле

    // Конструктор с сообщением (по умолчанию BAD_REQUEST)
    public UserException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    // Конструктор с сообщением и статусом
    public UserException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    // Добавьте геттер для статуса
    public HttpStatus getStatus() {
        return status;
    }
}
