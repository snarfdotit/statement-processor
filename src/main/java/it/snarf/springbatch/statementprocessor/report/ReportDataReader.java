package it.snarf.springbatch.statementprocessor.report;

import it.snarf.springbatch.statementprocessor.model.ErrorStatement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;

public class ReportDataReader {

    private final List<String> validationsQueries;

    public ReportDataReader(ValidationQuery... validations) {
        validationsQueries = Arrays.stream(validations).map(ValidationQuery::getQuery).collect(Collectors.toList());
    }

    public JdbcCursorItemReaderBuilder<ErrorStatement> getBuilder() {
        return new JdbcCursorItemReaderBuilder<ErrorStatement>()
            .name("reportReader")
            .rowMapper(new ReportRowMapper())
            .sql(createQuery());
    }

    private String createQuery() {
        return String.join(" union ", validationsQueries);
    }
}
