package it.snarf.springbatch.statementprocessor.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.snarf.springbatch.statementprocessor.model.CustomerStatement;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class EndBalanceValidatorTest {

    private final EndBalanceValidator endBalanceValidator = new EndBalanceValidator();

    @Test
    void isValid() {
        CustomerStatement customerStatement = new CustomerStatement(1L, 1L,"MyIban",
            "description", BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.TEN, "filename");

        assertTrue(endBalanceValidator.isValid(customerStatement, null));

    }

    @Test
    void isNotValid() {
        CustomerStatement customerStatement = new CustomerStatement(1L, 1L,"MyIban",
            "description", BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN, "filename");

        assertFalse(endBalanceValidator.isValid(customerStatement, null));

    }
}