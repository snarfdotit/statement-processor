package it.snarf.springbatch.statementprocessor.statement;

import it.snarf.springbatch.statementprocessor.model.CustomerStatement;
import org.springframework.batch.item.ItemProcessor;

public class StatementProcessor implements ItemProcessor<CustomerStatement, CustomerStatement>{

    @Override
    public CustomerStatement process(final CustomerStatement customerStatement) {
        return customerStatement;
    }
}
