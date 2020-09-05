package it.snarf.rabo.statementprocessor.statementprocessor.validation;

import static org.junit.jupiter.api.Assertions.*;

import it.snarf.rabo.statementprocessor.statementprocessor.model.CustomerStatement;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class EndBalanceValidatorTest {

    private EndBalanceValidator endBalanceValidator = new EndBalanceValidator();

    @Test
    void isValid() {
        CustomerStatement customerStatement = new CustomerStatement(1L, 1L,"MyIban",
            "description", BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.TEN);

        assertTrue(endBalanceValidator.isValid(customerStatement, null));

    }

    @Test
    void isNotValid() {
        CustomerStatement customerStatement = new CustomerStatement(1L, 1L,"MyIban",
            "description", BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN);

        assertFalse(endBalanceValidator.isValid(customerStatement, null));

    }
}