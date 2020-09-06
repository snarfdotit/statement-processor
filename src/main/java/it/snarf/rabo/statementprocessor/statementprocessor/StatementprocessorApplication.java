package it.snarf.rabo.statementprocessor.statementprocessor;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class StatementprocessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatementprocessorApplication.class, args);
    }

}
