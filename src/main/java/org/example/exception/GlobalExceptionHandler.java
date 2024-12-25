package org.example.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Обработка ошибок работы с файлами
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        String response = ex.getMessage();
        log.error(response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    // Обработка ошибок некорректного формата данных
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        String response = "Некорректный формат данных. " + ex.getMessage();
        log.error(response);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_PLAIN)
                .body(response);
    }

    // Ошибка при проверке диапазона. Следит, что бы менее было больше чем более))
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<String> handleBusinessLogicException(BusinessLogicException ex) {
        String response = "Ошибка бизнес-логики: " + ex.getMessage();
        log.error(response);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.TEXT_PLAIN)
                .body(response);
    }

    // Валидация поля
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String response = "Некорректный тип данных для поля: " + ex.getName();
        log.error(response);
        return ResponseEntity.badRequest().body(response);
    }

    // Обработка ошибок некорректного формата файла данных
    @ExceptionHandler(InvalidFileTypeException.class)
    public ResponseEntity<String> handleInvalidFileTypeException(InvalidFileTypeException ex) {
        String response = "Неверный формат файла " + ex.getMessage();
        log.error(response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    // Обработка ошибок недостаточного товара на складе
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleInsufficientStockException(InsufficientStockException ex) {
        String response = "Нехватка носков на складе. " + ex.getMessage();
        log.error(response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotFoundProductException.class)
    public ResponseEntity<String> handleNotFoundProductException(NotFoundProductException ex) {

        String response = "Товар " + ex.getMessage() + " не найден";
        log.error(response);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<String> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {

        String response = "Товар " + ex.getMessage() + " уже существует";
        log.error(response);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        log.error("Ошибка {}",errors);
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
        String errorMessage = "Отсутствует обязательный параметр: " + ex.getParameterName();
        log.error(errorMessage);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleJsonParseException(HttpMessageNotReadableException ex) {
        String response = "Некорректный формат данных в запросе" + ex.getMessage();
        log.error("Ошибка: {}",response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<String> handleMultipartException(MultipartException ex) {
        String response = "Ошибка при загрузке файла. " + ex.getMessage();
        log.error("Ошибка при загрузке файла Проверьте корректность файла.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        String response = "Ошибка ввода-вывода при обработке файла. Убедитесь, что файл не поврежден.";
        log.error("Ошибка: {}",response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}