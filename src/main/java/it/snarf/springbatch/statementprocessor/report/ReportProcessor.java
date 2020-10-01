package it.snarf.springbatch.statementprocessor.report;

import it.snarf.springbatch.statementprocessor.model.ErrorStatement;
import org.springframework.batch.item.ItemProcessor;

public class ReportProcessor implements ItemProcessor<ErrorStatement, ErrorStatement> {

    @Override
    public ErrorStatement process(ErrorStatement item) {
        return item;
    }
}
