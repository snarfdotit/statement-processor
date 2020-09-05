package it.snarf.rabo.statementprocessor.statementprocessor.configuration;

import it.snarf.rabo.statementprocessor.statementprocessor.model.CustomerStatement;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

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
                .names(new String[]{"reference","iban", "description", "startBalance", "mutation", "endBalance"})
                .lineMapper(lineMapper())
                .fieldSetMapper(new BeanWrapperFieldSetMapper<CustomerStatement>() {{
                    setTargetType(CustomerStatement.class);
                }})
                .build();
    }

    @Bean
    public LineMapper<CustomerStatement> lineMapper() {

        final DefaultLineMapper<CustomerStatement> defaultLineMapper = new DefaultLineMapper<>();
        final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(new String[] {"reference","iban", "description", "startBalance", "mutation", "endBalance"});

        final StatementFieldSetMapper fieldSetMapper = new StatementFieldSetMapper();
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return defaultLineMapper;
    }

    @Bean
    public StatementProcessor processor() {
        return new StatementProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<CustomerStatement> writer(final DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<CustomerStatement>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO customer_statement (reference_number, iban, description, start_balance, mutation, end_balance) VALUES (:referenceNumber, :iban, :description, :startBalance, :mutation, :endBalance)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importStatementJob(NotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importStatementJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<CustomerStatement> writer) {
        return stepBuilderFactory.get("step1")
                .<CustomerStatement, CustomerStatement> chunk(10)
                .reader(cvsReader())
                .processor(processor())
                .writer(writer)
                .build();
    }
}
