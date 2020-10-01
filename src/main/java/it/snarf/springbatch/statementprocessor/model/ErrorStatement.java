package it.snarf.springbatch.statementprocessor.model;

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
