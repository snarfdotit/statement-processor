package it.snarf.rabo.statementprocessor.statementprocessor.integration;

import static org.assertj.core.api.Assertions.assertThat;

import it.snarf.rabo.statementprocessor.statementprocessor.configuration.StatementProcessorConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.AssertFile;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;

//@SpringBootTest
////@ExtendWith(SpringExtension.class)
//@EnableAutoConfiguration
//@ContextConfiguration(classes = { StatementProcessorConfiguration.class})
//@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
//    DirtiesContextTestExecutionListener.class})
//@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBatchTest
//@RunWith(SpringRunner.class)
@ContextConfiguration(classes=StatementProcessorConfiguration.class)
class StatementProcessorIntegrationTest {

    private static final String TEST_INPUT = "input/records.csv";
    private static final String TEST_OUTPUT = "errorsCsv.json";


    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    @Qualifier("importCsvStatementJob")
    private Job job;

//    @Autowired
//    private JobLauncher jobLauncher;
//
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    //@Qualifier(value = "myJobLauncherTestUtils")
    private JobLauncherTestUtils myJobLauncherTestUtils;

//    private void initailizeJobLauncherTestUtils() {
//        this.jobLauncherTestUtils = new JobLauncherTestUtils();
//        this.jobLauncherTestUtils.setJobLauncher(jobLauncher);
//        this.jobLauncherTestUtils.setJobRepository(jobRepository);
//        this.jobLauncherTestUtils.setJob(importCsvStatementJob);
//    }
//
    @BeforeEach
    public void setUp() throws Exception {
        myJobLauncherTestUtils.setJob(job);
    }

    @AfterEach
    public void cleanUp() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString("file.input", TEST_INPUT);
        paramsBuilder.addString("file.output", TEST_OUTPUT);
        return paramsBuilder.toJobParameters();
    }

    //@Test
    void givenReferenceOutput_whenJobExecuted_thenSuccess() throws Exception {
        // given
        FileSystemResource expectedResult = new FileSystemResource("errorsCsv_check.json");
        FileSystemResource actualResult = new FileSystemResource(TEST_OUTPUT);

        // when
        JobExecution jobExecution = myJobLauncherTestUtils.launchJob(defaultJobParameters());
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        // then
        assertThat(actualJobInstance.getJobName()).isEqualTo("transformBooksRecords");
        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
        AssertFile.assertFileEquals(expectedResult, actualResult);
    }

//    @Test
//    public void givenReferenceOutput_whenStep1Executed_thenSuccess() throws Exception {
//        // given
//        FileSystemResource expectedResult = new FileSystemResource(EXPECTED_OUTPUT);
//        FileSystemResource actualResult = new FileSystemResource(TEST_OUTPUT);
//
//        // when
//        JobExecution jobExecution = jobLauncherTestUtils.launchStep(
//            "step1", defaultJobParameters());
//        Collection actualStepExecutions = jobExecution.getStepExecutions();
//        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();
//
//        // then
//        assertThat(actualStepExecutions.size()).isEqualTo(1);
//        assertThat(actualJobExitStatus.getExitCode()).isEqualTo("COMPLETED");
//        AssertFile.assertFileEquals(expectedResult, actualResult);
//    }

//    @Configuration
//    static class MyTestConfiguration {
//        @Bean
//        public JobLauncherTestUtils myJobLauncherTestUtils() {
//            return new JobLauncherTestUtils() {
//                @Override
//                @Autowired
//                public void setJob(@Qualifier("importCsvStatementJob") Job job) {
//                    super.setJob(job);
//                }
//            };
//        }
//    }

}
