package com.ecommerce.webapp.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseBody
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ResponseEntity<>("User Already Exists!", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    @ResponseBody
    public ResponseEntity<?> handleAddressNotFoundException(AddressNotFoundException ex) {
        return new ResponseEntity<>("Please update your details with an address to place an order.", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseBody
    public ResponseEntity<?> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ContactNotFoundException.class)
    @ResponseBody
    public ResponseEntity<?> handleContactNotFoundException(ContactNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailFailureException.class)
    @ResponseBody
    public ResponseEntity<?> handleEmailFailureException(EmailFailureException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    @ResponseBody
    public ResponseEntity<?> handleEmailNotFoundException(EmailNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotVerifiedException.class)
    @ResponseBody
    public ResponseEntity<?> handleUserNotVerifiedException(UserNotVerifiedException ex) {
        return new ResponseEntity<>("Provided User Is Not Verified! Please Verify The User Using Mobile And Email.", HttpStatus.CONFLICT);
    }


}
