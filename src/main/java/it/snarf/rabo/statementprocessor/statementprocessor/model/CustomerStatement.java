package it.snarf.rabo.statementprocessor.statementprocessor.model;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "record")
@Entity
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @XmlAttribute(name="reference")
    @NotNull
    private Long referenceNumber;

    @XmlElement(name = "accountNumber")
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
}
