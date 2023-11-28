package com.ecommerce.webapp.exception;

import com.ecommerce.webapp.api.model.CustomErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;

@RestControllerAdvice
public class CustomControllerAdvice extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(CustomControllerAdvice.class);

    private final MessageSource messageSource;

    public CustomControllerAdvice(MessageSource messageSource) {
        this.messageSource = messageSource;
    }



    @ExceptionHandler(IllegalProviderArgumentException.class)
    @ResponseBody
    public ResponseEntity<?> handleIllegalProviderArgumentException(IllegalProviderArgumentException ex) {
        String errorMessage = "Unsupported identity provider: " + ex.getProvider();
        CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage, LocalDateTime.now());
        logError(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    @ResponseBody
    public ResponseEntity<?> handleIOException(IOException ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error while processing your request!", LocalDateTime.now());
        logError(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseBody
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.CONFLICT.value(), "User Already Exists!", LocalDateTime.now());
        logError(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    @ResponseBody
    public ResponseEntity<?> handleAddressNotFoundException(AddressNotFoundException ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.NOT_FOUND.value(), "Please update your details with an address to place an order.", LocalDateTime.now());
        logError(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseBody
    public ResponseEntity<?> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now());
        logError(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ContactNotFoundException.class)
    @ResponseBody
    public ResponseEntity<?> handleContactNotFoundException(ContactNotFoundException ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now());
        logError(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailFailureException.class)
    @ResponseBody
    public ResponseEntity<?> handleEmailFailureException(EmailFailureException ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), LocalDateTime.now());
        logError(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    @ResponseBody
    public ResponseEntity<?> handleEmailNotFoundException(EmailNotFoundException ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now());
        logError(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    @ResponseBody
    public ResponseEntity<?> handleUserNotVerifiedException(UserNotVerifiedException ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.CONFLICT.value(), "Provided User Is Not Verified! Please Verify The User Using Mobile And Email.", LocalDateTime.now());
        logError(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<?> handleGenericException(Exception ex) {
        CustomErrorResponse errorResponse = new CustomErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred.", LocalDateTime.now());
        logError(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logError(Exception ex) {
        log.error("Exception occurred: {}", ex.getMessage(), ex);
    }

}
