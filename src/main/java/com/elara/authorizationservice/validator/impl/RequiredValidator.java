package com.elara.authorizationservice.validator.impl;

import com.elara.authorizationservice.exception.AppException;
import com.elara.authorizationservice.service.MessageService;
import com.elara.authorizationservice.validator.Required;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;

@Slf4j
@Component
public class RequiredValidator implements ConstraintValidator<Required, Object> {

    String message;

    @Autowired
    MessageService messageService;

    @Override
    public void initialize(Required annotation) {
        message = annotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext cvc) {

        String castValue = "";
        if (value instanceof String) {
            castValue = (String) value;
        } else if (value instanceof BigDecimal) {
            castValue = value.toString();
        } else if (value instanceof Double) {
            castValue = value.toString();
        } else if (value instanceof Integer) {
            castValue = value + "";
        } else if (value instanceof BigInteger) {
            castValue = value + "";
        } else if (value instanceof Long) {
            castValue = value + "";
        }

        if (castValue != null && !"".equals(castValue.trim())) {
            return true;
        }

        throw new AppException(messageService.getMessage(this.message));
    }
}
