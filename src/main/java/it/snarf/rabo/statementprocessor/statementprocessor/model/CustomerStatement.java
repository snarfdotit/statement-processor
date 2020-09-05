package it.snarf.rabo.statementprocessor.statementprocessor.model;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CustomerStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long referenceNumber;

    @NotNull
    private String iban;

    @NotNull
    private String description;

    @NotNull
    private BigDecimal startBalance;

    @NotNull
    private BigDecimal mutation;

    @NotNull
    private BigDecimal endBalance;

//    @ManyToOne
//    private StatementFile file;

}
