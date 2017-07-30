package com.example.calculator.service;

import com.example.calculator.model.Operation;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class ComputationService {

    @Autowired
    OperationValidator validator;

    public BigDecimal compute(Operation operation) {
        validator.apply(operation);
        return operation.getOperand().compute(operation.getLeft(), operation.getRight());
    }

    @VisibleForTesting
    void setValidator(OperationValidator validator) {
        this.validator = validator;
    }
}
