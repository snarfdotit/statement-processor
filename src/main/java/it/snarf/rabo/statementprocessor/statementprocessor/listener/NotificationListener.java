package it.snarf.rabo.statementprocessor.statementprocessor.listener;

import it.snarf.rabo.statementprocessor.statementprocessor.model.CustomerStatement;
import java.sql.ResultSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationListener extends JobExecutionListenerSupport {

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
        log.info("!!! JOB Started!");
        jobExecution.getExecutionContext().put("hello", 1L);
    }

    @Override
    public void afterJob(final JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            Long hello = jobExecution.getExecutionContext().getLong("hello");
            log.info("Hello: "+ hello);

//            jdbcTemplate.query("SELECT id, reference_number, iban, description, start_balance, mutation, end_balance FROM customer_statement",
//                (rs, row) -> new CustomerStatement(
//                    rs.getLong(1),
//                    rs.getLong(2),
//                    rs.getString(3),
//                    rs.getString(4),
//                    rs.getBigDecimal(5),
//                    rs.getBigDecimal(6),
//                    rs.getBigDecimal(7))
////                    ,
////                    rs.getString(8))
//            ).forEach(statement -> log.info("Found <" + statement + "> in the database."));
            //jdbcTemplate.execute("delete from customer_statement");
        }
    }
}
