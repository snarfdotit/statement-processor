package it.snarf.springbatch.statementprocessor.repository;

import it.snarf.springbatch.statementprocessor.model.CustomerStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerStatementRepository extends JpaRepository<CustomerStatement, Long> {

}
