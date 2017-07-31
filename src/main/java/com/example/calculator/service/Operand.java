package com.example.calculator.service;

import java.math.BigDecimal;

/**
 * Supported operations enumeration
 */
public enum Operand implements ComputeAware {

    ADD {
        @Override
        public BigDecimal compute(BigDecimal left, BigDecimal right) {
            return left.add(right);
        }
    },
    SUBTRACT {
        @Override
        public BigDecimal compute(BigDecimal left, BigDecimal right) {
            return left.subtract(right);
        }
    },
    MULTIPLY {
        @Override
        public BigDecimal compute(BigDecimal left, BigDecimal right) {
            return left.multiply(right);
        }
    },
    DIVIDE {
        @Override
        public BigDecimal compute(BigDecimal left, BigDecimal right) {
            return left.divide(right);
        }
    }
}
