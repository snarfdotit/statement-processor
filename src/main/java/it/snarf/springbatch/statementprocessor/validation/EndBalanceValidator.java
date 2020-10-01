package it.snarf.springbatch.statementprocessor.validation;

import it.snarf.springbatch.statementprocessor.model.CustomerStatement;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndBalanceValidator implements ConstraintValidator<EndBalanceAnnotation, CustomerStatement> {

    @Override
    public void initialize(EndBalanceAnnotation constraintAnnotation) {

    }

    @Override
    public boolean isValid(CustomerStatement value, ConstraintValidatorContext context) {
        return value.getStartBalance().add(value.getMutation()).compareTo(value.getEndBalance()) == 0;
    }
}
