package com.example.calculator.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class OperationResult {

    private String id;

    private BigDecimal value;

    private boolean success;

    private String errorMessage;
}
