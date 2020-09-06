package it.snarf.rabo.statementprocessor.statementprocessor.configuration;

import it.snarf.rabo.statementprocessor.statementprocessor.listener.NotificationListener;
import it.snarf.rabo.statementprocessor.statementprocessor.listener.ValidationStepExecutionListener;
import it.snarf.rabo.statementprocessor.statementprocessor.model.CustomerStatement;
import it.snarf.rabo.statementprocessor.statementprocessor.model.ErrorStatement;
import it.snarf.rabo.statementprocessor.statementprocessor.report.ReportDataReader;
import it.snarf.rabo.statementprocessor.statementprocessor.report.ReportProcessor;
import it.snarf.rabo.statementprocessor.statementprocessor.report.ReportRowMapper;
import it.snarf.rabo.statementprocessor.statementprocessor.report.ValidationQuery;
import it.snarf.rabo.statementprocessor.statementprocessor.statement.StatementFieldSetMapper;
import it.snarf.rabo.statementprocessor.statementprocessor.statement.StatementProcessor;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<CustomerStatement> cvsReader() {
        return new FlatFileItemReaderBuilder<CustomerStatement>()
                .name("statementCsvItemReader")
                .linesToSkip(1)
                .resource(new ClassPathResource("input/records.csv"))
                .delimited()
                .names("reference","iban", "description", "startBalance", "mutation", "endBalance")
                .lineMapper(lineMapper())
                .fieldSetMapper(new BeanWrapperFieldSetMapper<CustomerStatement>() {{
                    setTargetType(CustomerStatement.class);
                }})
                .build();
    }

    @Bean
    public StaxEventItemReader<CustomerStatement> xmlReader() {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(CustomerStatement.class);

        return new StaxEventItemReaderBuilder<CustomerStatement>()
            .name("xmlReader")
            .resource(new ClassPathResource("input/records.xml"))
            .addFragmentRootElements("record")
            .unmarshaller(unmarshaller)
            .build();
    }

    @Bean
    public LineMapper<CustomerStatement> lineMapper() {

        final DefaultLineMapper<CustomerStatement> defaultLineMapper = new DefaultLineMapper<>();
        final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("reference","iban", "description", "startBalance", "mutation", "endBalance");

        final StatementFieldSetMapper fieldSetMapper = new StatementFieldSetMapper();
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return defaultLineMapper;
    }

    @Bean
    public StatementProcessor csvProcessor() {
        return new StatementProcessor();
    }

    @Bean
    public StatementProcessor xmlProcessor() {
        return new StatementProcessor();
    }

    @Bean
    public ReportProcessor reportProcessor() { return new ReportProcessor(); }

    @Bean
    public JdbcBatchItemWriter<CustomerStatement> jdbcWriter(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CustomerStatement>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO customer_statement (reference_number, iban, description, start_balance, mutation, end_balance) VALUES (:referenceNumber, :iban, :description, :startBalance, :mutation, :endBalance)")
            .dataSource(dataSource)
            .build();
    }

    @Bean
    public Job importCsvStatementJob(NotificationListener listener, Step step1, Step report) {
        return jobBuilderFactory.get("importCsvStatementJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .next(report)
                .end()
                .build();
    }

    @Bean
    public Job importXmlStatementJob(NotificationListener listener, Step step2, Step report) {
        return jobBuilderFactory.get("importXmlStatementJob")
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .flow(step2)
            .next(report)
            .end()
            .build();
    }

    @Bean
    public Step step1(ValidationStepExecutionListener listener, JdbcBatchItemWriter<CustomerStatement> jdbcWriter) {
        return stepBuilderFactory.get("step1")
                .<CustomerStatement, CustomerStatement> chunk(5)
                .reader(cvsReader())
                .processor(csvProcessor())
                .writer(jdbcWriter)
                .listener(listener)
                .build();
    }

    @Bean
    public Step step2(ValidationStepExecutionListener listener, JdbcBatchItemWriter<CustomerStatement> jdbcWriter) {
        return stepBuilderFactory.get("step2")
            .<CustomerStatement, CustomerStatement> chunk(5)
            .reader(xmlReader())
            .processor(xmlProcessor())
            .writer(jdbcWriter)
            .listener(listener)
            .build();
    }

    @Bean
    public Step report(ValidationStepExecutionListener listener, JdbcCursorItemReader<ErrorStatement> reportReader) throws IOException {
        return stepBuilderFactory.get("report")
            .<ErrorStatement, ErrorStatement> chunk(5)
            .reader(reportReader)
            .processor(reportProcessor())
            .writer(errorJsonFileItemWriter())
            .listener(listener)
            .build();
    }

    @Bean
    public JsonFileItemWriter<ErrorStatement> errorJsonFileItemWriter() throws IOException {
        Path outputFilePath = Paths.get("output", "errors.json");
        Resource resource = new FileSystemResource(outputFilePath.toFile());


        return new JsonFileItemWriterBuilder<ErrorStatement>()
            .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
            .resource(resource)
            .name("errorJsonFileItemWriter")
            .build();
    }

    @Bean
    public JdbcCursorItemReader<ErrorStatement> reportReader(final DataSource dataSource) {
        ReportDataReader reader = new ReportDataReader(ValidationQuery.DUPLICATE, ValidationQuery.END_BALANCE);

        return reader.getBuilder()
            .name("reportReader")
            .rowMapper(new ReportRowMapper())
            .dataSource(dataSource)
            .build();
    }
}
