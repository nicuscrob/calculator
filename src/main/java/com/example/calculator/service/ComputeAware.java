package com.example.calculator.service;

import java.math.BigDecimal;

/**
 * Interface defining a stereotype for computing BigDecimals
 */
public interface ComputeAware {

    default BigDecimal compute(BigDecimal left, BigDecimal right) {
        throw new UnsupportedOperationException("Compute is not implemented");
    }
}
