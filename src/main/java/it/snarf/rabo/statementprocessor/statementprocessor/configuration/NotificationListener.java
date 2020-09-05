package it.snarf.rabo.statementprocessor.statementprocessor.configuration;

import it.snarf.rabo.statementprocessor.statementprocessor.model.CustomerStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener extends JobExecutionListenerSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public NotificationListener(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /* (non-Javadoc)
     * @see org.springframework.batch.core.domain.JobListener#beforeJob(org.springframework.batch.core.domain.JobExecution)
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
    }

    @Override
    public void afterJob(final JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            LOGGER.info("!!! JOB FINISHED! Time to verify the results");

            jdbcTemplate.query("SELECT id, reference_number, iban, description, start_balance, mutation, end_balance FROM customer_statement",
                (rs, row) -> new CustomerStatement(
                    rs.getLong(1),
                    rs.getLong(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getBigDecimal(5),
                    rs.getBigDecimal(6),
                    rs.getBigDecimal(7))
            ).forEach(statement -> LOGGER.info("Found <" + statement + "> in the database."));
        }
    }
}
