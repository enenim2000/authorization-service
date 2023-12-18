package com.elara.authorizationservice.exception;

import java.util.HashMap;

import com.elara.authorizationservice.enums.ResponseCode;
import com.elara.authorizationservice.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class CustomEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    MessageService messageService;

    @ExceptionHandler(AppException.class)
    public final ResponseEntity<Object> handleAppException(AppException ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("responseCode", ex.getResponseCode());
        response.put("responseMessage", ex.getMessage());
        HttpStatus httpStatus = "500".equals(ex.getResponseCode()) ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.EXPECTATION_FAILED;
        return ResponseEntity.status(httpStatus).body(response);
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public final ResponseEntity<Object> handleAppException(UnAuthorizedException ex) {
        HashMap<String, String> response = new HashMap<>();
        response.put("responseCode", ex.getResponseCode());
        response.put("responseMessage", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(java.sql.SQLException.class)
    public final ResponseEntity<Object> sqlException(java.sql.SQLException ex) {
        log.error("SQLException: ", ex);
        HashMap<String, String> response = new HashMap<>();
        response.put("responseCode", ResponseCode.INTERNAL_SERVER_ERROR.getValue());
        response.put("responseMessage", messageService.getMessage("Server.Error"));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(java.lang.NullPointerException.class)
    public final ResponseEntity<Object> nullPointerException(java.lang.NullPointerException ex) {
        log.error("NullPointerException: ", ex);
        HashMap<String, String> response = new HashMap<>();
        response.put("responseCode", ResponseCode.INTERNAL_SERVER_ERROR.getValue());
        response.put("responseMessage", messageService.getMessage("Server.Error"));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
