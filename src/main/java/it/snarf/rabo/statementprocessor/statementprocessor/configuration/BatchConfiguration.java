package it.snarf.rabo.statementprocessor.statementprocessor.configuration;

import it.snarf.rabo.statementprocessor.statementprocessor.listener.ImportStepExecutionListener;
import it.snarf.rabo.statementprocessor.statementprocessor.listener.JobListener;
import it.snarf.rabo.statementprocessor.statementprocessor.listener.ReportStepExecutionListener;
import it.snarf.rabo.statementprocessor.statementprocessor.model.CustomerStatement;
import it.snarf.rabo.statementprocessor.statementprocessor.model.ErrorStatement;
import it.snarf.rabo.statementprocessor.statementprocessor.report.ReportDataReader;
import it.snarf.rabo.statementprocessor.statementprocessor.report.ReportProcessor;
import it.snarf.rabo.statementprocessor.statementprocessor.report.ReportRowMapper;
import it.snarf.rabo.statementprocessor.statementprocessor.report.ValidationQuery;
import it.snarf.rabo.statementprocessor.statementprocessor.statement.StatementFieldSetMapper;
import it.snarf.rabo.statementprocessor.statementprocessor.statement.StatementProcessor;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    public Job importCsvStatementJob(JobListener listener, Step importCsvStep, Step report) {
        return jobBuilderFactory.get("importCsvStatementJob")
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .flow(importCsvStep)
            .next(report)
            .end()
            .build();
    }

    @Bean
    public Job importXmlStatementJob(JobListener listener, Step importXmlStep, Step report) {
        return jobBuilderFactory.get("importXmlStatementJob")
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .flow(importXmlStep)
            .next(report)
            .end()
            .build();
    }

    @StepScope
    @Bean
    public MultiResourceItemReader<CustomerStatement> csvResourceReader(
        @Value("#{jobParameters['file.input']}") String inputFile) {
        return new MultiResourceItemReaderBuilder<CustomerStatement>()
            .name("csvResourceReader")
            .delegate(cvsReader())
            .resources(new ClassPathResource(inputFile))
            .build();
    }

    public FlatFileItemReader<CustomerStatement> cvsReader() {
        return new FlatFileItemReaderBuilder<CustomerStatement>()
                .name("statementCsvItemReader")
                .linesToSkip(1)
                .delimited()
                .names("reference","iban", "description", "startBalance", "mutation", "endBalance")
                .lineMapper(lineMapper())
                .fieldSetMapper(new BeanWrapperFieldSetMapper<CustomerStatement>() {{
                    setTargetType(CustomerStatement.class);
                }})
                .build();
    }

    @StepScope
    @Bean
    public MultiResourceItemReader<CustomerStatement> xmlResourceReader(
        @Value("#{jobParameters['file.input']}") String inputFile) {
        return new MultiResourceItemReaderBuilder<CustomerStatement>()
            .name("xmlResourceReader")
            .delegate(xmlReader(inputFile))
            .resources(new ClassPathResource(inputFile))
            .build();
    }

    public StaxEventItemReader<CustomerStatement> xmlReader(String inputFile) {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(CustomerStatement.class);

        return new StaxEventItemReaderBuilder<CustomerStatement>()
            .name("xmlReader")
            .resource(new ClassPathResource(inputFile))
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
    public ReportProcessor reportProcessor() {
        return new ReportProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<CustomerStatement> jdbcWriter(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CustomerStatement>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO customer_statement (reference_number, iban, description, start_balance, mutation, end_balance, filename) VALUES (:referenceNumber, :iban, :description, :startBalance, :mutation, :endBalance, :filename)")
            .dataSource(dataSource)
            .build();
    }

    @Bean
    public Step importCsvStep(ImportStepExecutionListener listener, JdbcBatchItemWriter<CustomerStatement> jdbcWriter,
        MultiResourceItemReader<CustomerStatement> csvResourceReader) {
        return stepBuilderFactory.get("importCsvStep")
                .<CustomerStatement, CustomerStatement> chunk(5)
                .reader(csvResourceReader)
                .processor(csvProcessor())
                .writer(jdbcWriter)
                .listener(listener)
                .build();
    }

    @Bean
    public Step importXmlStep(ImportStepExecutionListener listener, JdbcBatchItemWriter<CustomerStatement> jdbcWriter,
        MultiResourceItemReader<CustomerStatement> xmlResourceReader) {
        return stepBuilderFactory.get("importXmlStep")
            .<CustomerStatement, CustomerStatement> chunk(5)
            .reader(xmlResourceReader)
            .processor(xmlProcessor())
            .writer(jdbcWriter)
            .listener(listener)
            .build();
    }

    @Bean
    public Step report(ReportStepExecutionListener listener, JdbcCursorItemReader<ErrorStatement> reportReader,
        JsonFileItemWriter<ErrorStatement> errorJsonFileItemWriter) {
        return stepBuilderFactory.get("report")
            .<ErrorStatement, ErrorStatement> chunk(5)
            .reader(reportReader)
            .processor(reportProcessor())
            .writer(errorJsonFileItemWriter)
            .listener(listener)
            .build();
    }
    @StepScope
    @Bean
    public JsonFileItemWriter<ErrorStatement> errorJsonFileItemWriter(
        @Value("#{jobParameters['file.output']}") String outputFile) {

        Path outputFilePath = Paths.get("output", outputFile);
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
