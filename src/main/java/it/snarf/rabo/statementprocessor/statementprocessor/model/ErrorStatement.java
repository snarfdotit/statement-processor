package it.snarf.rabo.statementprocessor.statementprocessor.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorStatement {

    private Long referenceNumber;
    private String description;
    private String error;
    private String filename;
}
