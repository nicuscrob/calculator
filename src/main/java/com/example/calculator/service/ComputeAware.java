package com.example.calculator.service;

import java.math.BigDecimal;

public interface ComputeAware {

    default BigDecimal compute(BigDecimal left, BigDecimal right) {
        throw new UnsupportedOperationException("Compute is not implemented");
    }
}
