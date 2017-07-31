package com.example.calculator.service;

import com.example.calculator.model.Operation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static java.util.Optional.ofNullable;
import static org.springframework.util.Assert.notNull;

/**
 * Class responsible for validating an operation
 */
@Component
public class OperationValidator {

    private final String OPERAND_NOT_NULL = "Operand must not be null";
    private final String DIVISION_BY_ZERO = "Division by zero";

    void apply(final Operation operation){
        notNull(operation.getLeft(), OPERAND_NOT_NULL);
        notNull(operation.getRight(), OPERAND_NOT_NULL);
        ofNullable(operation)
                .filter(op -> Operand.DIVIDE.equals(op.getOperand()))
                .map(Operation::getRight)
                .filter(BigDecimal.ZERO::equals)
                .ifPresent(r -> {
                    throw new IllegalArgumentException(DIVISION_BY_ZERO);
                });
    }
}
