package it.snarf.springbatch.statementprocessor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class StatementprocessorApplication implements CommandLineRunner {

    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job importCsvStatementJob;
    @Autowired
    private Job importXmlStatementJob;

    @Value("${file.csv.input}")
    private String inputCsv;
    @Value("${file.csv.output}")
    private String errorCsv;

    @Value("${file.xml.input}")
    private String inputXml;
    @Value("${file.xml.output}")
    private String errorXml;


    public static void main(String[] args) {
        SpringApplication.run(StatementprocessorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("file.input", inputCsv);
        paramsBuilder.addString("file.output", errorCsv);
        jobLauncher.run(importCsvStatementJob, paramsBuilder.toJobParameters());

        paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("file.input", inputXml);
        paramsBuilder.addString("file.output", errorXml);
        jobLauncher.run(importXmlStatementJob, paramsBuilder.toJobParameters());
    }

}
