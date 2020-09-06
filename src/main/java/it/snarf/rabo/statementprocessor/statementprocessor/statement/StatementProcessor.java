package it.snarf.rabo.statementprocessor.statementprocessor.statement;

import it.snarf.rabo.statementprocessor.statementprocessor.model.CustomerStatement;
import org.springframework.batch.item.ItemProcessor;

public class StatementProcessor implements ItemProcessor<CustomerStatement, CustomerStatement>{

    @Override
    public CustomerStatement process(final CustomerStatement customerStatement) {

        final CustomerStatement processedStatement = customerStatement;

        return processedStatement;
    }
}
