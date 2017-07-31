package com.example.calculator.service;

import com.example.calculator.model.Operation;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service responsible for computing operations
 */
@Service
public class ComputationService {

    @Autowired
    OperationValidator validator;

    /**
     * Validates and computes the operation
     * @param operation The operation to compute
     * @return Result of the operation as a BigDecimal
     */
    public BigDecimal compute(Operation operation) {
        validator.apply(operation);
        return operation.getOperand().compute(operation.getLeft(), operation.getRight());
    }

    @VisibleForTesting
    void setValidator(OperationValidator validator) {
        this.validator = validator;
    }
}
