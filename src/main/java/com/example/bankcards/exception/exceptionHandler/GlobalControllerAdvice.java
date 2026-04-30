package com.example.bankcards.exception.exceptionHandler;



import com.example.bankcards.dto.response.ErrorResponseBody;
import com.example.bankcards.exception.exceptions.AlreadyExistException;
import com.example.bankcards.exception.exceptions.AuthenticationFailException;
import com.example.bankcards.exception.exceptions.BadRequestException;
import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.service.ErrorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final ErrorService errorService;

    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException ex) {
        return ErrorResponse.builder(ex, HttpStatus.NOT_FOUND, ex.getMessage()).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseBody> validationHandler(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(errorService.makeResponse(ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<ErrorResponseBody> handleUserExists(AlreadyExistException ex) {
        return new ResponseEntity<>(errorService.makeResponse(ex), HttpStatus.CONFLICT);

    }

    @ExceptionHandler(AuthenticationFailException.class)
    public ResponseEntity<ErrorResponseBody> incorrectPasswordHandler(AuthenticationFailException ex) {
        return new ResponseEntity<>(errorService.makeResponse(ex), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseBody> incorrectPasswordHandler(BadRequestException ex) {
        return new ResponseEntity<>(errorService.makeResponse(ex), HttpStatus.BAD_REQUEST);

    }


}
