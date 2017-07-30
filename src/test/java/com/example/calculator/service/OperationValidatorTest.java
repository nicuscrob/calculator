package com.example.calculator.service;

import com.example.calculator.model.Operation;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;

public class OperationValidatorTest {

    private OperationValidator victim;

    @BeforeTest
    public void init(){
        victim = new OperationValidator();
    }

    @Test
    public void apply_ValidOperation_ShouldPass() {
        victim.apply(Operation.builder()
                .left(BigDecimal.ONE)
                .right(BigDecimal.ONE)
                .operand(Operand.ADD)
                .build());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void apply_NullOperand_ShouldFail() {
        victim.apply(Operation.builder().build());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void apply_DivisionByZero_ShouldFail() {
        victim.apply(Operation.builder()
                .left(BigDecimal.ONE)
                .right(BigDecimal.ZERO)
                .operand(Operand.DIVIDE)
                .build());
    }

}
