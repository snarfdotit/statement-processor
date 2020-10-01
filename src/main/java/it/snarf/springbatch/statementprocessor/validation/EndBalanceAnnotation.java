package it.snarf.springbatch.statementprocessor.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = { EndBalanceValidator.class })
public @interface EndBalanceAnnotation {
    String message() default "{error.endBalence}";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
