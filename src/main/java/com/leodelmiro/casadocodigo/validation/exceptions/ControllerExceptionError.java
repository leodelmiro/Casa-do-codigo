package com.leodelmiro.casadocodigo.validation.exceptions;

import com.leodelmiro.casadocodigo.validation.FieldMessageDTO;
import com.leodelmiro.casadocodigo.validation.StandardMessageErrorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ControllerExceptionError {

    @Autowired
    private MessageSource messageSource;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<FieldMessageDTO> validation(MethodArgumentNotValidException exception) {
        List<FieldMessageDTO> fieldMessageDTOS = new ArrayList<>();

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        fieldErrors.forEach(e -> {
            String message = messageSource.getMessage(e, LocaleContextHolder.getLocale());
            FieldMessageDTO error = new FieldMessageDTO(e.getField(), message);
            fieldMessageDTOS.add(error);
        });

        return fieldMessageDTOS;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResponseStatusException.class)
    public StandardMessageErrorDTO notFound(ResponseStatusException exception, HttpServletRequest request) {
        return new StandardMessageErrorDTO(Instant.now(), "Produto não encontrado", exception.getMessage(), request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CountryStateAlreadyExistsException.class)
    public FieldMessageDTO stateAlreadyExists(CountryStateAlreadyExistsException exception) {
        return new FieldMessageDTO("name", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalCountryStateException.class)
    public FieldMessageDTO countryHasState(IllegalCountryStateException exception) {
        return new FieldMessageDTO("state", exception.getMessage());
    }

}
