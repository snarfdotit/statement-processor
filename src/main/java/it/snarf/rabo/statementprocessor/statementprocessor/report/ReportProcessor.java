package it.snarf.rabo.statementprocessor.statementprocessor.report;

import it.snarf.rabo.statementprocessor.statementprocessor.model.ErrorStatement;
import org.springframework.batch.item.ItemProcessor;

public class ReportProcessor implements ItemProcessor<ErrorStatement, ErrorStatement> {

    @Override
    public ErrorStatement process(ErrorStatement item) {
        return item;
    }
}
