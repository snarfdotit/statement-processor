package it.snarf.springbatch.statementprocessor.report;

/**
 * This enum class functions as a placeholder for the validation queries.
 */
public enum ValidationQuery {
    DUPLICATE("select s.reference_number, s.description, 'DUB' as error, s.filename  from customer_statement s"
        +" inner join (select t.reference_number,  COUNT(*) count from customer_statement t group by t.reference_number having count > 2) as duplicates"
        + " on s.reference_number in(duplicates.reference_number)"),
    END_BALANCE("select reference_number, description, 'CALC' as error, filename  from customer_statement where (start_balance + mutation - end_balance != 0) ");

    private final String query;

    ValidationQuery(String query){
        this.query = query;
    }

    public String getQuery(){
        return query;
    }
}
