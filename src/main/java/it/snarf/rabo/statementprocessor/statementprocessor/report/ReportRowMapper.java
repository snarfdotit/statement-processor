package it.snarf.rabo.statementprocessor.statementprocessor.report;

import it.snarf.rabo.statementprocessor.statementprocessor.model.ErrorStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class ReportRowMapper implements RowMapper<ErrorStatement> {

    @Override
    public ErrorStatement mapRow(ResultSet rs, int rowNum) throws SQLException {
        ErrorStatement error = ErrorStatement.builder()
            .referenceNumber(rs.getLong("reference_number"))
            .description(rs.getString("description"))
            .error(rs.getString("error"))
            .build();

        return error;
    }
}
