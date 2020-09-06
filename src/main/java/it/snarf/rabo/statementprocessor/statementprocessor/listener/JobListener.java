package it.snarf.rabo.statementprocessor.statementprocessor.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobListener extends JobExecutionListenerSupport {

    private final JdbcTemplate jdbcTemplate;
    private final boolean clearDB;

    @Autowired
    public JobListener(final JdbcTemplate jdbcTemplate, @Value("${app.clear-db}") boolean clearDB) {
        this.jdbcTemplate = jdbcTemplate;
        this.clearDB = clearDB;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("!!! JOB Started!");
    }

    @Override
    public void afterJob(final JobExecution jobExecution) {
        if(clearDB) {
            log.info("removed data");
            jdbcTemplate.execute("delete from customer_statement");
        }
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");
        }
    }
}
