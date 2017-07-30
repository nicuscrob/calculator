package com.example.calculator.service;

import com.example.calculator.model.Operation;
import org.mockito.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.testng.AssertJUnit.assertNotNull;

public class ComputationServiceTest {

    @Mock
    private OperationValidator validator;
    @InjectMocks
    private ComputationService victim;

    @BeforeTest
    public void init() {
        victim = new ComputationService();
        victim.setValidator(validator);
        MockitoAnnotations.initMocks(this);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void compute_ValidationFails_ThrowsException() {
        //given
        final Operation operation = Operation.builder()
                .operand(Operand.ADD)
                .left(BigDecimal.ONE)
                .right(BigDecimal.ONE)
                .build();
        Mockito.doThrow(new IllegalArgumentException()).when(validator).apply(operation);
        //when
        victim.compute(operation);
    }

    @Test
    public void compute_CorrectOperation_ShouldCompute() {
        //given
        final Operation operation = Operation.builder()
                .operand(Operand.ADD)
                .left(BigDecimal.ONE)
                .right(BigDecimal.ONE)
                .build();
        Mockito.doNothing().when(validator).apply(operation);
        //when
        BigDecimal result = victim.compute(operation);

        //then
        assertNotNull(result);

    }
}
