package com.example.calculator.service;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class OperandTest {

    @Test(dataProvider = "correctOperations")
    public void shouldCompute(BigDecimal left, BigDecimal right, Operand operand, BigDecimal result) {
        assertEquals(operand.compute(left,right), result);
    }

    @DataProvider(name = "correctOperations")
    public Object[][] correctOperations() {
        return new Object[][] {
            { BigDecimal.ONE, BigDecimal.ONE, Operand.ADD, BigDecimal.valueOf(2) },
            { BigDecimal.ONE, BigDecimal.ONE, Operand.SUBTRACT, BigDecimal.valueOf(0) },
            { BigDecimal.valueOf(9), BigDecimal.valueOf(3), Operand.DIVIDE, BigDecimal.valueOf(3) },
            { BigDecimal.valueOf(2), BigDecimal.valueOf(3), Operand.MULTIPLY, BigDecimal.valueOf(6) }
        };
    }

}
