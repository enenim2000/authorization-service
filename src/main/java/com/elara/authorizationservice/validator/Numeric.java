package com.elara.authorizationservice.validator;

import com.elara.authorizationservice.validator.impl.NumericValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NumericValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Numeric {

    boolean required() default true;

    public String message() default "Invalid numeric value";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

}