package it.snarf.springbatch.statementprocessor.statement;

import it.snarf.springbatch.statementprocessor.model.CustomerStatement;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

@Component
public class StatementFieldSetMapper implements FieldSetMapper<CustomerStatement> {

    @Override
    public CustomerStatement mapFieldSet(FieldSet fieldSet) {
        final CustomerStatement statement = new CustomerStatement();
        statement.setReferenceNumber(fieldSet.readLong("reference"));
        statement.setIban(fieldSet.readString("iban"));
        statement.setDescription(fieldSet.readString("description"));
        statement.setStartBalance(fieldSet.readBigDecimal("startBalance"));
        statement.setMutation(fieldSet.readBigDecimal("mutation"));
        statement.setEndBalance(fieldSet.readBigDecimal("endBalance"));
        return statement;

    }
}
