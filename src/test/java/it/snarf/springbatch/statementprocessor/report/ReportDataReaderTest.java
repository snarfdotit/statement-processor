package it.snarf.springbatch.statementprocessor.report;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportDataReaderTest {

    @Mock
    DataSource dataSource;

    @Test
    void testContructorWithOneCheck() {
        ReportDataReader one = new ReportDataReader(ValidationQuery.END_BALANCE);
        String sql = one.getBuilder().dataSource(dataSource).build().getSql();
        assertThat(sql).isEqualTo(ValidationQuery.END_BALANCE.getQuery());

        one = new ReportDataReader(ValidationQuery.DUPLICATE);
        sql = one.getBuilder().dataSource(dataSource).build().getSql();
        assertThat(sql).isEqualTo(ValidationQuery.DUPLICATE.getQuery());
    }

    @Test
    void testContructorWithTwoCheck() {
        ReportDataReader one = new ReportDataReader(ValidationQuery.END_BALANCE, ValidationQuery.DUPLICATE);
        String sql = one.getBuilder().dataSource(dataSource).build().getSql();
        assertThat(sql).isEqualTo(ValidationQuery.END_BALANCE.getQuery()+" union "+ValidationQuery.DUPLICATE.getQuery());
    }

}