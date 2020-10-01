package it.snarf.springbatch.statementprocessor;

import org.springframework.batch.core.Job;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

//@TestConfiguration
public class IntegrationTestConfiguration {

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {

        return new JobLauncherTestUtils() {
            @Override
            @Autowired
            public void setJob(@Qualifier("importCsvStatementJob") Job job) {
                super.setJob(job);
            }
        };
    }

}
