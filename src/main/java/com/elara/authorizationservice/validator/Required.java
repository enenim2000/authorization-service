package com.elara.authorizationservice.validator;

import com.elara.authorizationservice.validator.impl.RequiredValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RequiredValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Required {

    public String message() default "Field is required";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

}
